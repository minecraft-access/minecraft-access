package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

/**
 * Searches for the closest water/lava source.
 */
@Slf4j
public class FluidDetector {
    private Config.AccessMenu.FluidDetector config;

    /**
     * Finds the closest water source and plays a sound at its position.
     *
     * @param closeCurrentlyOpenedScreen Whether to close the currently opened screen or not
     */
    public void findClosestWaterSource(boolean closeCurrentlyOpenedScreen) {
        if (closeCurrentlyOpenedScreen && Minecraft.getInstance().screen != null && Minecraft.getInstance().player != null)
            Minecraft.getInstance().player.clientSideCloseContainer();

        log.debug("Finding closest water source");
        findClosestFluidSource(true);
    }

    /**
     * Finds the closest lava source and plays a sound at its position.
     *
     * @param closeCurrentlyOpenedScreen Whether to close the currently opened screen or not
     */
    public void findClosestLavaSource(boolean closeCurrentlyOpenedScreen) {
        if (closeCurrentlyOpenedScreen && Minecraft.getInstance().screen != null && Minecraft.getInstance().player != null)
            Minecraft.getInstance().player.clientSideCloseContainer();

        log.debug("Finding closest lava source");
        findClosestFluidSource(false);
    }

    /**
     * Finds the closest fluid(water/lava) source and plays a sound at its position and
     * speaks its name with relative position.
     *
     * @param water Whether to find water or lava source block or not.
     */
    private void findClosestFluidSource(boolean water) {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient.level == null) return;
        if (minecraftClient.player == null) return;

        config = Config.getInstance().accessMenu.fluidDetector;

        BlockPos pos = minecraftClient.player.blockPosition();
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();

        BlockPos startingPointPos = new BlockPos(new Vec3i(posX, posY, posZ));
        BlockPos closestFluidPos = findFluid(minecraftClient, startingPointPos, config.range, water);
        if (closestFluidPos == null) {
            log.debug("Unable to find closest fluid source");
            MainClass.speakWithNarrator(I18n.get("minecraft_access.other.not_found"), true);
            return;
        }

        log.debug("{FluidDetector} playing sound at %dx %dy %dz".formatted(closestFluidPos.getX(), closestFluidPos.getY(), closestFluidPos.getZ()));
        minecraftClient.level.playSound(minecraftClient.player, closestFluidPos, SoundEvents.ITEM_PICKUP,
                SoundSource.BLOCKS, config.volume, 1f);

        String posDifference = NarrationUtils.narrateRelativePositionOfPlayerAnd(closestFluidPos);
        String name = minecraftClient.level.getBlockState(closestFluidPos).getBlock().getName().getString();

        MainClass.speakWithNarrator(name + ", " + posDifference, true);
    }

    /**
     * Checks if the block at the given position is fluid or not. If not found and within the range,
     * checks for the neighbouring blocks for the fluid recursively.
     *
     * @param minecraftClient The instance of MinecraftClient.
     * @param blockPos        The position of the block to check.
     * @param range           The range of the search area.
     * @param water           Whether to check for water source or lava source.
     * @return Returns the position of the fluid source or null if not found
     */
    private static BlockPos findFluid(Minecraft minecraftClient, BlockPos blockPos, int range, boolean water) {
        if (minecraftClient.level == null) return null;
        if (minecraftClient.player == null) return null;

        BlockState blockState = minecraftClient.level.getBlockState(blockPos);
        if (blockState.is(Blocks.VOID_AIR)) // Skip if void air is found, the world is probably still loading.
            return null;

        FluidState fluidState = minecraftClient.level.getFluidState(blockPos);
        boolean rightTarget = (fluidState.is(FluidTags.LAVA) && !water) || (fluidState.is(FluidTags.WATER) && water);

        if (rightTarget && fluidState.isSource()) {
            return blockPos;
        } else if (range - 1 >= 0 && blockState.isAir()) {
            int posX = blockPos.getX();
            int posY = blockPos.getY();
            int posZ = blockPos.getZ();
            int rangeVal = range - 1;

            BlockPos bp1 = findFluid(minecraftClient, new BlockPos(new Vec3i(posX, posY, posZ - 1)), rangeVal, water);
            BlockPos bp2 = findFluid(minecraftClient, new BlockPos(new Vec3i(posX, posY, posZ + 1)), rangeVal, water);
            BlockPos bp3 = findFluid(minecraftClient, new BlockPos(new Vec3i(posX - 1, posY, posZ)), rangeVal, water);
            BlockPos bp4 = findFluid(minecraftClient, new BlockPos(new Vec3i(posX + 1, posY, posZ)), rangeVal, water);
            BlockPos bp5 = findFluid(minecraftClient, new BlockPos(new Vec3i(posX, posY - 1, posZ)), rangeVal, water);
            BlockPos bp6 = findFluid(minecraftClient, new BlockPos(new Vec3i(posX, posY + 1, posZ)), rangeVal, water);

            if (bp1 != null) return bp1;
            if (bp2 != null) return bp2;
            if (bp3 != null) return bp3;
            if (bp4 != null) return bp4;
            if (bp5 != null) return bp5;
            return bp6;
        }

        return null;
    }
}
