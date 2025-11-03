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

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

@Slf4j
public class FluidDetector implements AccessMenuFunction {
    private final TagKey<Fluid> fluid;
    private final Config.AccessMenu.FluidDetector config = Config.getInstance().accessMenu.fluidDetector;
    private final Minecraft client = Minecraft.getInstance();

    public FluidDetector(TagKey<Fluid> fluid) {
        this.fluid = fluid;
    }

    @Override
    public void execute() {
        if (client.level == null) return;
        if (client.player == null) return;

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayListDeque<>();
        queue.add(BlockPos.containing(client.player.position()));

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            assert pos != null;
            if (visited.contains(pos)) {
                continue;
            }
            visited.add(pos);
            if (pos.distToCenterSqr(client.player.position()) > config.range || !client.level.isLoaded(pos)) {
                continue;
            }
            BlockState blockState = client.level.getBlockState(pos);
            FluidState fluidState = client.level.getFluidState(pos);
            if (fluidState.is(fluid) && fluidState.isSource()) {
                log.debug("playing sound at {}", pos);
                client.level.playSound(client.player, pos, SoundEvents.ITEM_PICKUP,
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
