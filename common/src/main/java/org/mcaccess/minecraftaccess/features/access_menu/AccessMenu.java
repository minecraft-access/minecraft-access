package org.mcaccess.minecraftaccess.features.access_menu;

import java.util.Arrays;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import lombok.extern.slf4j.Slf4j;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.features.BiomeIndicator;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderController;
import org.mcaccess.minecraftaccess.utils.KeyMappingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.condition.IntervalKeystroke;
import org.mcaccess.minecraftaccess.utils.condition.MenuKeystroke;

/**
 * Opens a menu when F4 button is pressed (configurable) with helpful options.
 */
@Slf4j
public class AccessMenu {
    private static final Minecraft CLIENT = Minecraft.getInstance();
    public static final double RAY_CAST_DISTANCE = 20.0; // Much farther than the Read Crosshair feature (6 blocks).
    private static final MenuKeystroke MENU_KEY = new MenuKeystroke(KeyMappingsHandler.Keys.ACCESS_MENU_KEY.mapping);
    private boolean gameModeSwitcherActive = false;
    /**
     * Access Menu function direct keys (configured in keybinding settings)
     * and Access Menu shortcuts bar keys (alt + number keys)
     * share cooldown interval.
     */
    private static final Interval[] FUNCTION_INTERVALS = new Interval[10];

