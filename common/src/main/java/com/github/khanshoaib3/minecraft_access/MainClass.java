package com.github.khanshoaib3.minecraft_access;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.config_maps.*;
import com.github.khanshoaib3.minecraft_access.features.*;
import com.github.khanshoaib3.minecraft_access.features.inventory_controls.InventoryControls;
import com.github.khanshoaib3.minecraft_access.features.access_menu.AccessMenu;
import com.github.khanshoaib3.minecraft_access.features.point_of_interest.POIMarking;
import com.github.khanshoaib3.minecraft_access.features.read_crosshair.ReadCrosshair;
import com.github.khanshoaib3.minecraft_access.screen_reader.ScreenReaderController;
import com.github.khanshoaib3.minecraft_access.screen_reader.ScreenReaderInterface;
import com.mojang.text2speech.Narrator;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.util.Strings;

@Slf4j
public class MainClass {
    public static final String MOD_ID = "minecraft_access";
    private static ScreenReaderInterface screenReader = null;

    public static CameraControls cameraControls = null;
    public static InventoryControls inventoryControls = null;
    public static BiomeIndicator biomeIndicator = null;
    public static XPIndicator xpIndicator = null;
    public static FacingDirection facingDirection = null;
    public static HealthNHunger healthNHunger = null;
    public static PlayerWarnings playerWarnings = null;
    public static AccessMenu accessMenu = null;
    public static FluidDetector fluidDetector = null;

    public static boolean isNeoForge = false;
    public static boolean interrupt = true;
    private static boolean alreadyDisabledAdvancementKey = false;

    /**
     * Initializes the mod
     */
    public static void init() {
        try {
            _init();
        } catch (Exception e) {
            log.error("An error occurred while initializing Minecraft Access.", e);
        }
    }

    private static void _init() {
        Config.getInstance().loadConfig();

        String msg = "Initializing Minecraft Access";
        log.info(msg);

        new AutoLibrarySetup().initialize();

        ScreenReaderController.refreshScreenReader();
        if (MainClass.getScreenReader() != null && MainClass.getScreenReader().isInitialized())
            MainClass.getScreenReader().say(msg, true);

        MainClass.cameraControls = new CameraControls();
        MainClass.inventoryControls = new InventoryControls();
        MainClass.biomeIndicator = new BiomeIndicator();
        MainClass.xpIndicator = new XPIndicator();
        MainClass.facingDirection = new FacingDirection();
        MainClass.healthNHunger = new HealthNHunger();
        MainClass.playerWarnings = new PlayerWarnings();
        MainClass.accessMenu = new AccessMenu();
        MainClass.fluidDetector = new FluidDetector();

        // This executes when minecraft closes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (MainClass.getScreenReader() != null && MainClass.getScreenReader().isInitialized())
                MainClass.getScreenReader().closeScreenReader();
        }, "Shutdown-thread"));
    }

    /**
     * This method gets called at the end of every tick
     *
     * @param minecraftClient The current minecraft client object
     */
    public static void clientTickEventsMethod(MinecraftClient minecraftClient) {
        try {
            _clientTickEventsMethod(minecraftClient);
        } catch (Exception e) {
            log.error("An error occurred while running Minecraft Access client tick events", e);
        }
    }

    private static void _clientTickEventsMethod(MinecraftClient minecraftClient) {
        OtherConfigsMap otherConfigsMap = OtherConfigsMap.getInstance();

        changeLogLevelBaseOnDebugConfig();

        // TODO change attack and use keys on startup and add startup features to config.json
        if (!MainClass.alreadyDisabledAdvancementKey && minecraftClient.options != null) {
            minecraftClient.options.advancementsKey.setBoundKey(InputUtil.fromTranslationKey("key.keyboard.unknown"));
            MainClass.alreadyDisabledAdvancementKey = true;
            log.info("Unbound advancements key");
        }

        if (otherConfigsMap.isMenuFixEnabled()) {
            MenuFix.update(minecraftClient);
        }

        // TODO Update these to singleton design pattern
        if (inventoryControls != null && InventoryControlsConfigMap.getInstance().isEnabled())
            inventoryControls.update();

        if (cameraControls != null && CameraControlsConfigMap.getInstance().isEnabled())
            cameraControls.update();

        ReadCrosshair.getInstance().tick();

        if (biomeIndicator != null && otherConfigsMap.isBiomeIndicatorEnabled())
            biomeIndicator.update();

        if (xpIndicator != null && otherConfigsMap.isXpIndicatorEnabled())
            xpIndicator.update();

        facingDirection.update();

        PositionNarrator.getInstance().update();

        if (healthNHunger != null && otherConfigsMap.isHealthNHungerEnabled())
            healthNHunger.update();

        if (playerWarnings != null && PlayerWarningConfigMap.getInstance().isEnabled())
            playerWarnings.update();

        if (accessMenu != null && NarratorMenuConfigMap.getInstance().isEnabled())
            accessMenu.update();

        // POI Marking will handle POI Scan and POI Locking features inside it
        POIMarking.getInstance().update();

        FallDetector.getInstance().update();

        MouseKeySimulation.getInstance().update();

        // TODO remove feature flag after complete
        // AreaMapMenu.getInstance().update();
    }

    /**
     * Dynamically changing log level based on debug mode config.
     */
    private static void changeLogLevelBaseOnDebugConfig() {
        boolean debugMode = OtherConfigsMap.getInstance().isDebugMode();
        if (debugMode) {
            if (!log.isDebugEnabled()) {
                Configurator.setLevel("com.github.khanshoaib3.minecraft_access", Level.DEBUG);
            }
        } else if (log.isDebugEnabled()) {
            Configurator.setLevel("com.github.khanshoaib3.minecraft_access", Level.INFO);
        }
    }

    public static ScreenReaderInterface getScreenReader() {
        return MainClass.screenReader;
    } //TODO remove this

    public static void setScreenReader(ScreenReaderInterface screenReader) {
        MainClass.screenReader = screenReader;
    }

    public static void speakWithNarrator(String text, boolean interrupt) {
        MainClass.interrupt = interrupt;
        if (isNeoForge) {
            MinecraftClient.getInstance().getNarratorManager().narrate(text);
            return;
        }

        Narrator.getNarrator().say(text, interrupt);
    }

    public static void speakWithNarratorIfNotEmpty(String text, boolean interrupt) {
        if (Strings.isNotEmpty(text)) {
            speakWithNarrator(text, interrupt);
        }
    }
}
