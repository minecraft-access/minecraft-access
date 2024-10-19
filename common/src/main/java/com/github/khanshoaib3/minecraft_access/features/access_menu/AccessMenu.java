package com.github.khanshoaib3.minecraft_access.features.access_menu;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.ConfigMenu;
import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;
import com.github.khanshoaib3.minecraft_access.features.BiomeIndicator;
import com.github.khanshoaib3.minecraft_access.screen_reader.ScreenReaderController;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.NarrationUtils;
import com.github.khanshoaib3.minecraft_access.utils.PlayerUtils;
import com.github.khanshoaib3.minecraft_access.utils.condition.Interval;
import com.github.khanshoaib3.minecraft_access.utils.condition.IntervalKeystroke;
import com.github.khanshoaib3.minecraft_access.utils.condition.MenuKeystroke;
import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Opens a menu when F4 button is pressed (configurable) with helpful options.
 */
@Slf4j
public class AccessMenu {
    /**
     * Much farther than the Read Crosshair feature (6 blocks).
     */
    public static final double RAY_CAST_DISTANCE = 20.0;
    private static MinecraftClient minecraftClient;
    private static final MenuKeystroke menuKey = new MenuKeystroke(KeyBindingsHandler.getInstance().accessMenuKey);
    /**
     * Access Menu function direct keys (configured in keybinding settings)
     * and Access Menu shortcuts bar keys (alt + number keys)
     * share cooldown interval.
     */
    private static final Interval[] functionIntervals = new Interval[10];

    /**
     * Should be same order as {@link AccessMenuGUI#init()}
     */
    private static final MenuFunction[] FUNCTIONS = new MenuFunction[]{
            new MenuFunction(1, new IntervalKeystroke(KeyBindingsHandler.getInstance().narrateTarget),
                    AccessMenu::getBlockAndFluidTargetInformation),
            new MenuFunction(2, new IntervalKeystroke(KeyBindingsHandler.getInstance().targetPosition),
                    AccessMenu::getBlockAndFluidTargetPosition),
            new MenuFunction(3, new IntervalKeystroke(KeyBindingsHandler.getInstance().lightLevel),
                    AccessMenu::getLightLevel),
            new MenuFunction(4, new IntervalKeystroke(KeyBindingsHandler.getInstance().closestWaterSource),
                    () -> MainClass.fluidDetector.findClosestWaterSource(true)),
            new MenuFunction(5, new IntervalKeystroke(KeyBindingsHandler.getInstance().closestLavaSource),
                    () -> MainClass.fluidDetector.findClosestLavaSource(true)),
            new MenuFunction(6, new IntervalKeystroke(KeyBindingsHandler.getInstance().currentBiome),
                    AccessMenu::getBiome),
            new MenuFunction(7, new IntervalKeystroke(KeyBindingsHandler.getInstance().timeOfDay),
                    AccessMenu::getTimeOfDay),
            new MenuFunction(8, new IntervalKeystroke(KeyBindingsHandler.getInstance().xpLevel),
                    AccessMenu::getXP),
            new MenuFunction(9, new IntervalKeystroke(KeyBindingsHandler.getInstance().refreshScreenReader),
                    () -> ScreenReaderController.refreshScreenReader(true)),
            new MenuFunction(0, new IntervalKeystroke(KeyBindingsHandler.getInstance().openConfigMenu),
                    () -> MinecraftClient.getInstance().setScreen(new ConfigMenu("config_menu"))),
    };

    static {
        // other functions get one second interval
        Arrays.fill(functionIntervals, Interval.ms(1000));
        // two long-time-running find-the-closest-liquid-source functions need a long interval (10 seconds)
        functionIntervals[4] = Interval.ms(10_000);
        functionIntervals[5] = Interval.ms(10_000);

        for (int i = 0; i < 10; i++) {
            FUNCTIONS[i].keystroke.interval = functionIntervals[i];
        }
    }

    private record MenuFunction(int number, IntervalKeystroke keystroke, Runnable func) {
    }

    public void update() {
        try {
            minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;

            Screen currentScreen = minecraftClient.currentScreen;
            if (currentScreen == null) {
                if (Screen.hasAltDown()) {
                    handleInMenuActions();
                    return;
                }

                for (MenuFunction function : FUNCTIONS) {
                    if (function.keystroke.canBeTriggered()) {
                        function.func.run();
                        return;
                    }
                }

                // F3 + F4 triggers game mode changing function in vanilla game,
                // will not open the menu under this situation.
                boolean isF3KeyNotPressed = !KeyUtils.isF3Pressed();
                if (menuKey.canOpenMenu() && isF3KeyNotPressed) {
                    // The F4 is pressed before and released at current tick
                    // To make the access menu open AFTER release the F4 key
                    minecraftClient.setScreen(new AccessMenuGUI("access_menu"));
                }

            } else if (currentScreen instanceof AccessMenuGUI) {
                if (menuKey.closeMenuIfMenuKeyPressing()) return;
                handleInMenuActions();
            }
        } catch (Exception e) {
            log.error("An error occurred in NarratorMenu.", e);
        }
    }

