package org.mcaccess.minecraftaccess;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.player.LocalPlayer;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.util.Strings;

import org.mcaccess.minecraftaccess.features.AutoLibrarySetup;
import org.mcaccess.minecraftaccess.features.BiomeIndicator;
import org.mcaccess.minecraftaccess.features.CameraControls;
import org.mcaccess.minecraftaccess.features.FacingDirection;
import org.mcaccess.minecraftaccess.features.FallDetector;
import org.mcaccess.minecraftaccess.features.FluidDetector;
import org.mcaccess.minecraftaccess.features.HUDStatus;
import org.mcaccess.minecraftaccess.features.MenuFix;
import org.mcaccess.minecraftaccess.features.MouseKeySimulation;
import org.mcaccess.minecraftaccess.features.NarrateHeldItem;
import org.mcaccess.minecraftaccess.features.PlayerStatus;
import org.mcaccess.minecraftaccess.features.PlayerWarnings;
import org.mcaccess.minecraftaccess.features.PositionNarrator;
import org.mcaccess.minecraftaccess.features.XPIndicator;
import org.mcaccess.minecraftaccess.features.access_menu.AccessMenu;
import org.mcaccess.minecraftaccess.features.inventory_controls.InventoryControls;
import org.mcaccess.minecraftaccess.features.narrate_crosshair.NarrateCrosshair;
import org.mcaccess.minecraftaccess.features.point_of_interest.POIManager;
import org.mcaccess.minecraftaccess.mixin.GameNarratorAccessor;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderController;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderInterface;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;

@Slf4j
public final class MainClass {
    public static final String MOD_ID = "minecraft_access";
    @Getter
    private static ScreenReaderInterface screenReader = null;

    public static AccessMenu accessMenu = null;
    public static BiomeIndicator biomeIndicator = null;
    public static FacingDirection facingDirection = null;
    public static FallDetector fallDetector = null;
    public static FluidDetector fluidDetector = null;
    public static HUDStatus hudStatus = null;
    public static InventoryControls inventoryControls = null;
    public static NarrateCrosshair narrateCrosshair = null;
    public static NarrateHeldItem narrateHeldItem = null;
    public static PlayerStatus playerStatus = null;
    public static PlayerWarnings playerWarnings = null;
    public static POIManager poiManager = null;
    public static XPIndicator xpIndicator = null;

    private MainClass() {
    }

    /**
     * Initializes the mod
     */
    public static void init() {
        Config.init();

        String startupMessage = "Initializing Minecraft Access: version " + Platform.getMod(MOD_ID).getVersion();
        log.info(startupMessage);

        new AutoLibrarySetup().initialize();

        ScreenReaderController.refreshScreenReader();
        if (getScreenReader() != null && getScreenReader().isInitialized()) {
            getScreenReader().narrate(startupMessage, true);
        }

        for (KeyBindingsHandler key : KeyBindingsHandler.values()) {
            KeyMappingRegistry.register(key.mapping);
        }

        ClientTickEvent.CLIENT_POST.register(MainClass::clientTickEventsMethod);
        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(MainClass::initWorldState);

        // This executes when minecraft closes
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (getScreenReader() != null && getScreenReader().isInitialized()) {
                getScreenReader().closeScreenReader();
            }
        }, "Shutdown-thread"));
    }

    /**
     * This method gets called at the end of every tick
     *
     * @param minecraftClient The current minecraft client object
     */
    public static void clientTickEventsMethod(Minecraft minecraftClient) {
        Config config = Config.getInstance();

        changeLogLevelBaseOnDebugConfig();

        if (config.menuFixEnabled) {
            MenuFix.tick(minecraftClient);
        }

        if (minecraftClient.level == null) {
            return;
        }

        if (inventoryControls != null && config.inventoryControls.enabled) {
            inventoryControls.tick();
        }

        narrateCrosshair.tick();

        if (xpIndicator != null && config.features.xpIndicatorEnabled && (PlayerUtils.isAdventure() || PlayerUtils.isSurvival())) {
            xpIndicator.tick();
        }

        if (biomeIndicator != null && config.features.biomeIndicatorEnabled) {
            biomeIndicator.tick();
        }

        facingDirection.tick();

        PositionNarrator.getINSTANCE().tick();

        if (WorldUtils.getClientPlayer() != null) {
            if (playerStatus != null) {
                playerStatus.tick();
            }

            if (!PlayerUtils.isPlayerTyping()) {
                MouseKeySimulation.tick();
            }

            if (minecraftClient.screen == null) {
                // These features are suppressed when there is any screen opening
                CameraControls.tick();
            }
        }

        if (playerWarnings != null && config.playerWarnings.enabled && (PlayerUtils.isSurvival() || PlayerUtils.isAdventure())) {
            playerWarnings.tick();
        }

        if (accessMenu != null && config.accessMenu.enabled) {
            accessMenu.tick();
        }

        if (!PlayerUtils.isSpectator()) {
            narrateHeldItem.tick();
        }

        poiManager.tick();

        fallDetector.tick();

        hudStatus.tick();

        // This should always be at the bottom
        Keystroke.updateInstances();
    }

    private static void initWorldState(LocalPlayer player) {
        accessMenu = new AccessMenu();
        biomeIndicator = new BiomeIndicator();
        facingDirection = new FacingDirection();
        fallDetector = new FallDetector();
        fluidDetector = new FluidDetector();
        hudStatus = new HUDStatus();
        inventoryControls = new InventoryControls();
        narrateCrosshair = new NarrateCrosshair();
        narrateHeldItem = new NarrateHeldItem();
        playerStatus = new PlayerStatus();
        playerWarnings = new PlayerWarnings();
        poiManager = new POIManager();
        xpIndicator = new XPIndicator();

        Minecraft client = Minecraft.getInstance();
        if (client.options.keyAdvancements.same(KeyBindingsHandler.CAMERA_CONTROLS_RIGHT.mapping)) {
            client.options.keyAdvancements.setKey(InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_O));
            client.options.save();
            client.options.load();
            log.info("Rebound advancements key");
        }
    }

    /**
     * Dynamically changing log level based on debug mode config.
     */
    private static void changeLogLevelBaseOnDebugConfig() {
        boolean debugMode = Config.getInstance().debugMode || Platform.isDevelopmentEnvironment();
        if (debugMode) {
            if (!log.isDebugEnabled()) {
                Configurator.setLevel("org.mcaccess.minecraftaccess", Level.DEBUG);
            }
        } else if (log.isDebugEnabled()) {
            Configurator.setLevel("org.mcaccess.minecraftaccess", Level.INFO);
        }
    }

    public static void setScreenReader(ScreenReaderInterface screenReader) {
        MainClass.screenReader = screenReader;
    }

    public static void narrate(String text, boolean interrupt) {
        if (Strings.isEmpty(text) || !Minecraft.getInstance().isWindowActive()) {
            log.warn("The narration of string \"{}\" with interrupt={} was suppressed", text, interrupt);
            return;
        }
        if (Minecraft.getInstance().options.narrator().get() != NarratorStatus.OFF) {
            ((GameNarratorAccessor) Minecraft.getInstance().getNarrator()).invokeNarrateMessage(text, interrupt);
        }
    }
}
