package org.mcaccess.minecraftaccess.addon.accessmenu;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ArrayListDeque;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.ModConfig;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

@Slf4j
public class FluidDetector implements AccessMenuFunction {
    private final TagKey<Fluid> fluid;

    public FluidDetector(TagKey<Fluid> fluid) {
        this.fluid = fluid;
    }

    @Override
    public void execute() {
        ModConfig.AccessMenuFluidDetector config = ModConfig.getInstance().accessMenuFluidDetector;
        if (Minecraft.getInstance().level == null) return;
        if (Minecraft.getInstance().player == null) return;

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayListDeque<>();
        queue.add(BlockPos.containing(Minecraft.getInstance().player.position()));

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            assert pos != null;
            if (visited.contains(pos)) {
                continue;
            }
            visited.add(pos);
            if (pos.distToCenterSqr(Minecraft.getInstance().player.position()) > config.range || !Minecraft.getInstance().level.isLoaded(pos)) {
                continue;
            }
            BlockState blockState = Minecraft.getInstance().level.getBlockState(pos);
            FluidState fluidState = Minecraft.getInstance().level.getFluidState(pos);
            if (fluidState.is(fluid) && fluidState.isSource()) {
                log.debug("playing sound at {}", pos);
                Minecraft.getInstance().level.playSound(Minecraft.getInstance().player, pos, SoundEvents.ITEM_PICKUP,
                        SoundSource.BLOCKS, config.volume, 1.0f);

                String posDifference = NarrationUtils.narrateRelativePositionOfPlayerAnd(pos);
                String name = blockState.getBlock().getName().getString();

                MainClass.narrate(name + I18n.get("minecraft_access.other.words_connection") + posDifference, true);
                return;
            }
            if (!blockState.isAir() && !fluidState.is(fluid)) {
                continue;
            }
            queue.add(pos.above());
            queue.add(pos.below());
            queue.add(pos.north());
            queue.add(pos.south());
            queue.add(pos.east());
            queue.add(pos.west());
        }
        log.debug("Unable to find closest fluid source");
        MainClass.narrate(I18n.get("minecraft_access.other.not_found"), true);
    }
}
