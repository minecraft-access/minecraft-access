package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import org.mcaccess.minecraftaccess.ModConfig;

public enum BuiltinBlockPOIGroups {
    ORE(new POIGroup<>(
            "minecraft_access.point_of_interest.group.ore",
            new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5.0f),
            pos -> Ore.PREDICATE.test(getBlockState(pos).getBlock())
    )),
    FUNCTIONAL(new POIGroup<>(
            "minecraft_access.point_of_interest.group.functional",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2.0f),
            pos -> {
                Block block = getBlockState(pos).getBlock();
                return block instanceof ButtonBlock || block instanceof LeverBlock || Functional.PREDICATE.test(block);
            }
    )),
    DOOR(new POIGroup<>(
            "minecraft_access.point_of_interest.group.door",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2.0f),
            pos -> {
                assert Minecraft.getInstance().level != null;
                BlockState doorState = Minecraft.getInstance().level.getBlockState(pos);
                if (doorState.getBlock() instanceof DoorBlock) {
                    // Only match upper part of doors
                    return doorState.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER;
                } else {
                    return doorState.getBlock() instanceof TrapDoorBlock;
                }
            }
    )),
    PORTAL(new POIGroup<>(
            "minecraft_access.point_of_interest.group.portal",
            pos -> {
                assert Minecraft.getInstance().level != null;
                Block block = Minecraft.getInstance().level.getBlockState(pos).getBlock();
                return block instanceof Portal || block instanceof EndPortalFrameBlock;
            }
    )),
    LADDER(new POIGroup<>(
            "minecraft_access.point_of_interest.group.ladder",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2.0f),
            pos -> {
                assert Minecraft.getInstance().level != null;
                Block block = Minecraft.getInstance().level.getBlockState(pos).getBlock();
                return block instanceof LadderBlock;
            }
    )),
    FLUID(new POIGroup<>(
            "minecraft_access.point_of_interest.group.fluid",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2.0f),
            pos -> {
                Level level = Minecraft.getInstance().level;
                boolean configEnabled = ModConfig.getInstance().poiBlocks.detectFluidBlocks;
                assert level != null;
                boolean isSource = level.getFluidState(pos).getAmount() == 8;
                boolean isLiquid = level.getBlockState(pos).getBlock() instanceof LiquidBlock;
                assert Minecraft.getInstance().player != null;
                return configEnabled && isLiquid && !Minecraft.getInstance().player.isInLiquid() && isSource;
            }
    )),
    HAVE_INTERFACE(new POIGroup<>(
            "minecraft_access.point_of_interest.group.gui",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BANJO.value(), 0.0f),
            pos -> {
                BlockState state = getBlockState(pos);
                if (state.getBlock() instanceof ChestBlock) {
                    ChestType chestType = state.getValue(ChestBlock.TYPE);
                    // intentionally ignore LEFT so two-blocks-size big chest can be detected as single chest
                    return chestType == ChestType.SINGLE || chestType == ChestType.RIGHT;
                } else {
                    assert Minecraft.getInstance().level != null;
                    return state.getMenuProvider(Minecraft.getInstance().level, pos) != null;
                }
            }
    ));

    public static final List<POIGroup<BlockPos>> ALL = Arrays.stream(values()).map(bg -> bg.group).toList();
    public static final Function<Block[], Predicate<Block>> BLOCK_PREDICATE_BUILDER =
            blocks -> b -> Arrays.stream(blocks).anyMatch(valid -> valid == b);

    public final POIGroup<BlockPos> group;

    BuiltinBlockPOIGroups(POIGroup<BlockPos> group) {
        this.group = group;
    }

    private static BlockState getBlockState(BlockPos pos) {
        assert Minecraft.getInstance().level != null;
        return Minecraft.getInstance().level.getBlockState(pos);
    }

    private static final class Ore {
        private static final Block[] ORE_BLOCKS = new Block[]{
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
                Blocks.ANCIENT_DEBRIS,
        };

        public static final Predicate<Block> PREDICATE = BLOCK_PREDICATE_BUILDER.apply(ORE_BLOCKS);
    }

    private static final class Functional {
        private static final Block[] FUNCTION_BLOCKS = new Block[]{
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

        public static final Predicate<Block> PREDICATE = BLOCK_PREDICATE_BUILDER.apply(FUNCTION_BLOCKS);
    }
}
