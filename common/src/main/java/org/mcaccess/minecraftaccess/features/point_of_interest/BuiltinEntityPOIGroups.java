package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.NeutralMob;
import net.minecraft.world.entity.monster.Monster;
import org.mcaccess.minecraftaccess.utils.WorldUtils;

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
    ));

    public final POIGroup<Entity> group;

    BuiltinEntityPOIGroups(POIGroup<Entity> group) {
        this.group = group;
    }

}
