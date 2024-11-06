package com.github.khanshoaib3.minecraft_access.features.point_of_interest;

import com.github.khanshoaib3.minecraft_access.config.config_maps.POIEntitiesConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.POIMarkingConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.WorldUtils;
import com.github.khanshoaib3.minecraft_access.utils.condition.Interval;
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
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Scans the area for entities, groups them and plays a sound at their location.
 */
@Slf4j
public class POIEntities {
    private int range;
    private boolean playSound;
    private float volume;
    private Interval interval;
    private boolean enabled;

    private static final POIEntities instance;
    private boolean onPOIMarkingNow = false;
    private Predicate<Entity> markedEntity = e -> false;

    public Map<String, POIGroup> builtInGroups = Map.of(
            "yourPet", new POIGroup("Your Pets", SoundEvents.BLOCK_NOTE_BLOCK_FLUTE.value(), 1f,
                    entity -> entity instanceof TameableEntity pet && MinecraftClient.getInstance().player.getUuid().equals(pet.getOwnerUuid()), null),
            "otherPet", new POIGroup("Other Pets", SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(), 1f,
                    entity -> entity instanceof TameableEntity pet && pet.isTamed(), null),
            "boss", new POIGroup("Bosses", SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2f,
                    entity -> entity instanceof MobEntity mob && mob.getMaxHealth() >= 80 && !(entity instanceof IronGolemEntity), null),
            "hostile", new POIGroup("Hostile Mobs", SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 2f,
                    entity -> entity instanceof HostileEntity || entity instanceof Angerable monster && (monster.hasAngerTime() || MinecraftClient.getInstance().player.getUuid().equals(monster.getAngryAt()) || MinecraftClient.getInstance().player.getUuid().equals(monster.getAttacker()) || monster.isUniversallyAngry(MinecraftClient.getInstance().player.getWorld())), null),
            "passive", new POIGroup("Passive Mobs", SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 0f,
                    entity -> entity instanceof PassiveEntity || entity instanceof WaterCreatureEntity, null),
            "player", new POIGroup("Players", SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), 1f,
                    entity -> entity instanceof PlayerEntity, null),
            "vehicle", new POIGroup("Vehicles", SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE.value(), 1f,
                    entity -> entity instanceof VehicleEntity, null),
            "item", new POIGroup("Items", SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, 2f,
                    entity -> entity instanceof ItemEntity itemEntity && itemEntity.isOnGround() || entity instanceof PersistentProjectileEntity projectile && projectile.pickupType.equals(PersistentProjectileEntity.PickupPermission.ALLOWED), null)
    );

    static {
        instance = new POIEntities();
    }

    public static POIEntities getInstance() {
        return instance;
    }

    private POIEntities() {
        loadConfigurations();
    }

    public void update(boolean onMarking, Entity markedEntity) {
        this.onPOIMarkingNow = onMarking;
        if (onPOIMarkingNow) setMarkedEntity(markedEntity);
        loadConfigurations();

        if (!enabled) return;
        if (interval != null && !interval.isReady()) return;

        try {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();

            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.world == null) return;
            if (minecraftClient.currentScreen != null) return; //Prevent running if any screen is opened

            log.debug("POIEntities started.");

            // Copied from PlayerEntity.tickMovement()
            Box scanBox = minecraftClient.player.getBoundingBox().expand(range, range, range);
            List<Entity> entities = minecraftClient.world.getOtherEntities(minecraftClient.player, scanBox);

            if (onPOIMarkingNow && POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled()) {
                POIGroup passiveGroup = builtInGroups.get("passive");
                POIGroup hostileGroup = builtInGroups.get("hostile");

                for (Entity e : entities) {
                    if (this.markedEntity.test(e)) {
                        if (passiveGroup.isEntityInGroup(markedEntity))
                            this.playSoundAt(e.getBlockPos(), passiveGroup.getSound(), passiveGroup.getSoundPitch());
                        if (hostileGroup.isEntityInGroup(markedEntity))
                            this.playSoundAt(e.getBlockPos(), hostileGroup.getSound(), hostileGroup.getSoundPitch());
                    }
                }

                return;
            }

            for (POIGroup group : builtInGroups.values()) {
                for (Entity e : entities) {
                    if (group.isEntityInGroup(e)) {
                        this.playSoundAt(e.getBlockPos(), group.getSound(), group.getSoundPitch());
                        entities.remove(e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("An error occurred while executing POIEntities", e);
        }
    }

    private void playSoundAt(BlockPos pos, SoundEvent soundEvent, float pitch) {
        if (!playSound || volume == 0f) return;
        log.debug("Play sound at [x:%d y:%d z%d]".formatted(pos.getX(), pos.getY(), pos.getZ()));
        WorldUtils.playSoundAtPosition(soundEvent, volume, pitch, pos.toCenterPos());
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
        this.interval = Interval.inMilliseconds(map.getDelay(), this.interval);
    }

    private void setMarkedEntity(Entity entity) {
        if (entity == null) {
            this.markedEntity = e -> false;
        } else {
            // Mark an entity = mark the type of entity (class type)
            Class<? extends Entity> clazz = entity.getClass();
            this.markedEntity = clazz::isInstance;
        }
    }
}