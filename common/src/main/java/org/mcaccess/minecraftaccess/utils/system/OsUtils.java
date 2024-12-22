package com.github.khanshoaib3.minecraft_access.utils.system;

public class OsUtils {
    private static String OS = null;
    private static String Arch = null;

    /**
     * Returns the name of the OS
     */
    public static String getOsName() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        return OS;
    }

    /**
     * Returns the architecture of the JRE
     */
    public static String getOsArchitecture(){
        if (Arch == null) {
            Arch = System.getProperty("os.arch");
        }
        return Arch;
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
    public static boolean is64Bit(){
        return getOsArchitecture().contains("64");
    }
}
