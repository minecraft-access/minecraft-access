package org.mcaccess.minecraftaccess;

import lombok.Getter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.AutoConfigClient;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import net.blay09.mods.balm.Balm;
import net.minecraft.resources.Identifier;

import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.api.WorldNarrator;
import org.mcaccess.minecraftaccess.utils.config.ConfigExtension;

@me.shedaniel.autoconfig.annotation.Config(name = "minecraft-access")
public final class Config implements ConfigData {
    @Getter
    private static Config instance;

    public boolean menuFixEnabled = true;
    @ConfigExtension.FormatString({'d', 'd', 's'})
    public String commandSuggestionNarratorFormat = "%dx%d %s";
    public boolean use12HourTimeFormat = false;
    public boolean debugMode = false;
    public int multipleClickSpeedMilliseconds = 750;

    @ConfigEntry.Category("features")
    @ConfigEntry.Gui.TransitiveObject
    public Features features = new Features();
    @ConfigEntry.Category("cameraControls")
    @ConfigEntry.Gui.TransitiveObject
    public CameraControls cameraControls = new CameraControls();
    @ConfigEntry.Category("inventoryControls")
    @ConfigEntry.Gui.TransitiveObject
    public InventoryControls inventoryControls = new InventoryControls();
    @ConfigEntry.Category("mouseSimulation")
    @ConfigEntry.Gui.TransitiveObject
    public MouseSimulation mouseSimulation = new MouseSimulation();
    @ConfigEntry.Category("poi")
    @ConfigEntry.Gui.TransitiveObject
    public POI poi = new POI();
    @ConfigEntry.Category("playerWarnings")
    @ConfigEntry.Gui.TransitiveObject
    public PlayerWarnings playerWarnings = new PlayerWarnings();
    @ConfigEntry.Category("fallDetector")
    @ConfigEntry.Gui.TransitiveObject
    public FallDetector fallDetector = new FallDetector();
    @ConfigEntry.Category("narrateCrosshair")
    @ConfigEntry.Gui.TransitiveObject
    public NarrateCrosshair narrateCrosshair = new NarrateCrosshair();
    @ConfigEntry.Category("accessMenu")
    @ConfigEntry.Gui.TransitiveObject
    public AccessMenu accessMenu = new AccessMenu();
    @ConfigEntry.Category("speechSettings")
    @ConfigEntry.Gui.TransitiveObject
    public SpeechSettings speechSettings = new SpeechSettings();

    private Config() {
    }

    static void init() {
        ConfigExtension.apply(AutoConfigClient.getGuiRegistry(Config.class));
        AutoConfig.register(Config.class, ConfigExtension::serialiser);
        instance = AutoConfig.getConfigHolder(Config.class).get();
    }

    @Override
    public void validatePostLoad() {
        ConfigExtension.validate(this, new Config());
    }

    public static final class Features {
        public boolean actionBarEnabled = true;
        public boolean onlyNarrateActionBarUpdates = false;
        public boolean biomeIndicatorEnabled = true;
        public boolean alwaysNarrateDimensionInBiomeIndicator = false;
        public boolean timeIndicatorEnabled = true;
        public boolean xpIndicatorEnabled = true;
        public boolean facingDirectionEnabled = true;
        public boolean fishingHarvestEnabled = true;
        public boolean alwaysNarratePickedUpItems = false;
        public boolean narrateHeldItemsCountWhenChanged = true;
        public boolean playNewChatMessageSound = true;

        private Features() {
        }
    }

    public static final class SpeechSettings {
        public float speechRate = 50;

        private SpeechSettings() {
        }
    }

    public static final class CameraControls {
        public float normalRotatingAngle = 22.5f;
        public float modifiedRotatingAngle = 11.25f;

        private CameraControls() {
        }
    }

    public static final class InventoryControls {
        public boolean enabled = true;
        public boolean autoOpenRecipeBook = true;
        @ConfigExtension.FormatString({'d', 'd'})
        public String rowAndColumnFormat = "%dx%d";
        public boolean narrateFocusedSlotChanges = true;
        public int delayMilliseconds = 150;

        private InventoryControls() {
        }
    }

    public static final class MouseSimulation {
        public int scrollDelayMilliseconds = 150;

        private MouseSimulation() {
        }
    }

    public static final class POI {
        public boolean narrateDistance = true;
        @ConfigEntry.Gui.CollapsibleObject
        public Blocks blocks = new Blocks();
        @ConfigEntry.Gui.CollapsibleObject
        public Entities entities = new Entities();
        @ConfigEntry.Gui.CollapsibleObject
        public Locking locking = new Locking();
        @ConfigEntry.Gui.CollapsibleObject
        public Marking marking = new Marking();

        private POI() {
        }

        public static final class Blocks {
            public boolean enabled = true;
            public boolean detectFluidBlocks = true;
            public int range = 24;
            public boolean playSound = true;
            public float volume = 0.25f;
            public boolean playSoundForOtherBlocks = true;
            public int delay = 3000;

            private Blocks() {
            }
        }

        public static final class Entities {
            public boolean enabled = true;
            public int range = 24;
            public boolean playSound = true;
            public float volume = 0.25f;
            public int delay = 3000;

            private Entities() {
            }
        }

