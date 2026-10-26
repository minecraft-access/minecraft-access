package org.mcaccess.minecraftaccess.screen_reader.android;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

/**
 * Thin JNI binding for speaking on Android.
 *
 * <p>The library only reaches the Android runtime; the speech logic and the
 * threading live in the dex that ships next to it (see {@code android/README.md}).
 * This class makes sure both files are on disk, loads the library and forwards
 * the calls.
 *
 * <p>Narration takes one of two paths. When a spoken accessibility service is
 * running, the text is handed to it as an announcement and the user keeps their
 * own voice, rate, pitch and language; that is prism's Android screen reader
 * backend. Otherwise the system text to speech engine synthesises the speech.
 */
@Slf4j
public final class AndroidTtsNative {
    private enum SpeechPath {
        NONE,
        SCREEN_READER,
        TEXT_TO_SPEECH
    }

    private static SpeechPath path = SpeechPath.NONE;
    private static boolean loaded;

    private AndroidTtsNative() {
    }

    /**
     * Loads the native library and picks the way narration reaches the user.
     *
     * @param languageTag the language the game is running in, for instance {@code zh-CN}
     * @return true when narration is ready
     */
    public static synchronized boolean start(@Nullable String languageTag) {
        if (path != SpeechPath.NONE) return true;
        if (!loaded && !load()) return false;

        byte[] dex = readDex();
        if (dex == null) return false;

        // A running screen reader already speaks with the user's own settings,
        // and a speech engine of our own would talk over it.
        if (systemScreenReaderAvailable(dex)) {
            path = SpeechPath.SCREEN_READER;
            log.info("Android narration goes through the system screen reader {}", screenReaderName());
            return true;
        }

        if (startEngine(dex, languageTag) != 0) {
            path = SpeechPath.TEXT_TO_SPEECH;
            log.info("Android text to speech engine started, language={}", languageTag);
            return true;
        }
        log.error("Android text to speech engine failed to start: {}", lastError());
        return false;
    }

    /**
     * Queues a narration.
     *
     * @param text the narration text
     * @param rate a 0..1 speech rate position, 0.5 is the natural speed; only the
     *     text to speech path uses it, a screen reader keeps its own setting
     * @param pitch a 0..1 pitch position, 0.5 is the natural pitch; only the text
     *     to speech path uses it
     * @param interrupt whether speech that is still queued has to be dropped
     * @return true when the text was accepted
     */
    public static boolean narrate(String text, float rate, float pitch, boolean interrupt) {
        byte[] utf8 = text.getBytes(StandardCharsets.UTF_8);
        if (path == SpeechPath.SCREEN_READER) {
            return announce(utf8, interrupt) == 0;
        }
        if (path == SpeechPath.TEXT_TO_SPEECH) {
            return speak(utf8, rate, pitch, interrupt) == 0;
        }
        return false;
    }

    /** @return true when the engine is currently producing speech */
    public static boolean isSpeaking() {
        // A screen reader announces through a one way event, there is no state
        // to ask about, and a wrong answer only costs an interruption.
        return path == SpeechPath.TEXT_TO_SPEECH && stillSpeaking();
    }

    /** Drops everything that has been queued. */
    public static void stop() {
        if (path == SpeechPath.SCREEN_READER) {
            stopAnnouncement();
        } else if (path == SpeechPath.TEXT_TO_SPEECH) {
            stopSpeaking();
        }
    }

    /** @return the last problem the bridge ran into */
    public static String error() {
        if (!loaded) return "the Android narration library is not loaded";
        if (path == SpeechPath.SCREEN_READER) return screenReaderLastError();
        return lastError();
    }

    /** Releases the engine. */
    public static synchronized void shutdown() {
        if (path == SpeechPath.SCREEN_READER) {
            stopAnnouncement();
        } else if (path == SpeechPath.TEXT_TO_SPEECH) {
            shutdownEngine();
        }
        path = SpeechPath.NONE;
    }

    /**
     * @param dex the bridge dex
     * @return true when a spoken accessibility service is running, false also when
     *     the library that got loaded predates the screen reader support
     */
    private static boolean systemScreenReaderAvailable(byte[] dex) {
        try {
            return hasSystemScreenReader(dex);
        } catch (UnsatisfiedLinkError e) {
            // The loaded library and this class come from different builds, so
            // it can speak but knows nothing about announcements.
            log.warn("The loaded Android narration library has no screen reader support: {}", e.getMessage());
            return false;
        }
    }

    private static boolean load() {
        Path library = AndroidPlatform.extractLibrary();
        if (library == null) return false;
        try {
            System.load(library.toAbsolutePath().toString());
            loaded = true;
            log.info("Loaded the Android narration library from {}", library);
            return true;
        } catch (UnsatisfiedLinkError e) {
            log.error("Unable to load the Android narration library from {}", library, e);
            return false;
        }
    }

    @Nullable
    private static byte[] readDex() {
        Path dex = AndroidPlatform.extractDex();
        if (dex == null) return null;
        try {
            return Files.readAllBytes(dex);
        } catch (IOException e) {
            log.error("Unable to read the Android narration bridge dex {}", dex, e);
            return null;
        }
    }

    private static native int startEngine(byte[] dex, @Nullable String languageTag);

    private static native int speak(byte[] utf8, float rate, float pitch, boolean interrupt);

    private static native void stopSpeaking();

    private static native boolean stillSpeaking();

    private static native void shutdownEngine();

    private static native boolean hasSystemScreenReader(byte[] dex);

    private static native String screenReaderName();

    private static native int announce(byte[] utf8, boolean interrupt);

    private static native void stopAnnouncement();

    private static native String screenReaderLastError();

    private static native String lastError();
}
