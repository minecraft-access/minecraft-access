package org.mcaccess.minecraftaccess;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.util.Strings;
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
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;

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

    public static boolean isNeoForge = Platform.isNeoForge();
    public static boolean interrupt = true;
    private static boolean alreadyDisabledAdvancementKey = false;

    /**
     * Initializes the mod
     */
    public static void init() {
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

        for (KeyMapping km : KeyBindingsHandler.getInstance().getKeys()) {
            KeyMappingRegistry.register(km);
        }

        ClientTickEvent.CLIENT_POST.register(MainClass::clientTickEventsMethod);

        // This executes when minecraft closes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (MainClass.getScreenReader() != null && MainClass.getScreenReader().isInitialized())
                MainClass.getScreenReader().closeScreenReader();
        }, "Shutdown-thread"));
    }

    public static void clientTickEventsMethod(Minecraft minecraftClient) {
        OtherConfigsMap otherConfigsMap = OtherConfigsMap.getInstance();

        changeLogLevelBaseOnDebugConfig();

        if (!MainClass.alreadyDisabledAdvancementKey && minecraftClient.options != null) {
            minecraftClient.options.keyAdvancements.setKey(InputConstants.getKey("key.keyboard.unknown"));
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

        if (Minecraft.getInstance() != null && WorldUtils.getClientPlayer() != null) {
            if (playerStatus != null && otherConfigsMap.isPlayerStatusEnabled()) {
                playerStatus.update();
            }

            MouseKeySimulation.runOnTick();

            if (Minecraft.getInstance().screen == null) {
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

        HUDStatus.getInstance().update();
    }

    /**
     * Dynamically changing log level based on debug mode config.
     */
    private static void changeLogLevelBaseOnDebugConfig() {
        boolean debugMode = OtherConfigsMap.getInstance().isDebugMode() || Platform.isDevelopmentEnvironment();
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
        // Remove formatting codes
        // ref: https://minecraft.wiki/w/Formatting_codes
        text = text.replaceAll("§.", "");
        MainClass.interrupt = interrupt;
        Minecraft.getInstance().getNarrator().sayNow(text);
    }

    public static void speakWithNarratorIfNotEmpty(String text, boolean interrupt) {
        if (Strings.isNotEmpty(text)) {
            speakWithNarrator(text, interrupt);
        }
    }
}
