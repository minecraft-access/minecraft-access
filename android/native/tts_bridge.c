/*
 * minecraft-access - Android text to speech bridge (native half)
 *
 * The mod runs inside the OpenJDK JVM that the launcher started, while
 * android.speech.tts.TextToSpeech only exists inside the Android runtime (ART)
 * of the same process. This library closes that gap:
 *
 *   1. it finds ART's JavaVM (libart.so exports JNI_GetCreatedJavaVMs, with the
 *      DALVIK_JAVAVM environment variable as a fallback for launchers that
 *      inject it),
 *   2. it obtains an Android Context (ActivityThread.currentApplication(), with
 *      DALVIK_APPLICATION as a fallback),
 *   3. it loads the small bridge class that is shipped next to this library as
 *      a dex (InMemoryDexClassLoader, Android 8.0+) and
 *   4. it forwards narration, rate, pitch, interruption and state queries to it.
 *
 * The threading model (looper thread inside ART) and the speech semantics live
 * in that bridge class, which is a port of prism's AndroidTextToSpeechBackend.
 */
#include <jni.h>
#include <android/log.h>
#include <dlfcn.h>
#include <pthread.h>
#include <stdarg.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define LOG_TAG "mcaccess-tts"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGW(...) __android_log_print(ANDROID_LOG_WARN, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define BRIDGE_CLASS_NAME "org/mcaccess/minecraftaccess/android/AndroidTtsBridge"
#define STARTUP_SCRATCH_BYTES 2048
#define ERROR_MESSAGE_BYTES 512

typedef jint (*get_created_vms_fn)(JavaVM **vms, jsize buffer_length, jsize *vm_count);

static JavaVM *art_vm;
static jobject bridge_class;
static jmethodID mid_start;
static jmethodID mid_speak;
static jmethodID mid_stop;
static jmethodID mid_is_speaking;
static jmethodID mid_shutdown;
static jmethodID mid_last_error;
static void *dex_copy;
static int bridge_ready;
static char error_message[ERROR_MESSAGE_BYTES];
static pthread_mutex_t callback_lock = PTHREAD_MUTEX_INITIALIZER;

static void set_error(const char *format, ...) {
    va_list args;
    va_start(args, format);
    pthread_mutex_lock(&callback_lock);
    vsnprintf(error_message, sizeof(error_message), format, args);
    pthread_mutex_unlock(&callback_lock);
    va_end(args);
    LOGE("%s", error_message);
}

static JavaVM *find_art_vm(void) {
    void *libart = dlopen("libart.so", RTLD_NOW);
    if (libart == NULL) {
        libart = dlopen("libart.so", RTLD_LAZY);
    }
    if (libart != NULL) {
        get_created_vms_fn get_created_vms =
                (get_created_vms_fn) dlsym(libart, "JNI_GetCreatedJavaVMs");
        if (get_created_vms != NULL) {
            JavaVM *vms[8];
            jsize count = 0;
            if (get_created_vms(vms, 8, &count) == JNI_OK && count > 0) {
                LOGI("using the Android runtime VM reported by libart.so");
                return vms[0];
            }
            LOGW("libart.so reported no running VM");
        } else {
            LOGW("libart.so does not export JNI_GetCreatedJavaVMs");
        }
    } else {
        LOGW("libart.so is not loadable from this process");
    }
    const char *injected = getenv("DALVIK_JAVAVM");
    if (injected != NULL && injected[0] != '\0') {
        JavaVM *vm = (JavaVM *) (uintptr_t) strtoull(injected, NULL, 0);
        if (vm != NULL) {
            LOGI("using the VM pointer injected by the launcher");
            return vm;
        }
    }
    return NULL;
}

static JNIEnv *android_env(void) {
    JNIEnv *env = NULL;
    if (art_vm == NULL) {
        return NULL;
    }
    jint status = (*art_vm)->GetEnv(art_vm, (void **) &env, JNI_VERSION_1_6);
    if (status == JNI_OK) {
        return env;
    }
    if (status != JNI_EDETACHED) {
        set_error("GetEnv on the Android runtime failed: %d", (int) status);
        return NULL;
    }
    if ((*art_vm)->AttachCurrentThreadAsDaemon(art_vm, &env, NULL) != JNI_OK) {
        set_error("unable to attach the game thread to the Android runtime");
        return NULL;
    }
    return env;
}