        public static final class Locking {
            public boolean unlockingSound = false;
            public boolean autoLockEyeOfEnderEntity = true;
            public boolean aimAssistEnabled = true;
            public boolean aimAssistAudioCuesEnabled = true;
            public float aimAssistAudioCuesVolume = 0.5f;

            private Locking() {
            }
        }

        public static final class Marking {
            public boolean suppressOtherWhenEnabled = true;

            private Marking() {
            }
        }
    }

    public static final class PlayerWarnings {
        public boolean enabled = true;
        public boolean playSound = true;
        public double firstHealthThreshold = 6;
        public double secondHealthThreshold = 3;
        public double hungerThreshold = 3;
        public double airThreshold = 5;
        public double frostThreshold = 30;
        @ConfigExtension.Registry(registry = Status.class, i18n = "status")
        public Identifier[] statuses = new Identifier[]{
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
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "durability/feet"),
        };
        @ConfigEntry.Gui.CollapsibleObject
        public DurabilityWarnings durabilityWarnings = new DurabilityWarnings();

        private PlayerWarnings() {
        }
    }

    public static final class DurabilityWarnings {
        public boolean enableHeldItems = true;
        public boolean enableWornArmor = true;
        public int firstThreshold = 10;
        public int secondThreshold = 3;

        private DurabilityWarnings() {
        }
    }

    public static final class FallDetector {
        public boolean enabled = true;
        public int range = 6;
        public int depth = 4;
        public float volume = 0.25f;
        public int delay = 2500;

        private FallDetector() {
        }
    }

    public static final class NarrateCrosshair {
        public boolean enabled = true;
        @ConfigExtension.Registry(registry = WorldNarrator.class, i18n = "narrator")
        public Identifier narrator = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, Balm.platform().isModLoaded("jade") ? "jade" : "minecraft_access");
        public boolean narrateBlockFace = true;
        public boolean disableNarratingConsecutiveBlocks = false;
        public long repetitionInterval = 0;
        public boolean narrateAdditionalEntityPoses = true;

        @ConfigEntry.Gui.CollapsibleObject
        public RelativePositionSoundCue relativePositionSoundCue = new RelativePositionSoundCue();
        @ConfigEntry.Gui.CollapsibleObject
        public Filter filter = new Filter();

        private NarrateCrosshair() {
        }

        public static final class RelativePositionSoundCue {
            public boolean enabled = true;
            public float minSoundVolume = 0.25f;
            public float maxSoundVolume = 0.4f;

            private RelativePositionSoundCue() {
            }
        }

        public static final class Filter {
            public boolean enabled = false;
            public boolean whitelist = true;
            public boolean fuzzy = true;
            @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            public TargetMode targetMode = TargetMode.BLOCK;
            public String[] targets = new String[]{"slab", "planks", "block", "stone", "sign"};

            private Filter() {
            }

            public enum TargetMode {
                ALL,
                ENTITY,
                BLOCK;

                public boolean filterBlocks() {
                    return this == ALL || this == BLOCK;
                }

                public boolean filterEntities() {
                    return this == ALL || this == ENTITY;
                }
            }
        }
    }

    public static final class AccessMenu {
        public boolean enabled = true;
        @ConfigEntry.Gui.CollapsibleObject
        public FluidDetector fluidDetector = new FluidDetector();
        @ConfigExtension.Registry(registry = AccessMenuFunction.class, i18n = "access_menu_function")
        public Identifier[] functions = new Identifier[]{
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
                Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "weather"),
        };
        @ConfigEntry.Gui.CollapsibleObject
        public ShortcutBar shortcutBar = new ShortcutBar();

        private AccessMenu() {
        }

        public static final class FluidDetector {
            public float volume = 0.25f;
            public int range = 100;

            private FluidDetector() {
            }
        }

        public static final class ShortcutBar {
            @ConfigExtension.Registry(registry = AccessMenuFunction.class, i18n = "access_menu_function")
            public Identifier key1 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "narrate_target");
            @ConfigExtension.Registry(registry = AccessMenuFunction.class, i18n = "access_menu_function")
            public Identifier key2 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "target_position");
            @ConfigExtension.Registry(registry = AccessMenuFunction.class, i18n = "access_menu_function")
            public Identifier key3 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "light_level");
            @ConfigExtension.Registry(registry = AccessMenuFunction.class, i18n = "access_menu_function")
            public Identifier key4 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "find_water");
            @ConfigExtension.Registry(registry = AccessMenuFunction.class, i18n = "access_menu_function")
            public Identifier key5 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "find_lava");
            @ConfigExtension.Registry(registry = AccessMenuFunction.class, i18n = "access_menu_function")
            public Identifier key6 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "biome");
            @ConfigExtension.Registry(registry = AccessMenuFunction.class, i18n = "access_menu_function")
            public Identifier key7 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "time");
            @ConfigExtension.Registry(registry = AccessMenuFunction.class, i18n = "access_menu_function")
            public Identifier key8 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "xp");
            @ConfigExtension.Registry(registry = AccessMenuFunction.class, i18n = "access_menu_function")
            public Identifier key9 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "refresh_screen_reader");
            @ConfigExtension.Registry(registry = AccessMenuFunction.class, i18n = "access_menu_function")
            public Identifier key0 = Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "config");
        }
    }

}
