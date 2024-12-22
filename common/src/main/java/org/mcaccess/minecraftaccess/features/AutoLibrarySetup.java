package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.utils.UnzipUtility;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Automatically installs the required libraries for client's operating system.
 */
@Slf4j
public class AutoLibrarySetup {
    public AutoLibrarySetup() {
    }

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
        if (SystemUtils.IS_OS_WINDOWS) {
           log.info("Downloading latest tolk build...");
            File tolkLatestBuildZip = new File(Paths.get("tolk-latest-build.zip").toAbsolutePath().toString());
            FileUtils.copyURLToFile(new URL("https://github.com/ndarilek/tolk/releases/download/refs%2Fheads%2Fmaster/tolk.zip"), tolkLatestBuildZip);

            UnzipUtility unzipUtility = new UnzipUtility();
            File tempDirectoryPath = Paths.get("temp-tolk-latest").toAbsolutePath().toFile();
            try {
                unzipUtility.unzip(tolkLatestBuildZip.getAbsolutePath(), tempDirectoryPath.getAbsolutePath());
            } catch (Exception e) {
                log.error("An error occurred while extracting tolk-latest-build.zip", e);
            }

           log.info("Moving files...");
            String sourceDir = "x64";
            if (SystemUtils.OS_ARCH.equalsIgnoreCase("X86")) sourceDir = "x86";
            FileUtils.copyDirectory(Paths.get(tempDirectoryPath.getAbsolutePath(), sourceDir).toFile(),
                    tempDirectoryPath.getParentFile());

           log.info("Deleting temp files...");
            FileUtils.delete(tolkLatestBuildZip);
            FileUtils.deleteDirectory(tempDirectoryPath);
           log.info("tolk library downloaded and installed.");
        } else if (SystemUtils.IS_OS_LINUX) {
           log.info("Downloading libspeechdwrapper.so ...");
            FileUtils.copyURLToFile(new URL("https://github.com/khanshoaib3/libspeechdwrapper/releases/download/v1.0.0/libspeechdwrapper.so"), new File(Paths.get("libspeechdwrapper.so").toAbsolutePath().toString()));
           log.info("libspeechdwrapper.so downloaded and installed.");
        }
    }

    /**
     * Checks whether all the library files are installed or not
     *
     * @return Returns true if all files are installed otherwise false.
     */
    private boolean checkInstalled() {
       log.info("Checking for installed files...");

        for (String libraryName : getRequiredLibraryNames()) {
           log.debug("Checking for " + libraryName);
            if (!Files.exists(Paths.get(libraryName).toAbsolutePath())) {
               log.error(libraryName + " file not found.");
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

        if (SystemUtils.IS_OS_WINDOWS) {
            requiredFiles.add("Tolk.dll");
            if (SystemUtils.OS_ARCH.equalsIgnoreCase("X86")) {
                requiredFiles.add("nvdaControllerClient32.dll");
                requiredFiles.add("SAAPI32.dll");
                requiredFiles.add("dolapi32.dll");
            } else {
                requiredFiles.add("nvdaControllerClient64.dll");
                requiredFiles.add("SAAPI64.dll");
            }
        } else if (SystemUtils.IS_OS_LINUX) {
            requiredFiles.add("libspeechdwrapper.so");
        }

        return requiredFiles;
    }
}
