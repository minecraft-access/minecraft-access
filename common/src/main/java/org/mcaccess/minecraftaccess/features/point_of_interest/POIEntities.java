package org.mcaccess.minecraftaccess.features.point_of_interest;

import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.mob.Angerable;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.vehicle.VehicleEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Optional;

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
            SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(),
            2f,
            entity -> entity instanceof HostileEntity || entity instanceof Angerable monster && (monster.hasAngerTime() || MinecraftClient.getInstance().player.getUuid().equals(monster.getAngryAt()) || MinecraftClient.getInstance().player.getUuid().equals(monster.getAttacker()))
    );

    @SuppressWarnings("unchecked")
    final POIGroup<Entity>[] groups = new POIGroup[] {
            new POIGroup<Entity>(// Your Pets
                    SoundEvents.BLOCK_NOTE_BLOCK_FLUTE.value(),
                    1f,
                    entity -> entity instanceof TameableEntity pet && MinecraftClient.getInstance().player.getUuid().equals(pet.getOwnerUuid())
            ),
            new POIGroup<Entity>(// Other Pets
                    SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(),
                    1f,
                    entity -> entity instanceof TameableEntity pet && pet.isTamed()
            ),
            new POIGroup<Entity>(// Bosses
                    SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(),
                    2f,
                    entity -> entity instanceof MobEntity mob && mob.getMaxHealth() >= 80 && !(entity instanceof IronGolemEntity)
            ),
            hostileGroup,
            new POIGroup<Entity>(// Passive Mobs
                    SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(),
                    0f,
                    entity -> entity instanceof PassiveEntity || entity instanceof WaterCreatureEntity
            ),
            new POIGroup<Entity>(// Players
                    SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(),
                    1f,
                    entity -> entity instanceof PlayerEntity
            ),
            new POIGroup<Entity>(// Vehicles
                    SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE.value(),
                    1f,
                    entity -> entity instanceof VehicleEntity
            ),
            new POIGroup<Entity>(// Items
                    SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON,
                    2f,
                    entity -> entity instanceof ItemEntity itemEntity && itemEntity.isOnGround() || entity instanceof PersistentProjectileEntity projectile && projectile.pickupType.equals(PersistentProjectileEntity.PickupPermission.ALLOWED)
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

        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.world == null) return;
        if (minecraftClient.currentScreen != null) return; //Prevent running if any screen is opened

        log.debug("POIEntities started.");

        for (POIGroup<Entity> group : groups) {
            group.clear();
        }

        Box scanBox = minecraftClient.player.getBoundingBox().expand(config.range, config.range, config.range);
        List<Entity> entities = minecraftClient.world.getOtherEntities(minecraftClient.player, scanBox);

        for (POIGroup<Entity> group : groups) {
            entities.removeIf(group::add);
        }

        for (POIGroup<Entity> group : groups) {
            for (Entity entity : group.getItems()) {
                if (isMarking && Config.getInstance().poi.marking.suppressOtherWhenEnabled && !(marked == null || marked.isInstance(entity))) {
                    continue;
                }
                playSoundAt(entity.getBlockPos(), group);
            }
        }
    }

    private void playSoundAt(BlockPos pos, POIGroup<Entity> group) {
        if (!config.playSound || config.volume == 0f) return;
        log.debug("Play sound at [x:{} y:{} z{}]", pos.getX(), pos.getY(), pos.getZ());
        group.playSound(pos.toCenterPos(), config.volume);
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