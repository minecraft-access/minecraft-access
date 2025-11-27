package org.mcaccess.minecraftaccess;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientPlayerEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
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
import org.jetbrains.annotations.UnmodifiableView;

import org.mcaccess.minecraftaccess.api.AddonRegistry;
import org.mcaccess.minecraftaccess.api.MinecraftAccessAddon;
import org.mcaccess.minecraftaccess.features.AccessMenu;
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
import org.mcaccess.minecraftaccess.features.inventory_controls.InventoryControls;
import org.mcaccess.minecraftaccess.features.NarrateCrosshair;
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
    private static final Map<Class<?>, Map<ResourceLocation, Object>> REGISTRY = new HashMap<>();
    private static boolean frozen = false;

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

    @SuppressWarnings("unchecked")
    public static <T> @UnmodifiableView Map<ResourceLocation, T> registry(Class<T> registry) {
        return Collections.unmodifiableMap(REGISTRY.getOrDefault(registry, Collections.EMPTY_MAP));
    }

    @SuppressWarnings("unchecked")
    private static <T> Map<ResourceLocation, T> mutableRegistry(Class<T> registry) {
        if (frozen) {
            throw new UnsupportedOperationException("Registries are frozen");
        }
        return (Map<ResourceLocation, T>) REGISTRY.computeIfAbsent(registry, t -> new HashMap<>());
    }

    public static <T> void register(Class<T> registry, ResourceLocation identifier, T value) {
        if (mutableRegistry(registry).putIfAbsent(identifier, value) != null) {
            throw new IllegalArgumentException(String.format("%s %s is already registered", registry.getSimpleName(), identifier));
        }
    }

    /**
     * Initializes the mod
     */
    public static void init(List<Addon> addons) {
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
            addon.addon().init(new AddonRegistry(addon.modid()));
        }
        frozen = true;
        Config.init();
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
