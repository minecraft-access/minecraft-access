package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @implNote uses DFS algorithm
 */
public class BlockScanner {
    private final Set<BlockPos> checked = new HashSet<>();
    private final Consumer<BlockPos> blockConsumer;

    public BlockScanner(Consumer<BlockPos> blockConsumer) {
        this.blockConsumer = blockConsumer;
    }

    /**
     * Scan blocks around given center position and apply consumer to them.
     * Note that the "scan wave" won't penetrate through non-air blocks or fluid blocks if the player isn't in a liquid
     *
     * @param blockPos center position
     * @param range    range
     */
    public void scanAndQualifyBlocksExposedInAirAround(BlockPos blockPos, int range) {
        if (checked.contains(blockPos)) return;
        checked.add(blockPos);

        int nextStepRange = range - 1;
        assert Minecraft.getInstance().level != null;
        BlockState currentBlockState = Minecraft.getInstance().level.getBlockState(blockPos);
        if ((currentBlockState.isAir()
                || !currentBlockState.getFluidState().isEmpty() && Minecraft.getInstance().player.isInLiquid())
                && nextStepRange >= 0) {
            scanAndQualifyBlocksExposedInAirAround(blockPos.north(), nextStepRange);
            scanAndQualifyBlocksExposedInAirAround(blockPos.south(), nextStepRange);
            scanAndQualifyBlocksExposedInAirAround(blockPos.west(), nextStepRange);
            scanAndQualifyBlocksExposedInAirAround(blockPos.east(), nextStepRange);
            scanAndQualifyBlocksExposedInAirAround(blockPos.above(), nextStepRange);
            scanAndQualifyBlocksExposedInAirAround(blockPos.below(), nextStepRange);
            return;
        }

        blockConsumer.accept(blockPos);
    }
}