    /**
     * Should be same order as {@link AccessMenuGUI#init()}
     */
    private static final MenuFunction[] FUNCTIONS = new MenuFunction[]{
            new MenuFunction(0, new IntervalKeystroke(KeyMappingsHandler.Keys.OPEN_CONFIG_MENU.mapping),
                    () -> Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, null).get())),
            new MenuFunction(1, new IntervalKeystroke(KeyMappingsHandler.Keys.NARRATE_TARGET.mapping),
                    AccessMenu::getBlockAndFluidTargetInformation),
            new MenuFunction(2, new IntervalKeystroke(KeyMappingsHandler.Keys.TARGET_POSITION.mapping),
                    AccessMenu::getBlockAndFluidTargetPosition),
            new MenuFunction(3, new IntervalKeystroke(KeyMappingsHandler.Keys.LIGHT_LEVEL.mapping),
                    AccessMenu::getLightLevel),
            new MenuFunction(4, new IntervalKeystroke(KeyMappingsHandler.Keys.CLOSEST_WATER_SOURCE.mapping),
                    () -> MainClass.fluidDetector.findClosestWaterSource(true)),
            new MenuFunction(5, new IntervalKeystroke(KeyMappingsHandler.Keys.CLOSEST_LAVA_SOURCE.mapping),
                    () -> MainClass.fluidDetector.findClosestLavaSource(true)),
            new MenuFunction(6, new IntervalKeystroke(KeyMappingsHandler.Keys.CURRENT_BIOME.mapping),
                    AccessMenu::getBiome),
            new MenuFunction(7, new IntervalKeystroke(KeyMappingsHandler.Keys.TIME_OF_DAY.mapping),
                    AccessMenu::getTimeOfDay),
            new MenuFunction(8, new IntervalKeystroke(KeyMappingsHandler.Keys.XP_LEVEL.mapping),
                    AccessMenu::getXP),
            new MenuFunction(9, new IntervalKeystroke(KeyMappingsHandler.Keys.REFRESH_SCREEN_READER.mapping),
                    () -> ScreenReaderController.refreshScreenReader(true)),
            new MenuFunction(10, new IntervalKeystroke(KeyMappingsHandler.Keys.WEATHER.mapping),
                    AccessMenu::getWeatherStatus),
    };

    static {
        // other functions get one second interval
        Arrays.fill(FUNCTION_INTERVALS, Interval.sec(1));
        for (int i = 0; i < 10; i++) {
            FUNCTIONS[i].keystroke.interval = FUNCTION_INTERVALS[i];
        }

        // the two long-time-running find-the-closest-liquid-source functions
        // are disabled in "alt + number keys" combination
        FUNCTION_INTERVALS[4] = Interval.ms(0);
        FUNCTION_INTERVALS[5] = Interval.ms(0);
    }

    public void tick() {
        if (CLIENT.player == null) return;

        if (CLIENT.screen == null) {
            if (CLIENT.hasAltDown()) {
                handleInMenuActions();
                return;
            }

            for (MenuFunction function : FUNCTIONS) {
                if (function.keystroke.canBeTriggered()) {
                    function.func.run();
                    return;
                }
            }

            if (MENU_KEY.canOpenMenu() && !gameModeSwitcherActive) {
                CLIENT.setScreen(new AccessMenuGUI());
            }
        } else if (CLIENT.screen instanceof AccessMenuGUI) {
            handleInMenuActions();
        }

        if (CLIENT.screen instanceof GameModeSwitcherScreen) {
            gameModeSwitcherActive = true;
        } else if (!InputConstants.isKeyDown(CLIENT.getWindow(), InputConstants.KEY_F4)) {
            gameModeSwitcherActive = false;
        }
    }

    private static void handleInMenuActions() {
        // With Access Menu opened or alt key pressed,
        // listen to number keys pressing for executing corresponding functions
        // for the little performance improvement, will not use KeyUtils here.
        Window window = CLIENT.getWindow();
        Stream.of(FUNCTIONS)
                .filter(f -> InputConstants.isKeyDown(window, f.number + InputConstants.KEY_0))
                .findFirst()
                .ifPresent(f -> {
                    if (FUNCTION_INTERVALS[f.number].isReady()) {
                        f.func().run();
                    }
                });
    }

    public static void getBlockAndFluidTargetInformation() {
        HitResult hit = PlayerUtils.crosshairTarget(RAY_CAST_DISTANCE);
        if (hit == null) return;
        switch (hit.getType()) {
            case MISS, ENTITY -> MainClass.narrate(I18n.get("minecraft_access.access_menu.target_missed"), true);
            case BLOCK -> {
                BlockHitResult blockHit = (BlockHitResult) hit;
                BlockPos blockPos = blockHit.getBlockPos();
                String narration = NarrationUtils.narrateBlock(blockPos, "") + ", " + NarrationUtils.narrateRelativePositionOfPlayerAnd(blockPos);
                MainClass.narrate(narration, true);
            }
        }
    }

    public static void getBlockAndFluidTargetPosition() {
        HitResult hit = PlayerUtils.crosshairTarget(RAY_CAST_DISTANCE);
        if (hit == null) return;
        switch (hit.getType()) {
            case MISS, ENTITY -> MainClass.narrate(I18n.get("minecraft_access.access_menu.target_missed"), true);
            case BLOCK -> {
                BlockHitResult blockHitResult = (BlockHitResult) hit;
                BlockPos blockPos = blockHitResult.getBlockPos();
                MainClass.narrate(NarrationUtils.narrateCoordinatesOf(blockPos), true);
            }
        }
    }

    public static void getLightLevel() {
        if (CLIENT.player == null) return;
        if (CLIENT.level == null) return;

        CLIENT.player.clientSideCloseContainer();

        int light = CLIENT.level.getMaxLocalRawBrightness(CLIENT.player.blockPosition());
        MainClass.narrate(I18n.get("minecraft_access.access_menu.light_level", NarrationUtils.narrateNumber(light)), true);
    }

    public static void getBiome() {
        if (CLIENT.player == null) return;

        CLIENT.player.clientSideCloseContainer();

        Holder<Biome> currentBiome = BiomeIndicator.getCurrentBiome();
        NarrationUtils.getTranslatedName(currentBiome, "biome")
                .ifPresent(name -> MainClass.narrate(I18n.get("minecraft_access.access_menu.biome", name), true));
    }

    public static void getXP() {
        if (CLIENT.player == null) return;

        CLIENT.player.clientSideCloseContainer();

        assert CLIENT.gameMode != null;
        if (CLIENT.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            MainClass.narrate(I18n.get("gameMode.spectator"), true);
            return;
        } else if (CLIENT.gameMode.getPlayerMode() == GameType.CREATIVE) {
            MainClass.narrate(I18n.get("gameMode.creative"), true);
            return;
        }

        MainClass.narrate(I18n.get("minecraft_access.access_menu.xp",
                        NarrationUtils.narrateNumber(CLIENT.player.experienceLevel),
                        NarrationUtils.narrateNumber(CLIENT.player.experienceProgress * 100)),
                true);
    }

    public static void getTimeOfDay() {
        if (CLIENT.player == null) return;
        if (CLIENT.level == null) return;

        CLIENT.player.clientSideCloseContainer();
        long daytime = CLIENT.player.level().getDayTime() + 6000;
        int hours = (int) (daytime / 1000) % 24;
        int minutes = (int) ((daytime % 1000) * 60 / 1000);

        StringBuilder translationKey = new StringBuilder("minecraft_access.access_menu.time_of_day");
        if (Config.getInstance().use12HourTimeFormat) {
            if (hours == 0) {
                hours = 12;
                translationKey.append("_am");
            } else if (hours > 12) {
                hours -= 12;
                translationKey.append("_pm");
            } else if (hours == 12) {
                translationKey.append("_pm");
            } else {
                translationKey.append("_am");
            }
        }

        String narration = I18n.get(translationKey.toString(), String.format("%02d:%02d", hours, minutes));
        MainClass.narrate(narration, true);
    }

    public static void getWeatherStatus() {
        Holder<Biome> currentBiome = BiomeIndicator.getCurrentBiome();
        Level level = CLIENT.level;
        if (CLIENT.player == null) return;
        if (level == null) return;
        if (currentBiome == null) return;

        Biome.Precipitation currentPrecipitation = currentBiome.value()
                .getPrecipitationAt(CLIENT.player.getOnPos(), level.getSeaLevel());

        CLIENT.player.clientSideCloseContainer();

        String weather;
        if (level.isRaining()) {
            weather = switch (currentPrecipitation) {
                case NONE -> I18n.get("minecraft_access.weather.clear");
                case RAIN -> {
                    if (level.isThundering()) {
                        yield I18n.get("minecraft_access.weather.thunder");
                    } else {
                        yield I18n.get("minecraft_access.weather.rain");
                    }
                }
                case SNOW -> I18n.get("minecraft_access.weather.snow");
                default -> {
                    log.warn("Unexpected Precipitation type in weather status.");
                    yield "";
                }
            };
        } else {
            weather = I18n.get("minecraft_access.weather.clear");
        }

        if (level.isMoonVisible()) {
            int moonPhase = level.getMoonPhase();
            weather += I18n.get("minecraft_access.other.words_connection") + I18n.get("minecraft_access.weather.moon_phase." + moonPhase);
        }

        MainClass.narrate(weather, false);
    }

    private record MenuFunction(int number, IntervalKeystroke keystroke, Runnable func) {
}
}
