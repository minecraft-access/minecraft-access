package org.mcaccess.minecraftaccess.features.point_of_interest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.config.config_maps.POIEntitiesConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.POIMarkingConfigMap;
import org.mcaccess.minecraftaccess.utils.condition.Interval;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

    @SuppressWarnings("unchecked")
    final POIGroup<Entity>[] groups = Arrays.stream(BuiltinEntityPOIGroups.values()).map(bg -> bg.group).toArray(POIGroup[]::new);

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

        if (minecraftClient == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.level == null) return;
        if (minecraftClient.screen != null) return; //Prevent running if any screen is opened

        for (POIGroup<Entity> group : groups) {
            group.clear();
        }

        List<Entity> currentScanResults = new ArrayList<>();
        log.debug("POIEntities started.");

        AABB scanBox = minecraftClient.player.getBoundingBox().inflate(range, range, range);
        List<Entity> entities = minecraftClient.level.getEntities(minecraftClient.player, scanBox);

        for (POIGroup<Entity> group : groups) {
            entities.removeIf(group::addIfQualified);
        }

        for (POIGroup<Entity> group : groups) {
            for (Entity entity : group.getItems()) {
                if (isMarking && POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled() &&
                    !(marked == null || marked.isInstance(entity))) {
                    continue;
                }
                if (playSound && volume != 0f) {
                    group.playSoundAt(entity.blockPosition().getCenter(), volume);
                }
                currentScanResults.add(entity);
            }
        }

        lastScanResults = currentScanResults;
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