static jobject android_context(JNIEnv *env) {
    jclass activity_thread = (*env)->FindClass(env, "android/app/ActivityThread");
    if (activity_thread != NULL) {
        jmethodID current_application = (*env)->GetStaticMethodID(
                env, activity_thread, "currentApplication", "()Landroid/app/Application;");
        if (current_application != NULL) {
            jobject application = (*env)->CallStaticObjectMethod(env, activity_thread, current_application);
            if ((*env)->ExceptionCheck(env)) {
                (*env)->ExceptionClear(env);
                application = NULL;
            }
            if (application != NULL) {
                LOGI("using ActivityThread.currentApplication() as the Android context");
                return application;
            }
        } else if ((*env)->ExceptionCheck(env)) {
            (*env)->ExceptionClear(env);
        }
        (*env)->DeleteLocalRef(env, activity_thread);
    } else if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionClear(env);
    }
    const char *injected = getenv("DALVIK_APPLICATION");
    if (injected != NULL && injected[0] != '\0') {
        jobject global = (jobject) (uintptr_t) strtoull(injected, NULL, 0);
        if (global != NULL) {
            jobject local = (*env)->NewLocalRef(env, global);
            if (local != NULL) {
                LOGI("using the Android context injected by the launcher");
                return local;
            }
        }
    }
    return NULL;
}

static void capture_bridge_error(JNIEnv *env) {
    if (mid_last_error == NULL || bridge_class == NULL) {
        return;
    }
    jstring message = (jstring) (*env)->CallStaticObjectMethod(env, bridge_class, mid_last_error);
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionClear(env);
        return;
    }
    if (message == NULL) {
        return;
    }
    const char *utf = (*env)->GetStringUTFChars(env, message, NULL);
    if (utf != NULL) {
        set_error("bridge reported: %s", utf);
        (*env)->ReleaseStringUTFChars(env, message, utf);
    }
}

static int load_bridge(JNIEnv *env, const jbyte *dex, jsize length, jobject context) {
    void *copy = malloc((size_t) length);
    if (copy == NULL) {
        set_error("out of memory copying the bridge dex");
        return 0;
    }
    memcpy(copy, dex, (size_t) length);
    jobject buffer = (*env)->NewDirectByteBuffer(env, copy, (jlong) length);
    if (buffer == NULL) {
        free(copy);
        set_error("NewDirectByteBuffer failed for the bridge dex");
        return 0;
    }

    jclass context_class = (*env)->GetObjectClass(env, context);
    jmethodID get_class_loader = (*env)->GetMethodID(
            env, context_class, "getClassLoader", "()Ljava/lang/ClassLoader;");
    jobject parent = NULL;
    if (get_class_loader != NULL) {
        parent = (*env)->CallObjectMethod(env, context, get_class_loader);
    }
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionClear(env);
        parent = NULL;
    }

    jclass loader_class = (*env)->FindClass(env, "dalvik/system/InMemoryDexClassLoader");
    if (loader_class == NULL) {
        (*env)->ExceptionClear(env);
        set_error("InMemoryDexClassLoader is missing, Android 8.0 or newer is required");
        return 0;
    }
    jmethodID constructor = (*env)->GetMethodID(
            env, loader_class, "<init>", "(Ljava/nio/ByteBuffer;Ljava/lang/ClassLoader;)V");
    if (constructor == NULL) {
        (*env)->ExceptionClear(env);
        set_error("InMemoryDexClassLoader(ByteBuffer, ClassLoader) is missing");
        return 0;
    }
    jobject loader = (*env)->NewObject(env, loader_class, constructor, buffer, parent);
    if ((*env)->ExceptionCheck(env) || loader == NULL) {
        (*env)->ExceptionClear(env);
        set_error("creating the InMemoryDexClassLoader failed");
        return 0;
    }

    jclass loader_base = (*env)->FindClass(env, "java/lang/ClassLoader");
    jmethodID load_class = (*env)->GetMethodID(
            env, loader_base, "loadClass", "(Ljava/lang/String;)Ljava/lang/Class;");
    if (load_class == NULL) {
        (*env)->ExceptionClear(env);
        set_error("ClassLoader.loadClass is missing");
        return 0;
    }
    jstring class_name = (*env)->NewStringUTF(env, BRIDGE_CLASS_NAME);
    jobject loaded = (*env)->CallObjectMethod(env, loader, load_class, class_name);
    if ((*env)->ExceptionCheck(env) || loaded == NULL) {
        (*env)->ExceptionClear(env);
        set_error("the bridge class %s was not found in the dex", BRIDGE_CLASS_NAME);
        return 0;
    }
    jclass bridge = (jclass) loaded;

    mid_start = (*env)->GetStaticMethodID(
            env, bridge, "start", "(Landroid/content/Context;Ljava/lang/String;)Z");
    mid_speak = (*env)->GetStaticMethodID(env, bridge, "speak", "([BFFZ)I");
    mid_stop = (*env)->GetStaticMethodID(env, bridge, "stop", "()V");
    mid_is_speaking = (*env)->GetStaticMethodID(env, bridge, "isSpeaking", "()Z");
    mid_shutdown = (*env)->GetStaticMethodID(env, bridge, "shutdown", "()V");
    mid_last_error = (*env)->GetStaticMethodID(env, bridge, "lastError", "()Ljava/lang/String;");
    if ((*env)->ExceptionCheck(env)) {
        (*env)->ExceptionClear(env);
        set_error("the bridge class is missing one of its entry points");
        return 0;
    }

    bridge_class = (*env)->NewGlobalRef(env, bridge);
    if (bridge_class == NULL) {
        set_error("unable to keep the bridge class alive");
        return 0;
    }
    dex_copy = copy;
    bridge_ready = 1;
    LOGI("Android text to speech bridge loaded");
    return 1;
}

