# Android speech output

Minecraft Access speaks through the text to speech engine of Android itself when
the game runs inside an Android launcher. The code here is the bridge between
the two runtimes that share the process:

```
Minecraft thread (OpenJDK JVM)          Android runtime (ART)
  ScreenReaderAndroid                     AndroidTtsBridge  (this folder, dex)
    -> AndroidTtsNative  -- JNI -->         -> TextToSpeech
       libmcaccess_android_tts.so           -> UtteranceProgressListener
                                            -> TextChunker
```

The launcher does not have to be modified: the mod finds everything it needs on
its own. The two pieces of software that are shipped inside the mod jar are
built from this folder and committed, so a normal Gradle build of the mod stays
self contained:

| Source | Produces |
| --- | --- |
| `native/tts_bridge.c` | `src/main/resources/mcaccess/android/<abi>/libmcaccess_android_tts.so` |
| `art/src/**/*.java` | `src/main/resources/mcaccess/android/mcaccess_android_tts.dex` |

## How the runtimes are connected

The game JVM and ART both live in the launcher's process, which is why the
bridge can work at all:

1. `tts_bridge.c` opens `libart.so` and asks it for the running `JavaVM` through
   `JNI_GetCreatedJavaVMs`. If that fails, the `DALVIK_JAVAVM` environment
   variable is used (several launchers inject it).
2. The game thread is attached to ART with `AttachCurrentThreadAsDaemon`, so the
   JNI environment stays valid for the life of the session.
3. An Android `Context` comes from `ActivityThread.currentApplication()`; the
   `DALVIK_APPLICATION` environment variable is the fallback.
4. The dex is loaded with `InMemoryDexClassLoader` on top of the launcher's own
   class loader, which needs Android 8.0 (API 26) or newer.
5. Narration, rate, pitch and interruption are forwarded to `AndroidTtsBridge`,
   which owns the `TextToSpeech` object on its own looper thread - the game
   thread has no `Looper`, so callbacks would never arrive there.

Failures never throw into the game: the bridge records the reason, the mod logs
it, and narration is silently disabled.

## Speech settings

The `speechSettings` category of the mod configuration has two Android only
sliders, both in percent:

* **Android speech rate** (`androidSpeechRate`, default 50)
* **Android speech pitch** (`androidSpeechPitch`, default 50)

50 means the natural speed and the natural pitch of the selected voice. The
values are mapped with the exponential curve from prism, so the middle of the
slider is the natural voice and both halves are usable: rate `0.1x` to `6x`,
pitch `0.25x` to `4x`.

The voice follows the language the game is set to (`zh_cn` becomes `zh-CN`); if
the device has no voice for it, the engine falls back to its default language.

## Rebuilding

```sh
export ANDROID_NDK_HOME=/path/to/android-ndk-r25+
export ANDROID_HOME=/path/to/android-sdk
android/build.sh
```

The script compiles the library for `arm64-v8a`, `armeabi-v7a` and `x86_64`,
compiles the Java sources against the newest installed `android.jar` and
converts them with `d8 --min-api 26`. Both artifacts are committed because the
mod build itself has no dependency on the Android toolchain.

## Licence

The Android backend of [prism](https://github.com/ethindp/prism) (MPL-2.0) was
the reference for all of it. `art/src/.../TextChunker.java` and
`art/src/.../Utils.java` are ports of prism's `TextChunker.java` and `utils.java`
and keep their MPL-2.0 header; `AndroidTtsBridge.java` reimplements the speech
semantics of prism's `AndroidTextToSpeechBackend.java` for this API. As with the
rest of the mod, the files are distributed under the GPL-3.0.
