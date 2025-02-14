package org.mcaccess.minecraftaccess;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@me.shedaniel.autoconfig.annotation.Config(name = "minecraft-access")
public class Config implements ConfigData {
    @ConfigEntry.Gui.Excluded
    private static final Pattern FORMAT_STRING_PLACEHOLDER = Pattern.compile("%(?<type>[^%])");
    @ConfigEntry.Gui.Excluded
    private static Config instance;

    private Config() {}

    @Contract(pure = true)
    public static Config getInstance() {
        return instance;
    }

    @Contract(pure = true)
    @VisibleForTesting
    public static @NotNull ConfigSerializer.Factory<@NotNull Config> getSerializer() {
        return GsonConfigSerializer::new;
    }

    static void init() {
        AutoConfig.register(Config.class, getSerializer());
        instance = AutoConfig.getConfigHolder(Config.class).get();
    }

    @Override
    public void validatePostLoad() throws ValidationException {
        validateFormatString(commandSuggestionNarratorFormat, 'd', 'd', 's');
        validateFormatString(inventoryControls.rowAndColumnFormat, 'd', 'd');
    }

    private void validateFormatString(String string, char @NotNull ... placeholders) throws ValidationException {
        Matcher matcher = FORMAT_STRING_PLACEHOLDER.matcher(string);
        for (char type : placeholders) {
            if (!matcher.find()) {
                throw new ValidationException(String.format("Too few placeholders in string '%s'. Expected %d", string, placeholders.length));
            }
            if (!Objects.equals(matcher.group("type"), String.valueOf(type))) {
                throw new ValidationException(String.format("Invalid placeholder type in string '%s'. Expected %%%s, found %%%s", string, type, matcher.group("type")));
            }
        }
        if (matcher.find()) {
            throw new ValidationException(String.format("Too many placeholders in string '%s'. Expected %d", string, placeholders.length));
        }
    }

    public boolean menuFixEnabled = true;
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
    @ConfigEntry.Category("readCrosshair")
    @ConfigEntry.Gui.TransitiveObject
    public ReadCrosshair readCrosshair = new ReadCrosshair();
    @ConfigEntry.Category("accessMenu")
    @ConfigEntry.Gui.TransitiveObject
    public AccessMenu accessMenu = new AccessMenu();
    @ConfigEntry.Category("speechSettings")
    @ConfigEntry.Gui.TransitiveObject
    public SpeechSettings speechSettings = new SpeechSettings();

    public static final class Features {
        private Features() {
        }

        public boolean actionBarEnabled = true;
        public boolean onlySpeakActionBarUpdates = false;
        public boolean biomeIndicatorEnabled = true;
        public boolean xpIndicatorEnabled = true;
        public boolean facingDirectionEnabled = true;
        public boolean playerStatusEnabled = true;
        public boolean positionNarratorEnabled = true;
        public boolean fishingHarvestEnabled = true;
        public boolean reportHeldItemsCountWhenChanged = true;
    }

    public static final class SpeechSettings {
        private SpeechSettings() {}

        public float speechRate = 50;
    }

    public static final class CameraControls {
        private CameraControls() {}

        public boolean enabled = true;
        public float normalRotatingAngle = 22.5f;
        public float modifiedRotatingAngle = 11.25f;
        public int delayMilliseconds = 250;
    }

    public static final class InventoryControls {
        private InventoryControls() {}

        public boolean enabled = true;
        public boolean autoOpenRecipeBook = true;
        public String rowAndColumnFormat = "%dx%d";
        public boolean speakFocusedSlotChanges = true;
        public int delayMilliseconds = 150;
    }

    public static final class MouseSimulation {
        private MouseSimulation() {}

        public boolean enabled = true;
        public int scrollDelayMilliseconds = 150;
        public boolean macMouseFix = false;
    }

    public static final class POI {
        private POI() {}

        @ConfigEntry.Gui.CollapsibleObject
        public Blocks blocks = new Blocks();
        @ConfigEntry.Gui.CollapsibleObject
        public Entities entities = new Entities();
        @ConfigEntry.Gui.CollapsibleObject
        public Locking locking = new Locking();
        @ConfigEntry.Gui.CollapsibleObject
        public Marking marking = new Marking();

        public static final class Blocks {
            private Blocks() {}

            public boolean enabled = true;
            public boolean detectFluidBlocks = true;
            public int range = 6;
            public boolean playSound = true;
            public float volume = 0.25f;
            public boolean playSoundForOtherBlocks = true;
            public int delay = 3000;
        }

        public static final class Entities {
            private Entities() {}

            public boolean enabled = true;
            public int range = 6;
            public boolean playSound = true;
            public float volume = 0.25f;
            public int delay = 3000;
        }

        public static final class Locking {
            private Locking() {}

            public boolean enabled = true;
            public boolean lockOnBlocks = true;
            public boolean speakDistance = false;
            public boolean unlockingSound = false;
            public boolean autoLockEyeOfEnderEntity = true;
            public int delay = 100;
            public boolean aimAssistEnabled = true;
            public boolean aimAssistAudioCuesEnabled = true;
            public float aimAssistAudioCuesVolume = 0.5f;
        }

        public static final class Marking {
            private Marking() {}

            public boolean enabled = true;
            public boolean suppressOtherWhenEnabled = true;
        }
    }

    public static final class PlayerWarnings {
        private PlayerWarnings() {}

        public boolean enabled = true;
        public boolean playSound = true;
        public double firstHealthThreshold = 6;
        public double secondHealthThreshold = 3;
        public double hungerThreshold = 3;
        public double airThreshold = 5;
        public double frostThreshold = 30;
    }

    public static final class FallDetector {
        private FallDetector() {}

        public boolean enabled = true;
        public int range = 6;
        public int depth = 4;
        public float volume = 0.25f;
        public int delay = 2500;
    }

    public static final class ReadCrosshair {
        private ReadCrosshair() {}

        public boolean enabled = true;
        public boolean useJade = true;
        public boolean speakSide = true;
        public boolean disableSpeakingConsecutiveBlocks = false;
        public long repeatSpeakingInterval = 0;

        @ConfigEntry.Gui.CollapsibleObject
        public RelativePositionSoundCue relativePositionSoundCue = new RelativePositionSoundCue();
        @ConfigEntry.Gui.CollapsibleObject
        public PartialSpeaking partialSpeaking = new PartialSpeaking();

        public static final class RelativePositionSoundCue {
            private RelativePositionSoundCue() {}

            public boolean enabled = true;
            public float minSoundVolume = 0.25f;
            public float maxSoundVolume = 0.4f;
        }

        public static final class PartialSpeaking {
            private PartialSpeaking() {}

            public boolean enabled = false;
            public boolean whitelist = true;
            public boolean fuzzy = true;
            @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
            public TargetMode targetMode = TargetMode.BLOCK;
            public String[] targets = new String[] {"slab", "planks", "block", "stone", "sign"};

            public enum TargetMode {
                ALL,
                ENTITY,
                BLOCK,
            }
        }
    }

    public static final class AccessMenu {
        private AccessMenu() {}

        public boolean enabled = true;

        @ConfigEntry.Gui.CollapsibleObject
        public FluidDetector fluidDetector = new FluidDetector();

        public static final class FluidDetector {
            private FluidDetector() {}

            public float volume = 0.25f;
            public int range = 10;
        }
    }
}
