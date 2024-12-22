package com.github.khanshoaib3.minecraft_access.features.access_menu;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.ConfigMenu;
import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;
import com.github.khanshoaib3.minecraft_access.features.BiomeIndicator;
import com.github.khanshoaib3.minecraft_access.screen_reader.ScreenReaderController;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.NarrationUtils;
import com.github.khanshoaib3.minecraft_access.utils.PlayerUtils;
import com.github.khanshoaib3.minecraft_access.utils.condition.Keystroke;
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
    private static final MenuKeystroke menuKey;

    private static Keystroke narrateTargetKey;
    private static Keystroke targetPositionKey;
    private static Keystroke lightLevelKey;
    private static Keystroke currentBiomeKey;
    private static Keystroke xpLevelKey;
    private static Keystroke closestLavaSourceKey;
    private static Keystroke closestWaterSourceKey;
    private static Keystroke timeOfDayKey;
    private static Keystroke refreshScreenReaderKey;
    private static Keystroke openConfigMenuKey;

    // config keystroke conditions
    static {
        menuKey = new MenuKeystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().accessMenuKey));
        narrateTargetKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().narrateTarget));
        targetPositionKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().targetPosition));
        currentBiomeKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().currentBiome));
        lightLevelKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().lightLevel));
        closestLavaSourceKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().closestLavaSource));
        closestWaterSourceKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().closestWaterSource));
        timeOfDayKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().timeOfDay));
        xpLevelKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().xpLevel));
        refreshScreenReaderKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().refreshScreenReader));
        openConfigMenuKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().openConfigMenu));
    }

    /**
     * Should be same order as {@link AccessMenuGUI#init()}
     */
    private static final MenuFunction[] MENU_FUNCTIONS = new MenuFunction[]{
            new MenuFunction("minecraft_access.access_menu.gui.button.block_and_fluid_target_info",
                    GLFW.GLFW_KEY_1, GLFW.GLFW_KEY_KP_1,
                    AccessMenu::getBlockAndFluidTargetInformation),
            new MenuFunction("minecraft_access.access_menu.gui.button.block_and_fluid_target_position",
                    GLFW.GLFW_KEY_2, GLFW.GLFW_KEY_KP_2,
                    AccessMenu::getBlockAndFluidTargetPosition),
            new MenuFunction("minecraft_access.access_menu.gui.button.light_level",
                    GLFW.GLFW_KEY_3, GLFW.GLFW_KEY_KP_3,
                    AccessMenu::getLightLevel),
            new MenuFunction("minecraft_access.access_menu.gui.button.find_water",
                    GLFW.GLFW_KEY_4, GLFW.GLFW_KEY_KP_4,
                    () -> MainClass.fluidDetector.findClosestWaterSource(true)),
            new MenuFunction("minecraft_access.access_menu.gui.button.find_lava",
                    GLFW.GLFW_KEY_5, GLFW.GLFW_KEY_KP_5,
                    () -> MainClass.fluidDetector.findClosestLavaSource(true)),
            new MenuFunction("minecraft_access.access_menu.gui.button.biome",
                    GLFW.GLFW_KEY_6, GLFW.GLFW_KEY_KP_6,
                    AccessMenu::getBiome),
            new MenuFunction("minecraft_access.access_menu.gui.button.time_of_day",
                    GLFW.GLFW_KEY_7, GLFW.GLFW_KEY_KP_7,
                    AccessMenu::getTimeOfDay),
            new MenuFunction("minecraft_access.access_menu.gui.button.xp",
                    GLFW.GLFW_KEY_8, GLFW.GLFW_KEY_KP_8,
                    AccessMenu::getXP),
            new MenuFunction("minecraft_access.access_menu.gui.button.refresh_screen_reader",
                    GLFW.GLFW_KEY_9, GLFW.GLFW_KEY_KP_9,
                    () -> ScreenReaderController.refreshScreenReader(true)),
            new MenuFunction("minecraft_access.access_menu.gui.button.open_config_menu",
                    GLFW.GLFW_KEY_0, GLFW.GLFW_KEY_KP_0,
                    () -> MinecraftClient.getInstance().setScreen(new ConfigMenu("config_menu"))),
    };

    private record MenuFunction(String configKey, int numberKeyCode, int keyPadKeyCode, Runnable func) {
     }

    public void update() {
        try {
            minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;

            if (minecraftClient.currentScreen instanceof AccessMenuGUI) {
                if (menuKey.closeMenuIfMenuKeyPressing()) return;
                handleInMenuActions();
            }

            // other menus is opened
            if (minecraftClient.currentScreen != null) return;

            // F3 + F4 triggers game mode changing function in vanilla game,
            // will not open the menu under this situation.
            boolean isF3KeyNotPressed = !KeyUtils.isF3Pressed();

            if (!menuKey.isPressing() && menuKey.canOpenMenu() && isF3KeyNotPressed) {
                // The F4 is pressed before and released at current tick
                // To make the access menu open AFTER release the F4 key
                openAccessMenu();
            }

            if (narrateTargetKey.canBeTriggered()) getBlockAndFluidTargetInformation();

            if (targetPositionKey.canBeTriggered()) getBlockAndFluidTargetPosition();

            if (lightLevelKey.canBeTriggered()) getLightLevel();

            if (timeOfDayKey.canBeTriggered()) getTimeOfDay();

            if (xpLevelKey.canBeTriggered()) getXP();

            if (currentBiomeKey.canBeTriggered()) getBiome();

            if (closestWaterSourceKey.canBeTriggered()) MainClass.fluidDetector.findClosestWaterSource(true);

            if (closestLavaSourceKey.canBeTriggered()) MainClass.fluidDetector.findClosestLavaSource(true);

            if (refreshScreenReaderKey.canBeTriggered()) ScreenReaderController.refreshScreenReader(true);

            if (openConfigMenuKey.canBeTriggered()) MinecraftClient.getInstance().setScreen(new ConfigMenu("config_menu"));

            menuKey.updateStateForNextTick();
            narrateTargetKey.updateStateForNextTick();
            targetPositionKey.updateStateForNextTick();
            currentBiomeKey.updateStateForNextTick();
            xpLevelKey.updateStateForNextTick();
            closestWaterSourceKey.updateStateForNextTick();
            closestLavaSourceKey.updateStateForNextTick();
            lightLevelKey.updateStateForNextTick();
            timeOfDayKey.updateStateForNextTick();
            refreshScreenReaderKey.updateStateForNextTick();
            openConfigMenuKey.updateStateForNextTick();
        } catch (Exception e) {
            log.error("An error occurred in NarratorMenu.", e);
        }
    }

    private static void handleInMenuActions() {
        // With Narrator Menu opened, listen to number keys pressing for executing corresponding functions
        // for the little performance improvement, will not use KeyUtils here.
        long handle = minecraftClient.getWindow().getHandle();
        Stream.of(MENU_FUNCTIONS)
                .filter(f -> InputUtil.isKeyPressed(handle, f.numberKeyCode())
                        || InputUtil.isKeyPressed(handle, f.keyPadKeyCode()))
                .findFirst()
                .ifPresent(f -> f.func().run());
    }

    private void openAccessMenu() {
        Screen screen = new AccessMenuGUI("access_menu");
        minecraftClient.setScreen(screen); // post 1.18
    }

    public static void getBlockAndFluidTargetInformation() {
        try {
            HitResult hit = PlayerUtils.crosshairTarget(RAY_CAST_DISTANCE);
            if (hit == null) return;
            switch (hit.getType()) {
                case MISS, ENTITY ->
                        MainClass.speakWithNarrator(I18n.translate("minecraft_access.access_menu.target_missed"), true);
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
                case MISS, ENTITY ->
                        MainClass.speakWithNarrator(I18n.translate("minecraft_access.access_menu.target_missed"), true);
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
