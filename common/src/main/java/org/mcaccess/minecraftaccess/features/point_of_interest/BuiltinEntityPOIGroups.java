package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.vehicle.VehicleEntity;

public enum BuiltinEntityPOIGroups {
    HOSTILE(new POIGroup<>(
            "minecraft_access.point_of_interest.group.hostile",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BELL.value(), 2.0f),
            entity -> {
                if (entity instanceof Monster) return true;
                if (entity instanceof NeutralMob mob) {
                    LocalPlayer player = Minecraft.getInstance().player;
                    assert player != null;
                    boolean mobAttackedPlayer = mob.equals(player.getLastHurtByMob());
                    boolean mobAngryAtPlayer = player.getUUID().equals(mob.getPersistentAngerTarget());
                    return mobAttackedPlayer || mobAngryAtPlayer;
                }
                return false;
            }
    )),
    YOUR_PETS(new POIGroup<>(
            "minecraft_access.point_of_interest.group.your_pet",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_FLUTE.value(), 1.0f),
            entity -> entity instanceof TamableAnimal pet && pet.isOwnedBy(Minecraft.getInstance().player)
    )),
    OTHER_PETS(new POIGroup<>(
            "minecraft_access.point_of_interest.group.other_pet",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_COW_BELL.value(), 1.0f),
            entity -> entity instanceof TamableAnimal pet && pet.isTame()
    )),
    BOSS(new POIGroup<>(
            "minecraft_access.point_of_interest.group.boss",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_PLING.value(), 2.0f),
            entity -> entity instanceof EnderDragon || entity instanceof WitherBoss
    )),
    PASSIVE(new POIGroup<>(
            "minecraft_access.point_of_interest.group.passive",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BELL.value(), 0.0f),
            entity -> (entity instanceof AgeableMob || entity instanceof WaterAnimal || entity instanceof NeutralMob || entity instanceof Allay)
                    && !(entity.getPassengers().contains(Minecraft.getInstance().player))
    )),
    PLAYER(new POIGroup<>(// Players
            "minecraft_access.point_of_interest.group.player",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_CHIME.value(), 1.0f),
            Player.class::isInstance
    )),
    VEHICLE(new POIGroup<>(
            "minecraft_access.point_of_interest.group.vehicle",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_IRON_XYLOPHONE.value(), 1.0f),
            entity -> entity instanceof VehicleEntity vehicle && !(vehicle.getPassengers().contains(Minecraft.getInstance().player))
    )),
    ITEM(new POIGroup<>(
            "minecraft_access.point_of_interest.group.item",
            new POIGroup.Sound(SoundEvents.METAL_PRESSURE_PLATE_CLICK_ON, 2.0f),
            entity -> {
                boolean itemOnGround = entity instanceof ItemEntity itemEntity && itemEntity.onGround();
                boolean pickupAllowedProjectile = entity instanceof AbstractArrow projectile && projectile.pickup == AbstractArrow.Pickup.ALLOWED;
                return itemOnGround || pickupAllowedProjectile;
            }
    )),
    DISPLAY(new POIGroup<>(
            "minecraft_access.point_of_interest.group.display",
            new POIGroup.Sound(SoundEvents.UI_TOAST_IN, 1.0f),
            Display.class::isInstance
    ));

    public static final List<POIGroup<Entity>> ALL = Arrays.stream(values()).map(bg -> bg.group).toList();
    public final POIGroup<Entity> group;

    BuiltinEntityPOIGroups(POIGroup<Entity> group) {
        this.group = group;
    }
}
