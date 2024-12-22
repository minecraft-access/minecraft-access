package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.FluidDetectorConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.NarrationUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

/**
 * Searches for the closest water/lava source.
 */
@Slf4j
public class FluidDetector {
    private int range;
    private float volume;

    /**
     * Finds the closest water source and plays a sound at its position.
     *
     * @param closeCurrentlyOpenedScreen Whether to close the currently opened screen or not
     */
    public void findClosestWaterSource(boolean closeCurrentlyOpenedScreen) {
        if (closeCurrentlyOpenedScreen && MinecraftClient.getInstance().currentScreen != null && MinecraftClient.getInstance().player != null)
            MinecraftClient.getInstance().player.closeScreen();

        log.debug("Finding closest water source");
        findClosestFluidSource(true);
    }

    /**
     * Finds the closest lava source and plays a sound at its position.
     *
     * @param closeCurrentlyOpenedScreen Whether to close the currently opened screen or not
     */
    public void findClosestLavaSource(boolean closeCurrentlyOpenedScreen) {
        if (closeCurrentlyOpenedScreen && MinecraftClient.getInstance().currentScreen != null && MinecraftClient.getInstance().player != null)
            MinecraftClient.getInstance().player.closeScreen();

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
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient.world == null) return;
        if (minecraftClient.player == null) return;

        BlockPos pos = minecraftClient.player.getBlockPos();
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();

        loadConfigurations();

        BlockPos startingPointPos = new BlockPos(new Vec3i(posX, posY, posZ));
        BlockPos closestFluidPos = findFluid(minecraftClient, startingPointPos, this.range, water);
        if (closestFluidPos == null) {
            log.debug("Unable to find closest fluid source");
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.other.not_found"), true);
            return;
        }

        log.debug("{FluidDetector} playing sound at %dx %dy %dz".formatted(closestFluidPos.getX(), closestFluidPos.getY(), closestFluidPos.getZ()));
        minecraftClient.world.playSound(minecraftClient.player, closestFluidPos, SoundEvents.ENTITY_ITEM_PICKUP,
                SoundCategory.BLOCKS, this.volume, 1f);

        String posDifference = NarrationUtils.narrateRelativePositionOfPlayerAnd(closestFluidPos);
        String name = minecraftClient.world.getBlockState(closestFluidPos).getBlock().getName().getString();

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
    private static BlockPos findFluid(MinecraftClient minecraftClient, BlockPos blockPos, int range, boolean water) {
        if (minecraftClient.world == null) return null;
        if (minecraftClient.player == null) return null;

        BlockState blockState = minecraftClient.world.getBlockState(blockPos);
        if (blockState.isOf(Blocks.VOID_AIR)) // Skip if void air is found, the world is probably still loading.
            return null;

        FluidState fluidState = minecraftClient.world.getFluidState(blockPos);
        boolean rightTarget = (fluidState.isIn(FluidTags.LAVA) && !water) || (fluidState.isIn(FluidTags.WATER) && water);

        if (rightTarget && fluidState.isStill()) {
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

    private void loadConfigurations() {
        FluidDetectorConfigMap map = FluidDetectorConfigMap.getInstance();
        this.range = map.getRange();
        this.volume = map.getVolume();
    }
}
