package org.mcaccess.minecraftaccess.features.point_of_interest;

import org.mcaccess.minecraftaccess.config.config_maps.POIEntitiesConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.POIMarkingConfigMap;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
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
    private final Interval interval = Interval.defaultDelay();
    private boolean enabled;

    private static final POIEntities instance;
    private boolean onPOIMarkingNow = false;
    private Predicate<Entity> markedEntity = e -> false;

    public Map<String, POIGroup> builtInGroups = new LinkedHashMap<>();

    {
        builtInGroups.put("boss", new POIGroup(I18n.translate("minecraft_access.point_of_interest.group.boss"), SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), 2f,
            entity -> entity instanceof MobEntity mob && mob.getMaxHealth() >= 80 && !(entity instanceof IronGolemEntity), null)
        );

        builtInGroups.put("hostile", new POIGroup(I18n.translate("minecraft_access.point_of_interest.group.hostile"), SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 2f,
            entity -> entity instanceof HostileEntity || entity instanceof Angerable monster && (monster.hasAngerTime() || MinecraftClient.getInstance().player.getUuid().equals(monster.getAngryAt()) || MinecraftClient.getInstance().player.getUuid().equals(monster.getAttacker())), null)
        );

            builtInGroups.put("passive", new POIGroup(I18n.translate("minecraft_access.point_of_interest.group.passive"), SoundEvents.BLOCK_NOTE_BLOCK_BELL.value(), 0f,
            entity -> entity instanceof PassiveEntity || entity instanceof WaterCreatureEntity, null)
            );

            builtInGroups.put("player", new POIGroup(I18n.translate("minecraft_access.point_of_interest.group.player"), SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), 1f,
            entity -> entity instanceof PlayerEntity, null)
            );

            builtInGroups.put("item", new POIGroup(I18n.translate("minecraft_access.point_of_interest.group.item"), SoundEvents.BLOCK_METAL_PRESSURE_PLATE_CLICK_ON, 2f,
            entity -> entity instanceof ItemEntity itemEntity && itemEntity.isOnGround() || entity instanceof PersistentProjectileEntity projectile && projectile.pickupType.equals(PersistentProjectileEntity.PickupPermission.ALLOWED), null)
            );

            builtInGroups.put("yourPet", new POIGroup(I18n.translate("minecraft_access.point_of_interest.group.your_pets"), SoundEvents.BLOCK_NOTE_BLOCK_FLUTE.value(), 1f,
            entity -> entity instanceof TameableEntity pet && MinecraftClient.getInstance().player.getUuid().equals(pet.getOwnerUuid()), null)
        );

        builtInGroups.put("otherPet", new POIGroup(I18n.translate("minecraft_access.point_of_interest.group.other_pet"), SoundEvents.BLOCK_NOTE_BLOCK_COW_BELL.value(), 1f,
            entity -> entity instanceof TameableEntity pet && pet.isTamed(), null)
        );

        builtInGroups.put("vehicle", new POIGroup(I18n.translate("minecraft_access.point_of_interest.group.vehicle"), SoundEvents.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE.value(), 1f,
        entity -> entity instanceof VehicleEntity, null)
    );
    }

    static {
        instance = new POIEntities();
    }

    public static POIEntities getInstance() {
        return instance;
    }

    @Getter
    private List<Entity> lastScanResults = new ArrayList<>();

    private POIEntities() {
        loadConfigurations();
    }

    public void update(boolean onMarking, Entity markedEntity) {
        this.onPOIMarkingNow = onMarking;
        if (onPOIMarkingNow) setMarkedEntity(markedEntity);
        loadConfigurations();

        if (!enabled) return;
        if (!interval.isReady()) return;

        try {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();

            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.world == null) return;
            if (minecraftClient.currentScreen != null) return; //Prevent running if any screen is opened

            for (POIGroup group : builtInGroups.values()) {
                group.clearEntities();
            }

            List<Entity> currentScanResults = new ArrayList<>();

            log.debug("POIEntities started.");

            // Copied from PlayerEntity.tickMovement()
            Box scanBox = minecraftClient.player.getBoundingBox().expand(range, range, range);
            List<Entity> entities = minecraftClient.world.getOtherEntities(minecraftClient.player, scanBox);

            if (onPOIMarkingNow && POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled()) {
                POIGroup passiveGroup = builtInGroups.get("passive");
                POIGroup hostileGroup = builtInGroups.get("hostile");

                for (Entity e : entities) {
                    if (this.markedEntity.test(e)) {
                        if (passiveGroup.checkAndAddEntity(markedEntity))
                            this.playSoundAt(e.getBlockPos(), passiveGroup.sound, passiveGroup.soundPitch);
                        if (hostileGroup.checkAndAddEntity(markedEntity))
                            this.playSoundAt(e.getBlockPos(), hostileGroup.sound, hostileGroup.soundPitch);
                    }
                }

                return;
            }

            for (POIGroup group : builtInGroups.values()) {
                for (Entity e : entities) {
                    if (group.checkAndAddEntity(e)) {
                        this.playSoundAt(e.getBlockPos(), group.sound, group.soundPitch);
                        currentScanResults.add(e);

                        // Todo: Figure out why is this line causing only the nearest entity to get added into the group
                        // entities.remove(e);
                    }
                }

                lastScanResults = currentScanResults;
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
        this.interval.setDelay(map.getDelay(), Interval.Unit.Millisecond);
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