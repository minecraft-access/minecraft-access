package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.function.Consumer;
import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @implNote uses BFS algorithm, so every position is first reached through its shortest path
 */
public class BlockScanner {
    /**
     * Remaining range of every checked position, a position is checked again only if it's reached with a larger remaining range.
     */
    private final Map<BlockPos, Integer> checked = new HashMap<>();
    private final Consumer<BlockPos> blockConsumer;
    private final Predicate<BlockPos> isPassable;

    public BlockScanner(Consumer<BlockPos> blockConsumer) {
        this(blockConsumer, BlockScanner::isPassableInLevel);
    }

    /**
     * Creates a scanner with a custom check of whether a position can be passed through.
     *
     * @param blockConsumer consumer of the scanned blocks
     * @param isPassable    whether the "scan wave" can pass through the block at given position
     */
    BlockScanner(Consumer<BlockPos> blockConsumer, Predicate<BlockPos> isPassable) {
        this.blockConsumer = blockConsumer;
        this.isPassable = isPassable;
    }

    /**
     * Scan blocks around given center position and apply consumer to them.
     * Note that the "scan wave" won't penetrate through non-air blocks or fluid blocks if the player isn't in a liquid
     *
     * @param blockPos center position
     * @param range    range
     */
    public void scanAndQualifyBlocksExposedInAirAround(BlockPos blockPos, int range) {
        Queue<Step> queue = new ArrayDeque<>();
        queue.add(new Step(blockPos, range));

        while (!queue.isEmpty()) {
            Step step = queue.poll();
            Integer previousRange = checked.get(step.pos);
            if (previousRange != null && previousRange >= step.range) continue;
            checked.put(step.pos, step.range);

            int nextStepRange = step.range - 1;
            if (nextStepRange >= 0 && isPassable.test(step.pos)) {
                queue.add(new Step(step.pos.north(), nextStepRange));
                queue.add(new Step(step.pos.south(), nextStepRange));
                queue.add(new Step(step.pos.west(), nextStepRange));
                queue.add(new Step(step.pos.east(), nextStepRange));
                queue.add(new Step(step.pos.above(), nextStepRange));
                queue.add(new Step(step.pos.below(), nextStepRange));
                continue;
            }

            // Only pass each position to the consumer once, even if it's checked again with a larger range
            if (previousRange == null) blockConsumer.accept(step.pos);
        }
    }

    private static boolean isPassableInLevel(BlockPos blockPos) {
        assert Minecraft.getInstance().level != null;
        BlockState currentBlockState = Minecraft.getInstance().level.getBlockState(blockPos);
        return currentBlockState.isAir()
                || !currentBlockState.getFluidState().isEmpty() && Minecraft.getInstance().player.isInLiquid();
    }

    private record Step(BlockPos pos, int range) {
    }
}
