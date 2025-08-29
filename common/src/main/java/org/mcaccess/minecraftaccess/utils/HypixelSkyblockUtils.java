package org.mcaccess.minecraftaccess.utils;

import java.util.regex.Pattern;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

import org.mcaccess.minecraftaccess.MainClass;

public final class HypixelSkyblockUtils {
    // https://regex101.com/r/hgCXM2/1
    private static final Pattern STATS_REGEX = Pattern.compile(".*❤.*❈ Defense.*✎ Mana", Pattern.MULTILINE);
    private static final Pattern LOCATION_REGEX = Pattern.compile(".*❤.*⏣(\\D*)\\d*/\\d*✎ Mana", Pattern.MULTILINE);
    private static final String LOCATION_REGEX_SUBSTITUTE = "$1";

    public static String lastStats = null;
    public static String lastLocation = null;

    private HypixelSkyblockUtils() {
    }

    public static boolean isInServer() {
        return Minecraft.getInstance().getCurrentServer() != null
                && Minecraft.getInstance().getCurrentServer().ip.equalsIgnoreCase("mc.hypixel.net");
    }

    public static boolean checkForExclusion(String text) {
        String formattedText = TextUtils.removeFormattingCodes(text);
        if (STATS_REGEX.matcher(formattedText).matches()) {
            lastStats = formattedText;
            return true;
        }

        if (LOCATION_REGEX.matcher(formattedText).matches()) {
            String loc = LOCATION_REGEX.matcher(formattedText).replaceAll(LOCATION_REGEX_SUBSTITUTE);
            if (!loc.equals(lastLocation)) {
                lastLocation = loc;
                MainClass.narrate(I18n.get("minecraft_access.other.hypixel_skyblock_area_entered", lastLocation), true);
            }
            return true;
        }

        return false;
    }
}
