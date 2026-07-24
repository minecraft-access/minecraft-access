package org.mcaccess.minecraftaccess;

import java.util.List;
import java.util.Locale;

import net.blay09.mods.balm.platform.config.reflection.Comment;
import net.blay09.mods.balm.platform.config.reflection.Config;
import net.blay09.mods.balm.platform.config.reflection.NestedType;
import net.minecraft.resources.Identifier;
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
    public SpeechSettings speechSettings = new SpeechSettings();
    public CameraControls cameraControls = new CameraControls();
    public InventoryControls inventoryControls = new InventoryControls();
    public  MouseSimulation mouseSimulation = new MouseSimulation();
    public POI poi = new POI();
    public POIBlocks poiBlocks = new POIBlocks();
    public POIEntities poiEntities = new POIEntities();
    public POILocking poiLocking = new POILocking();
    public POIMarking poiMarking = new POIMarking();
    public PlayerWarnings playerWarnings = new PlayerWarnings();
    public DurabilityWarnings durabilityWarnings = new DurabilityWarnings();
    public FallDetector fallDetector = new FallDetector();

    public static class General {
        public boolean menuFixEnabled = true;
        @Comment("Format: '%d', '%d', '%s'")
        public String commandSuggestionNarratorFormat = "%dx%d %s";
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

    public static class SpeechSettings {
        public float speechRate = 50;
        public boolean narrateHints = true;
    }

    public static class CameraControls {
        public float normalRotatingAngle = 22.5f;
        public float modifiedRotatingAngle = 11.25f;
    }

    public static class InventoryControls {
        public boolean enabled = true;
        public boolean autoOpenRecipeBook = true;
        @Comment("Format: '%d', '%d'")
        public String rowAndColumnFormat = "%dx%d";
        public boolean narrateFocusedSlotChanges = true;
        public int delayMilliseconds = 150;
    }

    public static class MouseSimulation {
        public int scrollDelayMilliseconds = 150;
    }

    public static class POI {
        public boolean narrateDistance = true;
    }

        public static class POIBlocks {
            public boolean enabled = true;
            public boolean detectFluidBlocks = true;
            public int range = 24;
            public boolean playSound = true;
            public float volume = 0.25f;
            public boolean playSoundForOtherBlocks = true;
            public int delay = 3000;
        }

        public static class POIEntities {
            public boolean enabled = true;
            public int range = 24;
            public boolean playSound = true;
            public float volume = 0.25f;
            public int delay = 3000;
        }

        public static class POILocking {
            public boolean autoLockEyeOfEnderEntity = true;
            public boolean aimAssistEnabled = true;
            public boolean aimAssistAudioCuesEnabled = true;
            public float aimAssistAudioCuesVolume = 0.5f;
        }

        public static class POIMarking {
            public boolean suppressOtherWhenEnabled = true;
        }

    public static class PlayerWarnings {
        public boolean enabled = true;
        public boolean playSound = true;
        public double firstHealthThreshold = 6;
        public double secondHealthThreshold = 3;
        public double hungerThreshold = 3;
        public double airThreshold = 5;
        public double frostThreshold = 30;
        @NestedType(Identifier.class)
        public List<Identifier> statuses = List.of(
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "health"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "hunger"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "armour"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "air"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "frost"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "game_mode"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "durability/main_hand"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "durability/offhand"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "durability/head"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "durability/chest"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "durability/legs"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "durability/feet")
        );
    }

    public static class DurabilityWarnings {
        public boolean enableHeldItems = true;
        public boolean enableWornArmor = true;
        public int firstThreshold = 10;
        public int secondThreshold = 3;
    }

    public static class FallDetector {
        public boolean enabled = true;
        public int range = 6;
        public int depth = 4;
        public float volume = 0.25f;
        public int delay = 2500;
    }


}
