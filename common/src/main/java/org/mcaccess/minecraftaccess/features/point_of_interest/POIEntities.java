package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.condition.Interval;

/**
 * Scans the area for entities, groups them and plays a sound at their location.
 */
@Slf4j
public class POIEntities implements BalmClientModule {
    private Config.POI.Entities config;
    private final Interval interval = Interval.defaultDelay();

    private @Nullable Class<? extends Entity> marked = null;

    private final POIGroup<Entity> markedGroup = new POIGroup<>(
            "minecraft_access.point_of_interest.group.markedEntity",
            new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5.0f),
            e -> marked != null && marked.isInstance(e)
    );

    private final POIGroup<Entity> otherEntitiesGroup = new POIGroup<>(
            "minecraft_access.point_of_interest.group.otherEntities",
            entity -> true
    );

    @SuppressWarnings("unchecked")
    final POIGroup<Entity>[] groups = Stream.of(List.of(markedGroup), BuiltinEntityPOIGroups.ALL, List.of(otherEntitiesGroup))
            .flatMap(Collection::stream).toArray(POIGroup[]::new);

    @Getter
    private List<Entity> lastScanResults = new ArrayList<>();

    POIEntities() {
        loadConfig();
    }

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "poi/entities");
    }

    @Override
    public void initialize() {
        ClientTickCallback.ClientPlayerTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            marked = null;
            lastScanResults = new ArrayList<>();
        });
    }

    private void tick(Player player) {
        setMarked(MainClass.poiManager.poiMarking.getMarkedEntity());
        loadConfig();

        if (!config.enabled) return;
        if (!interval.isReady()) return;

        if (Minecraft.getInstance().screen != null) return; //Prevent running if any screen is opened

        log.trace("POIEntities started");
        scanEntitiesAroundPlayer();
        playerSoundAtFoundPOI(MainClass.poiManager.poiMarking.isMarked());
        log.trace("POIEntities ended");
    }

    private void scanEntitiesAroundPlayer() {
        // initialize
        List<Entity> currentScanResults = new ArrayList<>();
        for (POIGroup<Entity> group : groups) {
            group.clear();
        }

        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        AABB scanBox = player.getBoundingBox().inflate(config.range, config.range, config.range);
        assert Minecraft.getInstance().level != null;
        List<Entity> entities = Minecraft.getInstance().level.getEntities(player, scanBox);

        for (Entity entity : entities) {
            for (POIGroup<Entity> group : groups) {
                if (group.addIfQualified(entity)) {
                    currentScanResults.add(entity);
                    break;
                }
            }
        }

        lastScanResults = currentScanResults;
    }

    private void playerSoundAtFoundPOI(boolean isMarking) {
        if (config.volume == 0.0f) return;
        Function<Entity, Vec3> mapper = e -> e.blockPosition().getCenter();
        if (isMarking && Config.getInstance().poi.marking.suppressOtherWhenEnabled) {
            markedGroup.playSoundForGroupItems(mapper, config.volume);
        } else if (config.playSound) {
            for (POIGroup<Entity> group : groups) {
                group.playSoundForGroupItems(mapper, config.volume);
            }
        }
    }

    /**
     * Loads the configs from config.json
     */
    private void loadConfig() {
        config = Config.getInstance().poi.entities;
        interval.setDelay(config.delay, Interval.Unit.MILLISECOND);
    }

    private void setMarked(@Nullable Entity entity) {
        marked = Optional.ofNullable(entity).map(Entity::getClass).orElse(null);
    }
}