    private static void handleInMenuActions() {
        // With Access Menu opened or alt key pressed,
        // listen to number keys pressing for executing corresponding functions
        // for the little performance improvement, will not use KeyUtils here.
        long handle = minecraftClient.getWindow().getHandle();
        Stream.of(FUNCTIONS)
                .filter(f -> InputUtil.isKeyPressed(handle, f.number + GLFW.GLFW_KEY_0))
                .findFirst()
                .ifPresent(f -> {
                    if (functionIntervals[f.number].isReady()) {
                        f.func().run();
                    }
                });
    }

    public static void getBlockAndFluidTargetInformation() {
        try {
            HitResult hit = PlayerUtils.crosshairTarget(RAY_CAST_DISTANCE);
            if (hit == null) return;
            switch (hit.getType()) {
                case MISS, ENTITY -> MainClass.speakWithNarrator(I18n.translate("minecraft_access.access_menu.target_missed"), true);
                case BLOCK -> {
                    try {
                        BlockHitResult blockHit = (BlockHitResult) hit;
                        BlockPos blockPos = blockHit.getBlockPos();
                        String text = NarrationUtils.narrateBlock(blockPos, "") + ", " + NarrationUtils.narrateRelativePositionOfPlayerAnd(blockPos);
                        MainClass.speakWithNarrator(text, true);
                    } catch (Exception e) {
                        log.error("An error occurred when speaking block information.", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("An error occurred when getting block and target information.", e);
        }
    }

    public static void getBlockAndFluidTargetPosition() {
        try {
            HitResult hit = PlayerUtils.crosshairTarget(RAY_CAST_DISTANCE);
            if (hit == null) return;
            switch (hit.getType()) {
                case MISS, ENTITY -> MainClass.speakWithNarrator(I18n.translate("minecraft_access.access_menu.target_missed"), true);
                case BLOCK -> {
                    try {
                        BlockHitResult blockHitResult = (BlockHitResult) hit;
                        BlockPos blockPos = blockHitResult.getBlockPos();
                        MainClass.speakWithNarrator(NarrationUtils.narrateCoordinatesOf(blockPos), true);
                    } catch (Exception e) {
                        log.error("An error occurred when speaking block position.", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("An error occurred when getting block and target position.", e);
        }
    }

    public static void getLightLevel() {
        try {
            if (minecraftClient.player == null) return;
            if (minecraftClient.world == null) return;

            minecraftClient.player.closeScreen();

            int light = minecraftClient.world.getLightLevel(minecraftClient.player.getBlockPos());
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.access_menu.light_level", light), true);
        } catch (Exception e) {
            log.error("An error occurred when getting light level.", e);
        }
    }

    public static void getBiome() {
        try {
            if (minecraftClient.player == null) return;
            if (minecraftClient.world == null) return;

            minecraftClient.player.closeScreen();

            RegistryEntry<Biome> var27 = minecraftClient.world.getBiome(minecraftClient.player.getBlockPos());
            String name = I18n.translate(BiomeIndicator.getBiomeName(var27));
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.access_menu.biome", name), true);
        } catch (Exception e) {
            log.error("An error occurred when getting biome.", e);
        }
    }

    public static void getXP() {
        try {
            if (minecraftClient.player == null) return;

            minecraftClient.player.closeScreen();

            MainClass.speakWithNarrator(I18n.translate("minecraft_access.access_menu.xp",
                            PlayerUtils.getExperienceLevel(),
                            PlayerUtils.getExperienceProgress()),
                    true);
        } catch (Exception e) {
            log.error("An error occurred when getting XP.", e);
        }
    }

    public static void getTimeOfDay() {
        try {
            if (minecraftClient.player == null) return;
            if (minecraftClient.world == null) return;

            minecraftClient.player.closeScreen();
            long daytime = minecraftClient.player.clientWorld.getTimeOfDay() + 6000;
            int hours = (int) (daytime / 1000) % 24;
            int minutes = (int) ((daytime % 1000) * 60 / 1000);

            String translationKey = "minecraft_access.access_menu.time_of_day";
            if (OtherConfigsMap.getInstance().isUse12HourTimeFormat()) {
                if (hours > 12) {
                    hours -= 12;
                    translationKey += "_pm";
                } else if (hours == 12) {
                    translationKey += "_pm";
                } else {
                    translationKey += "_am";
                }
            }

            String toSpeak = "%02d:%02d".formatted(hours, minutes);
            toSpeak = I18n.translate(translationKey, toSpeak);
            MainClass.speakWithNarrator(toSpeak, true);
        } catch (Exception e) {
            log.error("An error occurred while speaking time of day.", e);
        }
    }
}
