package org.mcaccess.minecraftaccess.utils;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class PlayerUtils {
    // A way to get exactly at what part of the entity the player is looking when locked on it
    public static Vec3 currentEntityLookingAtPosition = null;
    private static final Minecraft CLIENT = Minecraft.getInstance();

    private PlayerUtils() {
    }

    /**
     * Let player looks at entity even the entity exposes a very small part of its body
     */
    public static void lookAt(Entity entity) {
        assert CLIENT.player != null;
        Vec3 playerEyePos = CLIENT.player.getEyePosition();

        // Try to look at entity's eyes or Enderman's stomach first.
        boolean targetIsEnderman = entity instanceof EnderMan;
        Vec3 firstPos = targetIsEnderman ? entity.blockPosition().getCenter() : entity.getEyePosition();
        if (isVisibleToPlayer(playerEyePos, firstPos, entity)) {
            CLIENT.player.lookAt(EntityAnchorArgument.Anchor.EYES, firstPos);
            currentEntityLookingAtPosition = firstPos;
            return;
        }

        // Then start to find a possible position to target at the entity.
        // This part of code is copied from Explosion.getExposure()
        AABB box = entity.getBoundingBox();
        double stepX = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
        double stepY = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
        double stepZ = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
        // Don't know how these two lengths are determined
        double initX = (1.0 - Math.floor(1.0 / stepX) * stepX) / 2.0;
        double initZ = (1.0 - Math.floor(1.0 / stepZ) * stepZ) / 2.0;
        if (stepX < 0.0 || stepY < 0.0 || stepZ < 0.0) {
            CLIENT.player.lookAt(EntityAnchorArgument.Anchor.EYES, firstPos);
            currentEntityLookingAtPosition = firstPos;
            return;
        }

        // Never look at Enderman's face
        double maxY = targetIsEnderman ? Mth.lerp(0.7, box.minY, box.maxY) / 2 : box.maxY;

        for (double i = 0.0; i <= 1.0; i += stepX) {
            for (double j = 0.0; j <= 1.0; j += stepY) {
                for (double k = 0.0; k <= 1.0; k += stepZ) {
                    double px = Mth.lerp(i, box.minX, box.maxX);
                    double py = Mth.lerp(j, box.minY, maxY);
                    double pz = Mth.lerp(k, box.minZ, box.maxZ);
                    Vec3 vec3d = new Vec3(px + initX, py, pz + initZ);
                    if (isVisibleToPlayer(playerEyePos, vec3d, entity)) {
                        CLIENT.player.lookAt(EntityAnchorArgument.Anchor.EYES, vec3d);
                        currentEntityLookingAtPosition = vec3d;
                        return;
                    }
                }
            }
        }

        // Make sure to look at entity even the player can't see it.
        CLIENT.player.lookAt(EntityAnchorArgument.Anchor.EYES, firstPos);
        currentEntityLookingAtPosition = firstPos;
    }

    public static boolean isVisibleToPlayer(Vec3 playerEyePos, Vec3 somewhereOnEntity, Entity entity) {
        BlockHitResult hitResult = entity.level().clip(
                new ClipContext(somewhereOnEntity, playerEyePos,
                        ClipContext.Block.COLLIDER,
                        ClipContext.Fluid.NONE, entity));
        return hitResult.getType() == HitResult.Type.MISS;
    }

    public static boolean isInFluid() {
        LocalPlayer player = CLIENT.player;
        assert player != null;
        return player.isSwimming()
                || player.isUnderWater()
                || player.isInWater()
                || player.isInLava();
    }

    /**
     * The value of MinecraftClient.crosshairTarget field is ray cast result that not including the fluid blocks.
     * So use this method to get what fluid the player might be looking at.
     *
     * @return fluid block if player isn't in fluid and is looking at a fluid block,
     *     or MinecraftClient.crosshairTarget otherwise
     */
    public static HitResult crosshairTarget(double rayCastDistance) {
        BlockHitResult fluidHitResult = crosshairFluidTarget(rayCastDistance);
        if (fluidHitResult.getType() == HitResult.Type.BLOCK && !isInFluid()) {
            return fluidHitResult;
        } else {
            return CLIENT.hitResult;
        }
    }

    private static BlockHitResult crosshairFluidTarget(double rayCastDistance) {
        Entity camera = Objects.requireNonNull(CLIENT.getCameraEntity());
        HitResult hit = camera.pick(rayCastDistance, 0.0F, true);
        // Whatever the inner values are, they are not used.
        BlockHitResult missed = BlockHitResult.miss(Vec3.ZERO, Direction.UP, BlockPos.ZERO);

        if (hit.getType() != HitResult.Type.BLOCK) return missed;

        BlockPos blockPos = ((BlockHitResult) hit).getBlockPos();
        ClientLevel world = CLIENT.level;

        assert world != null;
        BlockState blockState = world.getBlockState(blockPos);
        boolean thisBlockIsFluidBlock = blockState.is(Blocks.WATER) || blockState.is(Blocks.LAVA);
        if (!thisBlockIsFluidBlock) return missed;

        FluidState fluidState = world.getFluidState(blockPos);
        if (fluidState.isEmpty()) return missed;

        return (BlockHitResult) hit;
    }
}
