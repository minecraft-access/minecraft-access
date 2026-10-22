package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

class BlockScannerTest {
    private static final BlockPos CENTER = new BlockPos(0, 64, 0);

    @ParameterizedTest
    @MethodSource("blocksInRange")
    void testFindsBlockInRangeSurroundedByAir(BlockPos target, int range) {
        List<BlockPos> found = scan(Set.of(target), range);
        assertThat(found).containsExactly(target);
    }

    private static Stream<Arguments> blocksInRange() {
        return Stream.of(
                Arguments.of(CENTER.above(3), 10),
                Arguments.of(CENTER.below(3), 10),
                Arguments.of(CENTER.above(5), 10),
                Arguments.of(CENTER.offset(3, 3, 0), 10),
                Arguments.of(CENTER.offset(-2, -4, 3), 10),
                Arguments.of(CENTER.offset(0, 6, 4), 10)
        );
    }

    @Test
    void testFindsBlocksAboveAndBelowInStairShape() {
        // https://github.com/minecraft-access/minecraft-access/issues/446
        Set<BlockPos> targets = Set.of(
                CENTER.offset(-1, 3, 0),
                CENTER.offset(0, 4, 0),
                CENTER.offset(1, 5, 0),
                CENTER.offset(2, 6, 0),
                CENTER.offset(-1, -3, 0),
                CENTER.offset(0, -4, 0),
                CENTER.offset(1, -5, 0)
        );
        List<BlockPos> found = scan(targets, 10);
        assertThat(found).containsExactlyInAnyOrderElementsOf(targets);
    }

    @Test
    void testDoesNotFindBlockOutOfRange() {
        List<BlockPos> found = scan(Set.of(CENTER.above(5)), 3);
        assertThat(found).isEmpty();
    }

    @Test
    void testDoesNotPassThroughSolidBlocks() {
        BlockPos wall = CENTER.east();
        Set<BlockPos> solid = new HashSet<>();
        // a closed 3x3x3 box around the center
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) solid.add(CENTER.offset(x, y, z));
                }
            }
        }
        BlockPos outside = CENTER.east(3);
        solid.add(outside);

        List<BlockPos> found = scan(solid, 10);
        assertThat(found).contains(wall).doesNotContain(outside);
    }

    @Test
    void testEachBlockIsConsumedOnce() {
        Set<BlockPos> targets = Set.of(CENTER.above(2), CENTER.below(), CENTER.offset(2, 1, -1));
        List<BlockPos> found = new ArrayList<>();
        BlockScanner scanner = new BlockScanner(pos -> {
            if (targets.contains(pos)) found.add(pos);
        }, pos -> !targets.contains(pos));
        scanner.scanAndQualifyBlocksExposedInAirAround(CENTER.below(), 0);
        scanner.scanAndQualifyBlocksExposedInAirAround(CENTER.above(2), 0);
        scanner.scanAndQualifyBlocksExposedInAirAround(CENTER, 6);
        scanner.scanAndQualifyBlocksExposedInAirAround(CENTER.above(), 6);
        assertThat(found).containsExactlyInAnyOrderElementsOf(targets);
    }

    private static List<BlockPos> scan(Set<BlockPos> solid, int range) {
        List<BlockPos> found = new ArrayList<>();
        BlockScanner scanner = new BlockScanner(pos -> {
            if (solid.contains(pos)) found.add(pos);
        }, pos -> !solid.contains(pos));
        scanner.scanAndQualifyBlocksExposedInAirAround(CENTER, range);
        return found;
    }
}
