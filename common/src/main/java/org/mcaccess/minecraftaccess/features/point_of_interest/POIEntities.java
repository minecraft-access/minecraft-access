package org.mcaccess.minecraftaccess.features.point_of_interest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.config.config_maps.POIEntitiesConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.POIMarkingConfigMap;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Scans the area for entities, groups them and plays a sound at their location.
 */
@Slf4j
public class POIEntities {
    private int range;
    private boolean playSound;
    private float volume;
    private final Interval interval = Interval.defaultDelay();
    private boolean enabled;

    private static final POIEntities INSTANCE = new POIEntities();
    private @Nullable Class<? extends Entity> marked = null;

    private final POIGroup<Entity> markedGroup = new POIGroup<>(
            "minecraft_access.point_of_interest.group.markedEntity",
            new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5f),
            e -> marked != null && marked.isInstance(e)
    );

    @SuppressWarnings("unchecked")
    final POIGroup<Entity>[] groups = Stream.of(List.of(markedGroup), BuiltinEntityPOIGroups.ALL)
            .flatMap(Collection::stream).toArray(POIGroup[]::new);

    public static POIEntities getInstance() {
        return INSTANCE;
    }

    @Getter
    private List<Entity> lastScanResults = new ArrayList<>();

    private POIEntities() {
        loadConfigurations();
    }

    public void update(boolean isMarking, Entity markedEntity) {
        if (isMarking) setMarked(markedEntity);
        loadConfigurations();

        if (!enabled) return;
        if (!interval.isReady()) return;

        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient.player == null) return;
        if (minecraftClient.level == null) return;
        if (minecraftClient.screen != null) return; //Prevent running if any screen is opened

        log.debug("POIEntities started.");
        scanEntitiesAroundPlayer();
        playerSoundAtFoundPOI(isMarking);
        log.debug("POIEntities ended.");
    }

    private void scanEntitiesAroundPlayer() {
        // initialize
        List<Entity> currentScanResults = new ArrayList<>();
        for (POIGroup<Entity> group : groups) {
            group.clear();
        }

        LocalPlayer player = WorldUtils.getClientPlayer();
        AABB scanBox = player.getBoundingBox().inflate(range, range, range);
        List<Entity> entities = WorldUtils.getClientWorld().getEntities(player, scanBox);

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
        if (volume == 0f) return;
        Function<Entity, Vec3> mapper = e -> e.blockPosition().getCenter();
        if (isMarking && POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled()) {
            markedGroup.playSoundForGroupItems(mapper, volume);
        } else if (playSound) {
            for (POIGroup<Entity> group : groups) {
                group.playSoundForGroupItems(mapper, volume);
            }
        }
    }

    /**
     * Loads the configs from config.json
     */
    private void loadConfigurations() {
        POIEntitiesConfigMap map = POIEntitiesConfigMap.getInstance();
        this.enabled = map.isEnabled();
        this.range = map.getRange();
        this.playSound = map.isPlaySound();
        this.volume = map.getVolume();
        this.interval.setDelay(map.getDelay(), Interval.Unit.Millisecond);
    }

    private void setMarked(@Nullable Entity entity) {
        marked = Optional.ofNullable(entity).map(Entity::getClass).orElse(null);
    }
}