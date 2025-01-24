package org.mcaccess.minecraftaccess;

import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.config.config_maps.AccessMenuConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.InventoryControlsConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.OtherConfigsMap;
import org.mcaccess.minecraftaccess.config.config_maps.PlayerWarningConfigMap;
import org.mcaccess.minecraftaccess.features.*;
import org.mcaccess.minecraftaccess.features.access_menu.AccessMenu;
import org.mcaccess.minecraftaccess.features.inventory_controls.InventoryControls;
import org.mcaccess.minecraftaccess.features.point_of_interest.POIMarking;
import org.mcaccess.minecraftaccess.features.read_crosshair.ReadCrosshair;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderController;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderInterface;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
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

    public static InventoryControls inventoryControls = null;
    public static BiomeIndicator biomeIndicator = null;
    public static XPIndicator xpIndicator = null;
    public static FacingDirection facingDirection = null;
    public static PlayerStatus playerStatus = null;
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

        MainClass.inventoryControls = new InventoryControls();
        MainClass.biomeIndicator = new BiomeIndicator();
        MainClass.xpIndicator = new XPIndicator();
        MainClass.facingDirection = new FacingDirection();
        MainClass.playerStatus = new PlayerStatus();
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

        ReadCrosshair.getInstance().tick();

        if (xpIndicator != null && otherConfigsMap.isXpIndicatorEnabled())
            xpIndicator.update();

        if (biomeIndicator != null && otherConfigsMap.isBiomeIndicatorEnabled())
            biomeIndicator.update();

        facingDirection.update();

        PositionNarrator.getInstance().update();

        if (MinecraftClient.getInstance() != null && WorldUtils.getClientPlayer() != null) {
            if (playerStatus != null && otherConfigsMap.isPlayerStatusEnabled()) {
                playerStatus.update();
            }

            MouseKeySimulation.runOnTick();

            if (MinecraftClient.getInstance().currentScreen == null) {
                // These features are suppressed when there is any screen opening
                CameraControls.update();
            }
        }

        if (playerWarnings != null && PlayerWarningConfigMap.getInstance().isEnabled())
            playerWarnings.update();

        if (accessMenu != null && AccessMenuConfigMap.getInstance().isEnabled())
            accessMenu.update();

        // POI Marking will handle POI Scan and POI Locking features inside it
        POIMarking.getInstance().update();

        FallDetector.getInstance().update();

        Keystroke.updateInstances();

        EffectNarration.getInstance().update();

        HUDStatus.getInstance().update();
    }

    /**
     * Dynamically changing log level based on debug mode config.
     */
    private static void changeLogLevelBaseOnDebugConfig() {
        boolean debugMode = OtherConfigsMap.getInstance().isDebugMode();
        if (debugMode) {
            if (!log.isDebugEnabled()) {
                Configurator.setLevel("org.mcaccess.minecraftaccess", Level.DEBUG);
            }
        } else if (log.isDebugEnabled()) {
            Configurator.setLevel("org.mcaccess.minecraftaccess", Level.INFO);
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
