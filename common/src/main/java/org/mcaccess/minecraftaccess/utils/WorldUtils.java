package org.mcaccess.minecraftaccess.utils;

import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;

public final class WorldUtils {
    private WorldUtils() {
    }

    /**
     * To indicate relative location between player and target.
     */
    public static void playRelativePositionSoundCue(Vec3 targetPosition, double maxDistance, Holder.Reference<SoundEvent> sound, double minVolume, double maxVolume) {
        Vec3 playerPos = PlayerPositionUtils.getPlayerPosition().orElseThrow();

        // Use pitch to represent relative elevation, the higher the sound the higher the target.
        // The range of pitch is [0.5, 2.0], calculated as: 2 ^ (x / 12), where x is [-12, 12].
        // ref: https://minecraft.wiki/w/Note_Block#Notes
        //
        // Since we have a custom distance,
        // the range of (targetY - playerY) is [-maxDistance, maxDistance],
        // so let the maxDistance be the denominator to map to the original range.
        float pitch = (float) Math.pow(2, (targetPosition.y() - playerPos.y) / maxDistance);

        // Use volume to represent distance, the louder the sound the closer the distance.
        double distance = Math.sqrt(targetPosition.distanceToSqr(playerPos.x, playerPos.y, playerPos.z));
        // = base volume (minVolume) + the volume delta per block ((maxVolume - minVolume) / maxDistance)
        double volumeDeltaPerBlock = (maxVolume - minVolume) / maxDistance;
        float volume = (float) (minVolume + (maxDistance - distance) * volumeDeltaPerBlock);

        Minecraft.getInstance().level.playLocalSound(targetPosition.x, targetPosition.y, targetPosition.z, sound.value(), SoundSource.BLOCKS, volume, pitch, true);
    }
}
