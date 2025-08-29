package org.mcaccess.minecraftaccess.utils;

public final class TextUtils {
    public static String removeFormattingCodes(String text) {
        // Remove formatting codes
        // ref: https://minecraft.wiki/w/Formatting_codes
        return text.replaceAll("§.", "");
    }
}
