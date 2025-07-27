package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.core.BlockPos;

import org.mcaccess.minecraftaccess.utils.WorldUtils;

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
     * Note that the "scan wave" won't "penetrate" through non-air blocks,
     * image blow up a balloon in a bottle, the balloon will fit the shape of the bottle.
     *
     * @param blockPos center position
     * @param range    range
     */
    public void scanAndQualifyBlocksExposedInAirAround(BlockPos blockPos, int range) {
        if (checked.contains(blockPos)) return;
        checked.add(blockPos);

        int nextStepRange = range - 1;
        if (WorldUtils.getBlockState(blockPos).isAir() && nextStepRange >= 0) {
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
