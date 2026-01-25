package org.mcaccess.minecraftaccess;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.Balm;
import net.blay09.mods.balm.client.BalmClientRegistrars;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.platform.event.callback.PlayerCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.resources.Identifier;
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
import org.mcaccess.minecraftaccess.features.CreakingHeartLocationHint;
import org.mcaccess.minecraftaccess.features.FallDetector;
import org.mcaccess.minecraftaccess.features.HUDStatus;
import org.mcaccess.minecraftaccess.features.MenuFix;
import org.mcaccess.minecraftaccess.features.MouseSimulation;
import org.mcaccess.minecraftaccess.features.NarrateCrosshair;
import org.mcaccess.minecraftaccess.features.NarrateHeldItem;
import org.mcaccess.minecraftaccess.features.PlayerStatus;
import org.mcaccess.minecraftaccess.features.PositionNarrator;
import org.mcaccess.minecraftaccess.features.TimeIndicator;
import org.mcaccess.minecraftaccess.features.XPIndicator;
import org.mcaccess.minecraftaccess.features.inventory_controls.InventoryControls;
import org.mcaccess.minecraftaccess.features.point_of_interest.POIManager;
import org.mcaccess.minecraftaccess.mixin.GameNarratorAccessor;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderController;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderInterface;
import org.mcaccess.minecraftaccess.utils.events.ClientPlayingTick;

@Slf4j
public final class MainClass {
    public static final String MOD_ID = "minecraft_access";
    @Getter
    private static ScreenReaderInterface screenReader = null;
    private static final Map<Class<?>, Map<Identifier, Object>> REGISTRY = new HashMap<>();
    private static boolean frozen = false;

    public static POIManager poiManager = null;

    private MainClass() {
    }

    @SuppressWarnings("unchecked")
    public static <T> @UnmodifiableView Map<Identifier, T> registry(Class<T> registry) {
        return Collections.unmodifiableMap(REGISTRY.getOrDefault(registry, Collections.EMPTY_MAP));
    }

    @SuppressWarnings("unchecked")
    private static <T> Map<Identifier, T> mutableRegistry(Class<T> registry) {
        if (frozen) {
            throw new UnsupportedOperationException("Registries are frozen");
        }
        return (Map<Identifier, T>) REGISTRY.computeIfAbsent(registry, t -> new HashMap<>());
    }

    public static <T> void register(Class<T> registry, Identifier identifier, T value) {
        if (mutableRegistry(registry).putIfAbsent(identifier, value) != null) {
            throw new IllegalArgumentException(String.format("%s %s is already registered", registry.getSimpleName(), identifier));
        }
    }

    /**
     * Initializes the mod.
     */
    public static void init(BalmClientRegistrars registrars, List<Addon> addons) {
        String startupMessage = "Initializing Minecraft Access: version " + Balm.platform().getModInfo(MOD_ID).get().versionString();
        log.info(startupMessage);

        new AutoLibrarySetup().initialize();

        ClientPlayingTick.AFTER.configureMapping((priority, callback) -> {
            ClientTickCallback.AFTER.register(priority, client -> {
                if (client.player != null && client.level != null) {
                    callback.handle(client, client.player, client.level);
                }
            });
        });

        ClientTickCallback.AFTER.register(MainClass::clientTickEventsMethod);

        if (Balm.platform().isDevelopmentEnvironment()) {
            ServerList list = new ServerList(Minecraft.getInstance());
            list.load();

            ServerData testServer = new ServerData(
                    "Test Server",
                    "test.mcaccess.org",
                    ServerData.Type.OTHER
            );

            if (list.get(testServer.ip) == null) {
                testServer.setResourcePackStatus(ServerData.ServerPackStatus.ENABLED);
                list.add(testServer, false);
                list.save();
            }
        }

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

        ScreenReaderController.refreshScreenReader();
        if (getScreenReader() != null && getScreenReader().isInitialized()) {
            getScreenReader().narrate(startupMessage, true);
        }

        registrars.registerModule(new AccessMenu());
        registrars.registerModule(new BiomeIndicator());
        registrars.registerModule(new CameraControls());
        registrars.registerModule(new CreakingHeartLocationHint());
        registrars.registerModule(new FallDetector());
        registrars.registerModule(new HUDStatus());
        registrars.registerModule(new InventoryControls());
        registrars.registerModule(new MenuFix());
        registrars.registerModule(new MouseSimulation());
        registrars.registerModule(new NarrateCrosshair());
        registrars.registerModule(new NarrateHeldItem());
        registrars.registerModule(new PlayerStatus());
        poiManager = new POIManager();
        registrars.registerModule(poiManager.lockingHandler);
        registrars.registerModule(poiManager.objectTracker);
        registrars.registerModule(poiManager.poiBlocks);
        registrars.registerModule(poiManager.poiEntities);
        registrars.registerModule(poiManager.poiMarking);
        registrars.registerModule(new PositionNarrator());
        registrars.registerModule(new TimeIndicator());
        registrars.registerModule(new XPIndicator());
    }

    /**
     * This method gets called at the end of every tick.
     *
     * @param client The current minecraft client object
     */
    public static void clientTickEventsMethod(Minecraft client) {
        changeLogLevelBaseOnDebugConfig();
    }

    /**
     * Dynamically changing log level based on debug mode config.
     */
    private static void changeLogLevelBaseOnDebugConfig() {
        if (Config.getInstance().debugMode || Balm.platform().isDevelopmentEnvironment()) {
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
        Minecraft client = Minecraft.getInstance();

        if (Strings.isEmpty(text) || !client.isWindowActive()) {
            log.warn("The narration of string \"{}\" with interrupt={} was suppressed", text, interrupt);
            return;
        }

        if (client.options.narrator().get() != NarratorStatus.OFF) {
            ((GameNarratorAccessor) client.getNarrator()).invokeNarrateMessage(text, interrupt);
        }
    }

    public record Addon(String modid, MinecraftAccessAddon addon) {
    }
}