JNIEXPORT jint JNICALL
Java_org_mcaccess_minecraftaccess_screen_reader_android_AndroidTtsNative_startEngine(
        JNIEnv *env, jclass clazz, jbyteArray dex, jstring language_tag) {
    (void) clazz;
    pthread_mutex_lock(&callback_lock);
    if (bridge_ready) {
        pthread_mutex_unlock(&callback_lock);
        return 1;
    }
    pthread_mutex_unlock(&callback_lock);

    if (dex == NULL) {
        set_error("no bridge dex was given");
        return 0;
    }
    jsize length = (*env)->GetArrayLength(env, dex);
    if (length <= 0) {
        set_error("the bridge dex is empty");
        return 0;
    }
    jbyte *bytes = malloc((size_t) length);
    if (bytes == NULL) {
        set_error("out of memory reading the bridge dex");
        return 0;
    }
    (*env)->GetByteArrayRegion(env, dex, 0, length, bytes);

    if (art_vm == NULL) {
        art_vm = find_art_vm();
    }
    if (art_vm == NULL) {
        free(bytes);
        set_error("no Android runtime found in this process");
        return 0;
    }
    JNIEnv *android = android_env();
    if (android == NULL) {
        free(bytes);
        return 0;
    }
    jobject context = android_context(android);
    if (context == NULL) {
        free(bytes);
        set_error("unable to obtain an Android context");
        return 0;
    }
    if (!load_bridge(android, bytes, length, context)) {
        (*android)->DeleteLocalRef(android, context);
        free(bytes);
        return 0;
    }
    free(bytes);

    jstring android_tag = NULL;
    if (language_tag != NULL) {
        const char *utf = (*env)->GetStringUTFChars(env, language_tag, NULL);
        if (utf != NULL) {
            android_tag = (*android)->NewStringUTF(android, utf);
            (*env)->ReleaseStringUTFChars(env, language_tag, utf);
        }
    }
    jboolean started = (*android)->CallStaticBooleanMethod(
            android, bridge_class, mid_start, context, android_tag);
    if ((*android)->ExceptionCheck(android)) {
        (*android)->ExceptionClear(android);
        set_error("the bridge start() call threw");
        started = JNI_FALSE;
    }
    if (!started) {
        capture_bridge_error(android);
    }
    (*android)->DeleteLocalRef(android, context);
    if (android_tag != NULL) {
        (*android)->DeleteLocalRef(android, android_tag);
    }
    return started == JNI_TRUE ? 1 : 0;
}

