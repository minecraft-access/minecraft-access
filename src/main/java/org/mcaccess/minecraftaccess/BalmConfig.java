package org.mcaccess.minecraftaccess;

import java.util.Locale;

import net.blay09.mods.balm.platform.config.reflection.Config;
import net.minecraft.util.StringRepresentable;

@Config(value = MainClass.MOD_ID, type = "common")
public class BalmConfig {
    public enum PickedUpItemNarration implements StringRepresentable {
        ALWAYS,
        WHEN_FISHING,
        NEVER;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }

    public General general = new General();
    public Features features = new Features();

    public static class General {
        public boolean menuFixEnabled = true;
        //@ConfigExtension.FormatString({'d', 'd', 's'})
        //public String commandSuggestionNarratorFormat = "%dx%d %s";
        public boolean use12HourTimeFormat = false;
        public boolean debugMode = false;
        public int multipleClickSpeedMilliseconds = 750;
    }

    public static class Features {
        public boolean actionBarEnabled = true;
        public boolean onlyNarrateActionBarUpdates = false;
        public boolean biomeIndicatorEnabled = true;
        public boolean alwaysNarrateDimensionInBiomeIndicator = false;
        public boolean timeIndicatorEnabled = true;
        public boolean xpIndicatorEnabled = true;
        public boolean facingDirectionEnabled = true;
        public boolean crouchAndSprintCues = true;
        public PickedUpItemNarration pickedUpItemNarration = PickedUpItemNarration.WHEN_FISHING;
        public boolean narrateHeldItemsCountWhenChanged = true;
        public boolean playNewChatMessageSound = true;
    }
}
