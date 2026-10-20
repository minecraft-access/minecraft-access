package org.mcaccess.minecraftaccess.screen_reader.android;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

/**
 * Thin JNI binding to the Android text to speech engine.
 *
 * <p>The library only reaches the Android runtime; the speech logic and the
 * threading live in the dex that ships next to it (see {@code android/README.md}).
 * This class makes sure both files are on disk, loads the library and forwards
 * the calls.
 */
@Slf4j
public final class AndroidTtsNative {
    private static boolean available;
    private static boolean loaded;

    private AndroidTtsNative() {
    }

    /**
     * Loads the native library and starts the engine inside the Android runtime.
     *
     * @param languageTag the language the game is running in, for instance {@code zh-CN}
     * @return true when the engine accepted the request
     */
    public static synchronized boolean start(@Nullable String languageTag) {
        if (available) return true;
        if (!loaded && !load()) return false;

        Path dex = AndroidPlatform.extractDex();
        if (dex == null) return false;
        byte[] dexBytes;
        try {
            dexBytes = Files.readAllBytes(dex);
        } catch (IOException e) {
            log.error("Unable to read the Android text to speech bridge dex {}", dex, e);
            return false;
        }

        available = startEngine(dexBytes, languageTag) != 0;
        if (available) {
            log.info("Android text to speech engine started, language={}", languageTag);
        } else {
            log.error("Android text to speech engine failed to start: {}", lastError());
        }
        return available;
    }

    /**
     * Queues a narration.
     *
     * @param text the narration text
     * @param rate a 0..1 speech rate position, 0.5 is the natural speed
     * @param pitch a 0..1 pitch position, 0.5 is the natural pitch
     * @param interrupt whether speech that is still queued has to be dropped
     * @return true when the text was accepted
     */
    public static boolean narrate(String text, float rate, float pitch, boolean interrupt) {
        if (!available) return false;
        return speak(text.getBytes(StandardCharsets.UTF_8), rate, pitch, interrupt) == 0;
    }

    /** @return true when the engine is currently producing speech */
    public static boolean isSpeaking() {
        return available && stillSpeaking();
    }

    /** Drops everything the engine has queued. */
    public static void stop() {
        if (!available) return;
        stopSpeaking();
    }

    /** @return the last problem the bridge ran into */
    public static String error() {
        if (!loaded) return "the Android text to speech library is not loaded";
        return lastError();
    }

    /** Releases the engine. */
    public static synchronized void shutdown() {
        if (!available) return;
        shutdownEngine();
        available = false;
    }

    private static boolean load() {
        Path library = AndroidPlatform.extractLibrary();
        if (library == null) return false;
        try {
            System.load(library.toAbsolutePath().toString());
            loaded = true;
            log.info("Loaded the Android text to speech library from {}", library);
            return true;
        } catch (UnsatisfiedLinkError e) {
            log.error("Unable to load the Android text to speech library from {}", library, e);
            return false;
        }
    }

    private static native int startEngine(byte[] dex, @Nullable String languageTag);

    private static native int speak(byte[] utf8, float rate, float pitch, boolean interrupt);

    private static native void stopSpeaking();

    private static native boolean stillSpeaking();

    private static native void shutdownEngine();

    private static native String lastError();
}