JNIEXPORT jint JNICALL
Java_org_mcaccess_minecraftaccess_screen_reader_android_AndroidTtsNative_speak(
        JNIEnv *env, jclass clazz, jbyteArray utf8, jfloat rate, jfloat pitch, jboolean interrupt) {
    (void) clazz;
    if (!bridge_ready) {
        set_error("speech requested before the bridge was started");
        return -1;
    }
    JNIEnv *android = android_env();
    if (android == NULL) {
        return -1;
    }
    jsize length = (*env)->GetArrayLength(env, utf8);
    if (length <= 0) {
        return -1;
    }
    jbyte scratch[STARTUP_SCRATCH_BYTES];
    jbyte *bytes = length <= (jsize) STARTUP_SCRATCH_BYTES
            ? scratch
            : (jbyte *) malloc((size_t) length);
    if (bytes == NULL) {
        set_error("out of memory copying the narration text");
        return -1;
    }
    (*env)->GetByteArrayRegion(env, utf8, 0, length, bytes);
    jbyteArray android_text = (*android)->NewByteArray(android, length);
    if (android_text == NULL) {
        if (bytes != scratch) {
            free(bytes);
        }
        set_error("unable to allocate the narration text inside the Android runtime");
        return -1;
    }
    (*android)->SetByteArrayRegion(android, android_text, 0, length, bytes);
    if (bytes != scratch) {
        free(bytes);
    }
    jint result = (*android)->CallStaticIntMethod(
            android, bridge_class, mid_speak, android_text, rate, pitch, interrupt);
    if ((*android)->ExceptionCheck(android)) {
        (*android)->ExceptionClear(android);
        set_error("the bridge speak() call threw");
        result = -1;
    }
    (*android)->DeleteLocalRef(android, android_text);
    return result;
}

JNIEXPORT void JNICALL
Java_org_mcaccess_minecraftaccess_screen_reader_android_AndroidTtsNative_stopSpeaking(
        JNIEnv *env, jclass clazz) {
    (void) clazz;
    if (!bridge_ready) {
        return;
    }
    JNIEnv *android = android_env();
    if (android == NULL) {
        return;
    }
    (*android)->CallStaticVoidMethod(android, bridge_class, mid_stop);
    if ((*android)->ExceptionCheck(android)) {
        (*android)->ExceptionClear(android);
        set_error("the bridge stop() call threw");
    }
}

JNIEXPORT jboolean JNICALL
Java_org_mcaccess_minecraftaccess_screen_reader_android_AndroidTtsNative_isSpeaking(
        JNIEnv *env, jclass clazz) {
    (void) clazz;
    (void) env;
    if (!bridge_ready) {
        return JNI_FALSE;
    }
    JNIEnv *android = android_env();
    if (android == NULL) {
        return JNI_FALSE;
    }
    jboolean speaking = (*android)->CallStaticBooleanMethod(android, bridge_class, mid_is_speaking);
    if ((*android)->ExceptionCheck(android)) {
        (*android)->ExceptionClear(android);
        return JNI_FALSE;
    }
    return speaking;
}

JNIEXPORT void JNICALL
Java_org_mcaccess_minecraftaccess_screen_reader_android_AndroidTtsNative_shutdownEngine(
        JNIEnv *env, jclass clazz) {
    (void) clazz;
    (void) env;
    if (!bridge_ready) {
        return;
    }
    JNIEnv *android = android_env();
    if (android == NULL) {
        return;
    }
    (*android)->CallStaticVoidMethod(android, bridge_class, mid_shutdown);
    if ((*android)->ExceptionCheck(android)) {
        (*android)->ExceptionClear(android);
        set_error("the bridge shutdown() call threw");
    }
}

JNIEXPORT jstring JNICALL
Java_org_mcaccess_minecraftaccess_screen_reader_android_AndroidTtsNative_lastError(
        JNIEnv *env, jclass clazz) {
    (void) clazz;
    pthread_mutex_lock(&callback_lock);
    char snapshot[ERROR_MESSAGE_BYTES];
    snprintf(snapshot, sizeof(snapshot), "%s", error_message);
    pthread_mutex_unlock(&callback_lock);
    return (*env)->NewStringUTF(env, snapshot);
}
