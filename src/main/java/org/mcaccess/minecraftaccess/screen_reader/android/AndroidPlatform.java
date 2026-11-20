package org.mcaccess.minecraftaccess.screen_reader.android;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

/**
 * Detects the Android runtime that hosts the game and hands out the files
 * required to reach the system text to speech engine.
 *
 * <p>Minecraft runs in a normal OpenJDK JVM here, but the launcher starts that
 * JVM inside an Android application process, so {@code os.name} is
 * {@code Linux} and the platform checks of Minecraft cannot be used to tell the
 * two apart. The launcher marks the process through environment variables
 * instead, and {@code /system/build.prop} is a last resort.
 */
@Slf4j
public final class AndroidPlatform {
    /** Classpath folder that holds the Android native library and the dex. */
    private static final String RESOURCE_ROOT = "/mcaccess/android/";
    /** File name of the native bridge library. */
    public static final String LIBRARY_NAME = "libmcaccess_android_tts.so";
    /** File name of the dex that is loaded into the Android runtime. */
    public static final String DEX_NAME = "mcaccess_android_tts.dex";
    /** Smallest Android version that can load a dex from memory. */
    public static final int MIN_API_LEVEL = 26;

    private static Boolean android;
    private static @Nullable Path workDirectory;

    private AndroidPlatform() {
    }

    /**
     * Detects whether the game was started inside an Android application.
     *
     * @return true when the process is hosted by an Android launcher
     */
    public static boolean isAndroid() {
        if (android == null) {
            android = detect();
        }
        return android;
    }

    private static boolean detect() {
        if (notBlank(System.getenv("DALVIK_JAVAVM"))
                || notBlank(System.getenv("MOD_ANDROID_RUNTIME"))
                || notBlank(System.getenv("DALVIK_APPLICATION"))) {
            log.info("Android launcher environment detected through the process environment");
            return true;
        }
        if (Files.isReadable(Paths.get("/system/build.prop"))) {
            log.info("Android launcher environment detected through /system/build.prop");
            return true;
        }
        return false;
    }

    private static boolean notBlank(@Nullable String value) {
        return value != null && !value.isBlank();
    }

    /**
     * Maps the architecture reported by the JVM to the ABI name Android uses.
     *
     * @return the ABI folder name, or null when the architecture is unknown
     */
    public static @Nullable String abi() {
        String architecture = System.getProperty("os.arch", "");
        return switch (architecture) {
            case "aarch64", "arm64" -> "arm64-v8a";
            case "arm", "armv7l", "armv7", "armeabi" -> "armeabi-v7a";
            case "x86_64", "amd64" -> "x86_64";
            default -> {
                log.error("Unsupported Android architecture: {}", architecture);
                yield null;
            }
        };
    }

    /**
     * Copies the native library that matches the running architecture to disk.
     *
     * @return the extracted library, or null when it could not be written
     */
    public static @Nullable Path extractLibrary() {
        String abi = abi();
        if (abi == null) return null;
        return extract(RESOURCE_ROOT + abi + "/" + LIBRARY_NAME, LIBRARY_NAME);
    }

    /**
     * Copies the dex that holds the text to speech bridge to disk.
     *
     * @return the extracted dex, or null when it could not be written
     */
    public static @Nullable Path extractDex() {
        return extract(RESOURCE_ROOT + DEX_NAME, DEX_NAME);
    }

    private static @Nullable Path extract(String resource, String fileName) {
        Path directory = workDirectory();
        if (directory == null) return null;

        Path target = directory.resolve(fileName);
        try (InputStream stream = AndroidPlatform.class.getResourceAsStream(resource)) {
            if (stream == null) {
                log.error("The mod is missing the Android resource {}", resource);
                return null;
            }
            Files.copy(stream, target, StandardCopyOption.REPLACE_EXISTING);
            log.info("Wrote {} to {}", resource, target);
            return target;
        } catch (IOException e) {
            log.error("Unable to write {} to {}", resource, target, e);
            return null;
        }
    }

    private static @Nullable Path workDirectory() {
        if (workDirectory != null) return workDirectory;

        Path[] candidates = {
                pathOf(System.getProperty("java.io.tmpdir")),
                pathOf(System.getProperty("user.home")),
                Paths.get("").toAbsolutePath()
        };
        for (Path candidate : candidates) {
            if (candidate == null) continue;
            Path directory = candidate.resolve("mcaccess-android");
            try {
                Files.createDirectories(directory);
                workDirectory = directory;
                return directory;
            } catch (IOException e) {
                log.warn("Unable to use {} for the Android bridge files", directory, e);
            }
        }
        log.error("Unable to find a writable directory for the Android bridge files");
        return null;
    }

    private static @Nullable Path pathOf(@Nullable String value) {
        if (value == null || value.isBlank()) return null;
        return Paths.get(value);
    }
}
