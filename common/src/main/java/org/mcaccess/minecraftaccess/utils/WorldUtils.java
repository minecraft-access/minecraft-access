package com.github.khanshoaib3.minecraft_access.utils;

import com.github.khanshoaib3.minecraft_access.utils.position.PlayerPositionUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.function.Predicate;

public class WorldUtils {

    public static BlockPos blockPosOf(Vec3d accuratePos) {
        return BlockPos.ofFloored(accuratePos);
    }
    public record BlockInfo(BlockPos pos, BlockState state, Block type, BlockEntity entity) {
    }

    public static BlockInfo getBlockInfo(BlockPos pos) {
        ClientWorld world = getClientWorld();

        // Since Minecraft uses flyweight pattern for blocks and entities,
        // All same type of blocks share one singleton Block instance,
        // While every block keep their states with a BlockState instance.
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        BlockEntity entity = world.getBlockEntity(pos);

        return new BlockInfo(pos, state, block, entity);
    }

    public static ClientWorld getClientWorld() {
        return MinecraftClient.getInstance().world;
    }

    public static ClientPlayerEntity getClientPlayer() {
        return MinecraftClient.getInstance().player;
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
    public static void playRelativePositionSoundCue(Vec3d targetPosition, double maxDistance, RegistryEntry.Reference<SoundEvent> sound, double minVolume, double maxVolume) {
        Vec3d playerPos = PlayerPositionUtils.getPlayerPosition().orElseThrow();

        // Use pitch to represent relative elevation, the higher the sound the higher the target.
        // The range of pitch is [0.5, 2.0], calculated as: 2 ^ (x / 12), where x is [-12, 12].
        // ref: https://minecraft.wiki/w/Note_Block#Notes
        //
        // Since we have a custom distance,
        // the range of (targetY - playerY) is [-maxDistance, maxDistance],
        // so let the maxDistance be the denominator to map to the original range.
        float pitch = (float) Math.pow(2, (targetPosition.getY() - playerPos.y) / maxDistance);

        // Use volume to represent distance, the louder the sound the closer the distance.
        double distance = Math.sqrt(targetPosition.squaredDistanceTo(playerPos.x, playerPos.y, playerPos.z));
        // = base volume (minVolume) + the volume delta per block ((maxVolume - minVolume) / maxDistance)
        double volumeDeltaPerBlock = (maxVolume - minVolume) / maxDistance;
        float volume = (float) (minVolume + (maxDistance - distance) * volumeDeltaPerBlock);

        playSoundAtPosition(sound, volume, pitch, targetPosition);
    }

    public static void playSoundAtPosition(RegistryEntry.Reference<SoundEvent> sound, float volume, float pitch, Vec3d position) {
        playSoundAtPosition(sound.value(), volume, pitch, position);
    }

    public static void playSoundAtPosition(SoundEvent sound, float volume, float pitch, Vec3d position) {
        // note that the useDistance param only works for positions 100 blocks away, check its code.
        getClientWorld().playSound(position.x, position.y, position.z, sound, SoundCategory.BLOCKS, volume, pitch, true);
    }
}
