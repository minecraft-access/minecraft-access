package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

/**
 * We need to save the accurate (decimal) position of blocks
 * so that we can use it for the locking position.
 * See also: {@link NonCubeBlockAbsolutePositions}
 */
public class BlockPos3d extends BlockPos {
    private final Vec3 accuratePosition;

    public BlockPos3d(Vec3 position) {
        super((int) position.x, (int) position.y, (int) position.z);
        accuratePosition = position;
    }

    public BlockPos3d(BlockPos position) {
        super(position.getX(), position.getY(), position.getZ());
        accuratePosition = position.getCenter();
    }

    public BlockPos3d(BlockPos position, Vec3 accuratePosition) {
        super(position);
        this.accuratePosition = accuratePosition;
    }

    public static BlockPos3d of(BlockPos position) {
        return new BlockPos3d(position);
    }

    public Vec3 getAccuratePosition() {
        return accuratePosition;
    }
}
