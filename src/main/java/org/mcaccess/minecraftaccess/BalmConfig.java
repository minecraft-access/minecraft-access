package org.mcaccess.minecraftaccess;

import java.util.List;
import java.util.Locale;

import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.platform.config.reflection.Comment;
import net.blay09.mods.balm.platform.config.reflection.Config;
import net.blay09.mods.balm.platform.config.reflection.NestedType;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;

@Config(MainClass.MOD_ID)
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

    public enum TargetMode implements StringRepresentable {
        ALL,
        ENTITY,
        BLOCK;

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public boolean filterBlocks() {
            return this == ALL || this == BLOCK;
        }

        public boolean filterEntities() {
            return this == ALL || this == ENTITY;
        }
    }

    public General general = new General();
    public Features features = new Features();
    public SpeechSettings speechSettings = new SpeechSettings();
    public CameraControls cameraControls = new CameraControls();
    public InventoryControls inventoryControls = new InventoryControls();
    public MouseSimulation mouseSimulation = new MouseSimulation();
    public POI poi = new POI();
    public POIBlocks poiBlocks = new POIBlocks();
    public POIEntities poiEntities = new POIEntities();
    public POILocking poiLocking = new POILocking();
    public POIMarking poiMarking = new POIMarking();
    public PlayerWarnings playerWarnings = new PlayerWarnings();
    public DurabilityWarnings durabilityWarnings = new DurabilityWarnings();
    public FallDetector fallDetector = new FallDetector();
    public NarrateCrosshair narrateCrosshair = new NarrateCrosshair();
    public NarrateCrosshairFilter narrateCrosshairFilter = new NarrateCrosshairFilter();
    public NarrateCrosshairRelativePositionSoundCue narrateCrosshairRelativePositionSoundCue = new NarrateCrosshairRelativePositionSoundCue();
    public AccessMenu accessMenu = new AccessMenu();
    public AccessMenuFluidDetector accessMenuFluidDetector = new AccessMenuFluidDetector();
    public AccessMenuShortcutBar accessMenuShortcutBar = new AccessMenuShortcutBar();

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

    public static class NarrateCrosshair {
        public boolean enabled = true;
        public Identifier narrator = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, Balm.platform().isModLoaded("jade") ? "jade" : "minecraft_access");
        public boolean narrateBlockFace = true;
        public boolean disableNarratingConsecutiveBlocks = false;
        public long repetitionInterval = 0;
        public boolean narrateAdditionalEntityPoses = true;
    }

    public static class NarrateCrosshairFilter {
        public boolean enabled = false;
        public boolean whitelist = true;
        public boolean fuzzy = true;
        public TargetMode targetMode = TargetMode.BLOCK;
        @NestedType(String.class)
        public List<String> targets = List.of("slab", "planks", "block", "stone", "sign");
    }

    public static class NarrateCrosshairRelativePositionSoundCue {
        public boolean enabled = true;
        public float minSoundVolume = 0.25f;
        public float maxSoundVolume = 0.4f;
    }

    public static class AccessMenu {
        public boolean enabled = true;
        @NestedType(Identifier.class)
        public List<Identifier> functions = List.of(
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "narrate_target"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "target_position"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "light_level"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "find_water"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "find_lava"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "biome"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "time"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "xp"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "refresh_screen_reader"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "config"),
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "weather")
        );
    }

    public static class AccessMenuFluidDetector {
        public float volume = 0.25f;
        public int range = 100;
    }

    public static class AccessMenuShortcutBar {
        public Identifier key1 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "narrate_target");
        public Identifier key2 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "target_position");
        public Identifier key3 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "light_level");
        public Identifier key4 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "find_water");
        public Identifier key5 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "find_lava");
        public Identifier key6 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "biome");
        public Identifier key7 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "time");
        public Identifier key8 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "xp");
        public Identifier key9 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "refresh_screen_reader");
        public Identifier key0 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "config");
    }
}
