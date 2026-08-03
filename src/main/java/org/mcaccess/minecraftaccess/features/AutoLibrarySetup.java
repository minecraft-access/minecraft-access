package org.mcaccess.minecraftaccess.features;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.util.Util;
import org.apache.commons.io.FileUtils;

import org.mcaccess.minecraftaccess.utils.UnzipUtility;

/**
 * Automatically installs the required libraries for client's operating system.
 */
@Slf4j
public class AutoLibrarySetup {
    public void initialize() {
        try {
            if (checkInstalled()) return;
            downloadAndInstall();
        } catch (IOException e) {
            log.error("An error occurred while downloading library.", e);
        }
    }

    /**
     * Downloads and installs library based on the operating system.
     * Installs <a href="https://github.com/ndarilek/tolk">tolk</a> for windows and <a href="https://github.com/khanshoaib3/libspeechdwrapper">libspeechdwrapper</a> for linux.
     */
    private void downloadAndInstall() throws IOException {
        switch (getSupportedLibArchitecture()) {
            case WINDOWS -> {
                log.info("Downloading latest tolk build...");
                File tolkLatestBuildZip = new File(Paths.get("tolk-latest-build.zip").toAbsolutePath().toString());
                FileUtils.copyURLToFile(URI.create("https://github.com/ndarilek/tolk/releases/download/refs%2Fheads%2Fmaster/tolk.zip").toURL(), tolkLatestBuildZip);

                UnzipUtility unzipUtility = new UnzipUtility();
                File tempDirectoryPath = Paths.get("temp-tolk-latest").toAbsolutePath().toFile();
                unzipUtility.unzip(tolkLatestBuildZip.getAbsolutePath(), tempDirectoryPath.getAbsolutePath());

                log.info("Moving files...");
                String sourceDir = "x64";
                if (System.getProperty("os.arch").equalsIgnoreCase("X86")) sourceDir = "x86";
                FileUtils.copyDirectory(Paths.get(tempDirectoryPath.getAbsolutePath(), sourceDir).toFile(),
                        tempDirectoryPath.getParentFile());

                log.info("Deleting temp files...");
                FileUtils.delete(tolkLatestBuildZip);
                FileUtils.deleteDirectory(tempDirectoryPath);
                log.info("tolk library downloaded and installed.");
            }
            case LINUX -> {
                log.info("Downloading libspeechdwrapper.so ...");
                FileUtils.copyURLToFile(
                        URI.create("https://github.com/khanshoaib3/libspeechdwrapper/releases/download/v1.0.0/libspeechdwrapper.so").toURL(),
                        new File(Paths.get("libspeechdwrapper.so").toAbsolutePath().toString())
                );
                log.info("libspeechdwrapper.so downloaded and installed.");
            }
            default -> {
            }
        }
    }

    /**
     * Checks whether all the library files are installed or not.
     *
     * @return Returns true if all files are installed otherwise false.
     */
    private boolean checkInstalled() {
        log.info("Checking for installed files...");

        for (String libraryName : getRequiredLibraryNames()) {
            log.debug("Checking for {}", libraryName);
            if (!Files.exists(Paths.get(libraryName).toAbsolutePath())) {
                log.error("{} file not found.", libraryName);
                return false;
            }
        }

        log.info("All files are installed.");
        return true;
    }

    /**
     * Returns the list of libraries required based on the operating system.
     *
     * @return The list of required libraries
     */
    private List<String> getRequiredLibraryNames() {
        List<String> requiredFiles = new ArrayList<>();
        switch (getSupportedLibArchitecture()) {
            case WINDOWS -> {
                requiredFiles.add("Tolk.dll");
                if (System.getProperty("os.arch").equalsIgnoreCase("X86")) {
                    requiredFiles.add("nvdaControllerClient32.dll");
                    requiredFiles.add("SAAPI32.dll");
                    requiredFiles.add("dolapi32.dll");
                } else {
                    requiredFiles.add("nvdaControllerClient64.dll");
                    requiredFiles.add("SAAPI64.dll");
                }
            }
            case LINUX -> requiredFiles.add("libspeechdwrapper.so");
            default -> {
            }
        }

        return requiredFiles;
    }

    public static Util.OS getSupportedLibArchitecture() {
        Util.OS platform = Util.getPlatform();
        String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT).toLowerCase(Locale.ROOT);

        if (arch.contains("amd64")
                || arch.contains("x86_64")
                || arch.contains("x86-64")
                || arch.equals("x64")) {
            if (platform == Util.OS.WINDOWS) {
                return Util.OS.WINDOWS;
            } else if (platform == Util.OS.LINUX) {
                return Util.OS.LINUX;
            }
        }

        if (platform == Util.OS.OSX) {
            return Util.OS.OSX;
        }

        return Util.OS.UNKNOWN;
    }
}
