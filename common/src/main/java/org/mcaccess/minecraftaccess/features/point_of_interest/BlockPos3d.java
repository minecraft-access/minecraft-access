package com.github.khanshoaib3.minecraft_access.features.point_of_interest;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

/**
 * We need to save the accurate (decimal) position of blocks
 * so that we can use it for the locking position.
 * See also: {@link NonCubeBlockAbsolutePositions}
 */
public class BlockPos3d extends BlockPos {
    private final Vec3d accuratePosition;

    public BlockPos3d(Vec3d position) {
        super((int) position.x, (int) position.y, (int) position.z);
        accuratePosition = position;
    }

    public BlockPos3d(BlockPos position) {
        super(position.getX(), position.getY(), position.getZ());
        accuratePosition = position.toCenterPos();
    }

    public static BlockPos3d of(BlockPos position) {
        return new BlockPos3d(position);
    }

    public Vec3d getAccuratePosition() {
        return accuratePosition;
    }
}
