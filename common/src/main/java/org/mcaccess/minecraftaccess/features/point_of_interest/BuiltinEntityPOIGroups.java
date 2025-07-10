package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.VehicleEntity;
import org.mcaccess.minecraftaccess.utils.WorldUtils;

import java.util.Arrays;
import java.util.List;

public enum BuiltinEntityPOIGroups {
    HOSTILE(new POIGroup<>(
            "minecraft_access.point_of_interest.group.hostile",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BELL.value(), 2f),
            entity -> {
                if (entity instanceof Monster) return true;
                if (entity instanceof NeutralMob mob) {
                    LocalPlayer player = WorldUtils.getClientPlayer();
                    boolean mobAttackedPlayer = mob.equals(player.getLastHurtByMob());
                    boolean mobAngryAtPlayer = player.getUUID().equals(mob.getPersistentAngerTarget());
                    return mobAttackedPlayer || mobAngryAtPlayer;
                }
                return false;
            }
    )),
    YOUR_PETS(new POIGroup<>(
            "minecraft_access.point_of_interest.group.your_pet",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_FLUTE.value(), 1f),
            entity -> entity instanceof TamableAnimal pet && pet.isOwnedBy(WorldUtils.getClientPlayer())
    )),
    OTHER_PETS(new POIGroup<>(
            "minecraft_access.point_of_interest.group.other_pet",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_COW_BELL.value(), 1f),
            entity -> entity instanceof TamableAnimal pet && pet.isTame()
    )),
    BOSS(new POIGroup<>(
            "minecraft_access.point_of_interest.group.boss",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_PLING.value(), 2f),
            entity -> entity instanceof EnderDragon || entity instanceof WitherBoss
    )),
    PASSIVE(new POIGroup<>(
            "minecraft_access.point_of_interest.group.passive",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BELL.value(), 0f),
            entity -> {
                return (entity instanceof AgeableMob || entity instanceof WaterAnimal || entity instanceof NeutralMob) && !(entity.getPassengers().contains(Minecraft.getInstance().player));
            }
    )),
    PLAYER(new POIGroup<>(// Players
            "minecraft_access.point_of_interest.group.player",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_CHIME.value(), 1f),
            Player.class::isInstance
    )),
    VEHICLE(new POIGroup<>(
            "minecraft_access.point_of_interest.group.vehicle",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value(), 1f),
            entity -> {
                return entity instanceof VehicleEntity vehicle && !(vehicle.getPassengers().contains(Minecraft.getInstance().player));
            }
    )),
    ITEM(new POIGroup<>(
            "minecraft_access.point_of_interest.group.item",
            new POIGroup.Sound(SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, 2f),
            entity -> {
                boolean itemOnGround = entity instanceof ItemEntity itemEntity && itemEntity.onGround();
                boolean pickupAllowedProjectile = entity instanceof AbstractArrow projectile && projectile.pickup.equals(AbstractArrow.Pickup.ALLOWED);
                return itemOnGround || pickupAllowedProjectile;
            }
    )),
    DISPLAY(new POIGroup<>(
            "minecraft_access.point_of_interest.group.display",
            new POIGroup.Sound(SoundEvents.UI_TOAST_IN, 1f),
            Display.class::isInstance
    ));

    public static final List<POIGroup<Entity>> ALL = Arrays.stream(values()).map(bg -> bg.group).toList();
    public final POIGroup<Entity> group;

    BuiltinEntityPOIGroups(POIGroup<Entity> group) {
        this.group = group;
    }
}
