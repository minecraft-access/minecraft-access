package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import org.mcaccess.minecraftaccess.Config;

public enum BuiltinBlockPOIGroups {
    ORE(new POIGroup<>(
            "minecraft_access.point_of_interest.group.ore",
            new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5.0f),
            pos -> getBlockState(pos).is(BlockTags.ORES)
    )),
    FUNCTIONAL(new POIGroup<>(
            "minecraft_access.point_of_interest.group.functional",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2.0f),
            pos -> {
                BlockState state = getBlockState(pos);
                Block block = state.getBlock();
                return state.is(BlockTags.BUTTONS)
                        || state.is(BlockTags.BEEHIVES)
                        || state.is(BlockTags.CAMPFIRES)
                        || block instanceof LeverBlock
                        || Functional.FUNCTION_BLOCKS.contains(block);
            }
    )),
    DOOR(new POIGroup<>(
            "minecraft_access.point_of_interest.group.door",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2.0f),
            pos -> {
                BlockState doorState = getBlockState(pos);
                if (doorState.is(BlockTags.DOORS)) {
                    return doorState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER;
                } else {
                    return doorState.is(BlockTags.TRAPDOORS) || doorState.is(BlockTags.FENCE_GATES);
                }
            }
    )),
    PORTAL(new POIGroup<>(
            "minecraft_access.point_of_interest.group.portal",
            pos -> getBlockState(pos).is(BlockTags.PORTALS)
    )),
    LADDER(new POIGroup<>(
            "minecraft_access.point_of_interest.group.ladder",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2.0f),
            pos -> {
                if (!getBlockState(pos).is(BlockTags.CLIMBABLE)) {
                    return false;
                }

                return getBlockState(pos.above()).is(BlockTags.CLIMBABLE)
                        || getBlockState(pos.below()).is(BlockTags.CLIMBABLE);
            }
    )),
    FLUID(new POIGroup<>(
            "minecraft_access.point_of_interest.group.fluid",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2.0f),
            pos -> {
                assert Minecraft.getInstance().level != null;
                assert Minecraft.getInstance().player != null;
                return Config.getInstance().poi.blocks.detectFluidBlocks
                        && Minecraft.getInstance().level.getFluidState(pos).isFull()
                        && !isPlayerInOrOnFluid();
            }
    )),
    HAVE_INTERFACE(new POIGroup<>(
            "minecraft_access.point_of_interest.group.gui",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BANJO.value(), 0.0f),
            pos -> {
                BlockState state = getBlockState(pos);
                if (state.getBlock() instanceof ChestBlock) {
                    ChestType chestType = state.getValue(ChestBlock.TYPE);
                    return chestType == ChestType.SINGLE || chestType == ChestType.RIGHT;
                } else {
                    assert Minecraft.getInstance().level != null;
                    return state.getMenuProvider(Minecraft.getInstance().level, pos) != null;
                }
            }
    ));

    public static final List<POIGroup<BlockPos>> ALL = Arrays.stream(values()).map(bg -> bg.group).toList();

    public final POIGroup<BlockPos> group;

    BuiltinBlockPOIGroups(POIGroup<BlockPos> group) {
        this.group = group;
    }

    private static BlockState getBlockState(BlockPos pos) {
        assert Minecraft.getInstance().level != null;
        return Minecraft.getInstance().level.getBlockState(pos);
    }

    private static boolean isPlayerInOrOnFluid() {
        assert Minecraft.getInstance().player != null;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player.isInLiquid()) {
            return true;
        }

        BlockPos pos = player.blockPosition();
        if (!player.level().getFluidState(pos).isEmpty()) {
            return true;
        }

        return !player.level().getFluidState(pos.below()).isEmpty();
    }

    private static final class Functional {
        public static final Set<Block> FUNCTION_BLOCKS = Set.of(
                Blocks.PISTON,
                Blocks.STICKY_PISTON,
                Blocks.RESPAWN_ANCHOR,
                Blocks.BELL,
                Blocks.OBSERVER,
                Blocks.DAYLIGHT_DETECTOR,
                Blocks.JUKEBOX,
                Blocks.LODESTONE,
                Blocks.COMPOSTER,
                Blocks.SCULK_SHRIEKER,
                Blocks.SCULK_CATALYST,
                Blocks.CALIBRATED_SCULK_SENSOR,
                Blocks.SCULK_SENSOR,
                Blocks.VAULT,
                Blocks.TRIAL_SPAWNER,
                Blocks.SPAWNER,
                Blocks.CREAKING_HEART
        );
    }
}
