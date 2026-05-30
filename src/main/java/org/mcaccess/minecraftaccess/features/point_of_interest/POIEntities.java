package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.events.ClientPlayingTick;

/**
 * Scans the area for entities, groups them and plays a sound at their location.
 */
@Slf4j
public class POIEntities implements BalmClientModule {
    private static final Config.POI.Entities CONFIG = Config.getInstance().poi.entities;
    private final Interval interval = Interval.defaultDelay();

    private @Nullable Entity markedEntity = null;

    private final POIGroup<Entity> markedGroup = new POIGroup<>(
            "minecraft_access.point_of_interest.group.markedEntity",
            new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5.0f),
            e -> markedEntity != null && markedEntity.getClass().isInstance(e)
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
        interval.setDelay(CONFIG.delay, Interval.Unit.MILLISECOND);
    }

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "poi/entities");
    }

    @Override
    public void initialize() {
        ClientPlayingTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            markedEntity = null;
            lastScanResults = new ArrayList<>();
        });
    }

    private void tick(Minecraft client, Player player, Level level) {
        Object currentMarkedObject = MainClass.poiManager.poiMarking.getMarkedObject().value;
        if (currentMarkedObject instanceof Entity entity) {
            markedEntity = entity;
        } else {
            markedEntity = null;
        }

        interval.setDelay(CONFIG.delay, Interval.Unit.MILLISECOND);

        if (!CONFIG.enabled) return;
        if (!interval.isReady()) return;

        if (client.screen != null) return; //Prevent running if any screen is opened

        log.trace("POIEntities started");
        scanEntitiesAroundPlayer();
        playerSoundAtFoundPOI(MainClass.poiManager.poiMarking.getMarkedObject().value != null);
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
        AABB scanBox = player.getBoundingBox().inflate(CONFIG.range, CONFIG.range, CONFIG.range);
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
        if (CONFIG.volume == 0.0f) return;
        Function<Entity, Vec3> mapper = e -> e.blockPosition().getCenter();
        if (isMarking && Config.getInstance().poi.marking.suppressOtherWhenEnabled) {
            markedGroup.playSoundForGroupItems(mapper, CONFIG.volume);
        } else if (CONFIG.playSound) {
            for (POIGroup<Entity> group : groups) {
                group.playSoundForGroupItems(mapper, CONFIG.volume);
            }
        }
    }
}
