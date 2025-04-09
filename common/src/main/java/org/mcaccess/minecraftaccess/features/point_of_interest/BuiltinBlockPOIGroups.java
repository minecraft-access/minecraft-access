package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public enum BuiltinBlockPOIGroups {
    ORE(new POIGroup<>(
        "minecraft_access.point_of_interest.group.ore",
        new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5f),
        pos -> Ore.PREDICATE.test(WorldUtils.getBlockState(pos).getBlock()),
        PlayerUtils::distanceTo
    )),
    FUNCTIONAL(new POIGroup<>(
        "minecraft_access.point_of_interest.group.functional",
        new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2f),
        pos -> {
            Block block = WorldUtils.getBlockState(pos).getBlock();
            return block instanceof ButtonBlock || block instanceof LeverBlock || Functional.PREDICATE.test(block);
        },
        PlayerUtils::distanceTo
    )),
    DOOR(new POIGroup<>(
            "minecraft_access.point_of_interest.group.door",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2f),
            pos -> {
                WorldUtils.BlockInfo block = WorldUtils.getBlockInfo(pos);
                if (block.type() instanceof DoorBlock) {
                    // Only match upper part of doors
                    return block.state().getValue(DoorBlock.HALF).equals(DoubleBlockHalf.UPPER);
                } else {
                    return block.type() instanceof TrapDoorBlock;
                }
            },
            PlayerUtils::distanceTo
    )),
    PORTAL(new POIGroup<>(
        "minecraft_access.point_of_interest.group.portal",
        pos -> WorldUtils.getBlockInfo(pos).type() instanceof Portal || WorldUtils.getBlockInfo(pos).type() instanceof EndPortalFrameBlock,
        PlayerUtils::distanceTo
    )),
    LADDER(new POIGroup<>(
        "minecraft_access.point_of_interest.group.ladder",
        new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2f),
        pos -> WorldUtils.getBlockInfo(pos).type() instanceof LadderBlock,
        PlayerUtils::distanceTo
    )),
    FLUID(new POIGroup<>(
            "minecraft_access.point_of_interest.group.fluid",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2f),
            pos -> {
                Level world = WorldUtils.getClientWorld();
                boolean configEnabled = Config.getInstance().poi.blocks.detectFluidBlocks;
                boolean isSource = world.getFluidState(pos).getAmount() == 8;
                boolean isLiquid = world.getBlockState(pos).getBlock() instanceof LiquidBlock;
                return configEnabled && isLiquid && PlayerUtils.isNotInFluid() && isSource;
            },
            PlayerUtils::distanceTo
    )),
    HAVE_INTERFACE(new POIGroup<>(
            "minecraft_access.point_of_interest.group.gui",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BANJO.value(), 0f),
            pos -> {
                BlockState state = WorldUtils.getBlockState(pos);
                if (state.getBlock() instanceof ChestBlock) {
                    return Arrays.stream(ChestType.values()).anyMatch(t -> t.equals(state.getValue(ChestBlock.TYPE)));
                } else {
                    return state.getMenuProvider(WorldUtils.getClientWorld(), pos) != null;
                }
            },
            PlayerUtils::distanceTo
    ));

    public static final List<POIGroup<BlockPos>> ALL = Arrays.stream(values()).map(bg -> bg.group).toList();
    public static final Function<Block[], Predicate<Block>> BLOCK_PREDICATE_BUILDER =
        blocks -> b -> Arrays.stream(blocks).anyMatch(valid -> valid == b);

    public final POIGroup<BlockPos> group;

    BuiltinBlockPOIGroups(POIGroup<BlockPos> group) {
        this.group = group;
    }

    private static class Ore {
        protected static final Block[] ORE_BLOCKS = new Block[]{
            Blocks.DEEPSLATE_COAL_ORE,
            Blocks.COAL_ORE,
            Blocks.COPPER_ORE,
            Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.GOLD_ORE,
            Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.NETHER_GOLD_ORE,
            Blocks.IRON_ORE,
            Blocks.DEEPSLATE_IRON_ORE,
            Blocks.LAPIS_ORE,
            Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.NETHER_QUARTZ_ORE,
            Blocks.ANCIENT_DEBRIS
        };

        public static final Predicate<Block> PREDICATE =
            BuiltinBlockPOIGroups.BLOCK_PREDICATE_BUILDER.apply(ORE_BLOCKS);
    }

    private static class Functional {
        protected static final Block[] FUNCTION_BLOCKS = new Block[]{
            Blocks.PISTON,
            Blocks.STICKY_PISTON,
            Blocks.RESPAWN_ANCHOR,
            Blocks.BELL,
            Blocks.OBSERVER,
            Blocks.DAYLIGHT_DETECTOR,
            Blocks.JUKEBOX,
            Blocks.LODESTONE,
            Blocks.BEE_NEST,
            Blocks.COMPOSTER,
            Blocks.OBSERVER,
            Blocks.SCULK_SHRIEKER,
            Blocks.SCULK_CATALYST,
            Blocks.CALIBRATED_SCULK_SENSOR,
            Blocks.SCULK_SENSOR,
            Blocks.VAULT,
            Blocks.TRIAL_SPAWNER,
            Blocks.SPAWNER,
            Blocks.CREAKING_HEART,
        };

        public static final Predicate<Block> PREDICATE =
            BuiltinBlockPOIGroups.BLOCK_PREDICATE_BUILDER.apply(FUNCTION_BLOCKS);
    }
}
