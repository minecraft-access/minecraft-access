package org.mcaccess.minecraftaccess.utils.system;

public final class OsUtils {
    private OsUtils() {
    }

    /**
     * Returns the name of the OS
     */
    public static String getOsName() {
        return System.getProperty("os.name");
    }

    /**
     * Returns the architecture of the JRE
     */
    public static String getOsArchitecture() {
        return System.getProperty("os.arch");
    }

    /**
     * Checks whether the os is windows or not
     * @return Returns true if os is windows
     */
    public static boolean isWindows() {
        return getOsName().startsWith("Windows");
    }

    /**
     * Checks whether the os is linux or not
     * @return Returns true if os is linux
     */
    public static boolean isLinux() {
        return getOsName().startsWith("Linux");
    }

    /**
     * Checks whether the os is macos or not
     * @return Returns true if os is macos
     */
    public static boolean isMacOS() {
        return getOsName().startsWith("Mac");
    }

    /**
     * Checks whether the architecture of JRE is 64 bit or not
     * @return Returns true if the architecture is 64 bit
     */
    public static boolean is64Bit() {
        return getOsArchitecture().contains("64");
    }
}
