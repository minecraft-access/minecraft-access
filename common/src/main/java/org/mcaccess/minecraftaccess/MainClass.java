package org.mcaccess.minecraftaccess;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.util.Strings;

import org.mcaccess.minecraftaccess.addon.CoreAddon;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.api.AddonRegistry;
import org.mcaccess.minecraftaccess.api.MinecraftAccessAddon;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.features.AutoLibrarySetup;
import org.mcaccess.minecraftaccess.features.BiomeIndicator;
import org.mcaccess.minecraftaccess.features.CameraControls;
import org.mcaccess.minecraftaccess.features.FacingDirection;
import org.mcaccess.minecraftaccess.features.FallDetector;
import org.mcaccess.minecraftaccess.features.HUDStatus;
import org.mcaccess.minecraftaccess.features.MenuFix;
import org.mcaccess.minecraftaccess.features.MouseKeySimulation;
import org.mcaccess.minecraftaccess.features.NarrateHeldItem;
import org.mcaccess.minecraftaccess.features.PlayerStatus;
import org.mcaccess.minecraftaccess.features.PositionNarrator;
import org.mcaccess.minecraftaccess.features.XPIndicator;
import org.mcaccess.minecraftaccess.features.AccessMenu;
import org.mcaccess.minecraftaccess.features.inventory_controls.InventoryControls;
import org.mcaccess.minecraftaccess.features.narrate_crosshair.NarrateCrosshair;
import org.mcaccess.minecraftaccess.features.point_of_interest.POIManager;
import org.mcaccess.minecraftaccess.mixin.GameNarratorAccessor;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderController;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderInterface;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;

@Slf4j
public final class MainClass {
    private static final Minecraft CLIENT = Minecraft.getInstance();
    public static final String MOD_ID = "minecraft_access";
    @Getter
    private static ScreenReaderInterface screenReader = null;

    public static final Map<ResourceLocation, Status> STATUS_REGISTRY = new LinkedHashMap<>();
    public static final Map<ResourceLocation, AccessMenu.RegisteredFunction> ACCESS_MENU_REGISTRY = new LinkedHashMap<>();

    public static AccessMenu accessMenu = null;
    public static BiomeIndicator biomeIndicator = null;
    public static FacingDirection facingDirection = null;
    public static FallDetector fallDetector = null;
    public static HUDStatus hudStatus = null;
    public static InventoryControls inventoryControls = null;
    public static NarrateCrosshair narrateCrosshair = null;
    public static NarrateHeldItem narrateHeldItem = null;
    public static PlayerStatus playerStatus = null;
    public static POIManager poiManager = null;
    public static XPIndicator xpIndicator = null;

    private MainClass() {
    }

    /**
     * Initializes the mod
     */
    public static void init(List<Addon> addons) {
        Config.init();

        String startupMessage = "Initializing Minecraft Access: version " + Platform.getMod(MOD_ID).getVersion();
        log.info(startupMessage);

        new AutoLibrarySetup().initialize();

        ScreenReaderController.refreshScreenReader();
        if (getScreenReader() != null && getScreenReader().isInitialized()) {
            getScreenReader().narrate(startupMessage, true);
        }

        for (KeyBindingsHandler.Keys key : KeyBindingsHandler.Keys.values()) {
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

        for (Addon addon : addons) {
            AddonRegistry registry = new AddonRegistry(MOD_ID);
            addon.addon.init(registry);
            STATUS_REGISTRY.putAll(registry.getStatuses());
            for (Map.Entry<ResourceLocation, AccessMenuFunction> function : registry.getAccessMenuOptions().entrySet()) {
                KeyMapping key = new KeyMapping(
                        function.getKey().toLanguageKey("access_menu_function"),
                        InputConstants.Type.KEYSYM,
                        InputConstants.UNKNOWN.getValue(),
                        KeyBindingsHandler.Categories.ACCESS_MENU.category
                );
                KeyMappingRegistry.register(key);
                ACCESS_MENU_REGISTRY.put(function.getKey(), new AccessMenu.RegisteredFunction(function.getValue(), key));
            }
        }
    }

    /**
     * This method gets called at the end of every tick
     *
     * @param client The current minecraft client object
     */
    public static void clientTickEventsMethod(Minecraft client) {
        Config config = Config.getInstance();

        changeLogLevelBaseOnDebugConfig();

        if (config.menuFixEnabled) {
            MenuFix.tick(client);
        }

        if (client.level == null || client.player == null) {
            Keystroke.updateInstances();
            return;
        }

        narrateCrosshair.tick();
        facingDirection.tick();
        PositionNarrator.getINSTANCE().tick();
        poiManager.tick();
        fallDetector.tick();
        hudStatus.tick();

        if (client.screen == null || !(client.screen.getFocused() instanceof EditBox || client.screen instanceof KeyBindsScreen)) {
            MouseKeySimulation.tick();
        }

        if (inventoryControls != null && config.inventoryControls.enabled) {
            inventoryControls.tick();
        }

        assert client.gameMode != null;
        GameType mode = client.gameMode.getPlayerMode();

        if (xpIndicator != null && config.features.xpIndicatorEnabled && (mode == GameType.SURVIVAL || mode == GameType.ADVENTURE)) {
            xpIndicator.tick();
        }

        if (biomeIndicator != null && config.features.biomeIndicatorEnabled) {
            biomeIndicator.tick();
        }

        if (playerStatus != null) {
            playerStatus.tick();
        }

        if (client.screen == null) {
            CameraControls.tick();
        }

        if (accessMenu != null && config.accessMenu.enabled) {
            accessMenu.tick();
        }

        if (mode != GameType.SPECTATOR) {
            narrateHeldItem.tick();
        }

        // Must always remain at the end of client tick
        Keystroke.updateInstances();
    }

    private static void initWorldState(LocalPlayer player) {
        accessMenu = new AccessMenu();
        biomeIndicator = new BiomeIndicator();
        facingDirection = new FacingDirection();
        fallDetector = new FallDetector();
        hudStatus = new HUDStatus();
        inventoryControls = new InventoryControls();
        narrateCrosshair = new NarrateCrosshair();
        narrateHeldItem = new NarrateHeldItem();
        playerStatus = new PlayerStatus();
        poiManager = new POIManager();
        xpIndicator = new XPIndicator();

        if (CLIENT.options.keyAdvancements.same(KeyBindingsHandler.Keys.CAMERA_CONTROLS_RIGHT.mapping)) {
            CLIENT.options.keyAdvancements.setKey(InputConstants.Type.KEYSYM.getOrCreate(InputConstants.KEY_O));
            CLIENT.options.save();
            CLIENT.options.load();
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
        if (Strings.isEmpty(text) || !CLIENT.isWindowActive()) {
            log.warn("The narration of string \"{}\" with interrupt={} was suppressed", text, interrupt);
            return;
        }
        if (CLIENT.options.narrator().get() != NarratorStatus.OFF) {
            ((GameNarratorAccessor) CLIENT.getNarrator()).invokeNarrateMessage(text, interrupt);
        }
    }

    public record Addon(String modid, MinecraftAccessAddon addon) {
    }
}
