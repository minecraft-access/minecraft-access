package org.mcaccess.minecraftaccess.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;

import java.util.function.Predicate;

public class WorldUtils {

    public static BlockPos blockPosOf(Vec3 accuratePos) {
        return BlockPos.containing(accuratePos);
    }
    public record BlockInfo(BlockPos pos, BlockState state, Block type, BlockEntity entity) {
    }

    public static BlockInfo getBlockInfo(BlockPos pos) {
        ClientLevel world = getClientWorld();

        // Since Minecraft uses flyweight pattern for blocks and entities,
        // All same type of blocks share one singleton Block instance,
        // While every block keep their states with a BlockState instance.
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        BlockEntity entity = world.getBlockEntity(pos);

        return new BlockInfo(pos, state, block, entity);
    }

    public static ClientLevel getClientWorld() {
        return Minecraft.getInstance().level;
    }

    public static LocalPlayer getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    public static boolean checkAnyOfBlocks(Iterable<BlockPos> positions, Predicate<BlockState> expected) {
        for (BlockPos pos : positions) {
            BlockInfo info = getBlockInfo(pos);
            if (info.state == null) return false;
            if (expected.test(info.state)) return true;
        }
        return false;
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

        playSoundAtPosition(sound, volume, pitch, targetPosition);
    }

    public static void playSoundAtPosition(Holder.Reference<SoundEvent> sound, float volume, float pitch, Vec3 position) {
        playSoundAtPosition(sound.value(), volume, pitch, position);
    }

    public static void playSoundAtPosition(SoundEvent sound, float volume, float pitch, Vec3 position) {
        // note that the useDistance param only works for positions 100 blocks away, check its code.
        getClientWorld().playLocalSound(position.x, position.y, position.z, sound, SoundSource.BLOCKS, volume, pitch, true);
    }
}
