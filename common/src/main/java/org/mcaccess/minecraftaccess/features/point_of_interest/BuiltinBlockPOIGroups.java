package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.LeverBlock;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public enum BuiltinBlockPOIGroups {
    ORE(new POIGroup<>(
        "minecraft_access.point_of_interest.group.ore",
        new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5f),
        pos -> Ore.PREDICATE.test(WorldUtils.getBlockState(pos).getBlock())
    )),
    FUNCTIONAL(new POIGroup<>(// Functional blocks
        "minecraft_access.point_of_interest.group.functional",
        new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2f),
        pos -> {
            Block block = WorldUtils.getBlockState(pos).getBlock();
            return block instanceof ButtonBlock
                || block instanceof LeverBlock
                || Functional.PREDICATE.test(block);
        }
    ));

    public static final Function<Block[], Predicate<Block>> BLOCK_PREDICATE_BUILDER =
        blocks -> b -> Arrays.stream(blocks).anyMatch(valid -> valid == b);

    public final POIGroup<BlockPos> group;

    BuiltinBlockPOIGroups(POIGroup<BlockPos> group) {
        this.group = group;
    }

    public static class Ore {
        public static final Block[] ORE_BLOCKS = new Block[] {
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

    public static class Functional {
        public static final Block[] FUNCTION_BLOCKS = new Block[] {
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
