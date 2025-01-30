package org.mcaccess.minecraftaccess.features.point_of_interest;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Scans the area for entities, groups them and plays a sound at their location.
 */
@Slf4j
public class POIEntities {
    private Config.POI.Entities config;
    private final Interval interval = Interval.defaultDelay();

    private static final POIEntities INSTANCE = new POIEntities();
    private @Nullable Class<? extends Entity> marked = null;

    public final POIGroup<Entity> hostileGroup = new POIGroup<>(
            SoundEvents.NOTE_BLOCK_BELL.value(),
            2f,
            entity -> switch (entity) {
                case Monster ignored -> true;
                case NeutralMob mob -> {
                    if (mob.isAngry()) {
                        // Logic borrowed from NeutralMob.isAngryAt()
                        UUID playerId = WorldUtils.getClientPlayer().getUUID();
                        if (playerId.equals(mob.getPersistentAngerTarget())) yield true;
                        if (mob.getLastHurtByMob() != null && playerId.equals(mob.getLastHurtByMob().getUUID()))
                            yield true;
                    }
                    yield false;
                }
                default -> false;
            }
    );

    @SuppressWarnings("unchecked")
    final POIGroup<Entity>[] groups = new POIGroup[]{
            new POIGroup<Entity>(// Your Pets
                    SoundEvents.NOTE_BLOCK_FLUTE.value(),
                    1f,
                    entity -> entity instanceof TamableAnimal pet && pet.isOwnedBy(WorldUtils.getClientPlayer())
            ),
            new POIGroup<Entity>(// Other Pets
                    SoundEvents.NOTE_BLOCK_COW_BELL.value(),
                    1f,
                    entity -> entity instanceof TamableAnimal pet && pet.isTame()
            ),
            new POIGroup<Entity>(// Bosses
                    SoundEvents.NOTE_BLOCK_PLING.value(),
                    2f,
                    entity -> entity instanceof Mob mob && mob.getMaxHealth() >= 80 && !(entity instanceof IronGolem)
            ),
            hostileGroup,
            new POIGroup<Entity>(// Passive Mobs
                    SoundEvents.NOTE_BLOCK_BELL.value(),
                    0f,
                    entity -> entity instanceof AgeableMob || entity instanceof WaterAnimal
            ),
            new POIGroup<Entity>(// Players
                    SoundEvents.NOTE_BLOCK_CHIME.value(),
                    1f,
                    entity -> entity instanceof Player
            ),
            new POIGroup<Entity>(// Vehicles
                    SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value(),
                    1f,
                    entity -> entity instanceof VehicleEntity
            ),
            new POIGroup<Entity>(// Items
                    SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON,
                    2f,
                    entity -> entity instanceof ItemEntity itemEntity && itemEntity.onGround() || entity instanceof AbstractArrow projectile && projectile.pickup.equals(AbstractArrow.Pickup.ALLOWED)
            ),
    };

    public static POIEntities getInstance() {
        return INSTANCE;
    }

    private POIEntities() {
        loadConfig();
    }

    public void update(boolean isMarking, Entity markedEntity) {
        if (isMarking) setMarked(markedEntity);
        loadConfig();

        if (!config.enabled) return;
        if (!interval.isReady()) return;

        Minecraft client = Minecraft.getInstance();

        if (client.player == null) return;
        if (client.level == null) return;
        if (client.screen != null) return; //Prevent running if any screen is opened

        log.debug("POIEntities started.");

        for (POIGroup<Entity> group : groups) {
            group.clear();
        }

        AABB scanBox = client.player.getBoundingBox().inflate(config.range, config.range, config.range);
        List<Entity> entities = client.level.getEntities(client.player, scanBox);

        for (POIGroup<Entity> group : groups) {
            entities.removeIf(group::add);
        }

        for (POIGroup<Entity> group : groups) {
            for (Entity entity : group.getItems()) {
                if (isMarking && Config.getInstance().poi.marking.suppressOtherWhenEnabled && !(marked == null || marked.isInstance(entity))) {
                    continue;
                }
                playSoundAt(entity.blockPosition(), group);
            }
        }
    }

    private void playSoundAt(BlockPos pos, POIGroup<Entity> group) {
        if (!config.playSound || config.volume == 0f) return;
        log.debug("Play sound at [x:{} y:{} z{}]", pos.getX(), pos.getY(), pos.getZ());
        group.playSound(pos.getCenter(), config.volume);
    }

    /**
     * Loads the configs from config.json
     */
    private void loadConfig() {
        config = Config.getInstance().poi.entities;
        interval.setDelay(config.delay, Interval.Unit.Millisecond);
    }

    private void setMarked(@Nullable Entity entity) {
        marked = Optional.ofNullable(entity).map(Entity::getClass).orElse(null);
    }
}