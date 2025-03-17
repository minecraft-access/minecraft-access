package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import java.util.Arrays;
import java.util.function.Function;
import java.util.function.Predicate;

public enum BuiltinBlockPOIGroups {
    ORE(new POIGroup<>(
        "minecraft_access.point_of_interest.group.ore",
        new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5f),
        pos -> Ore.ORE_BLOCK_PREDICATE.test(WorldUtils.getBlockState(pos).getBlock())
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

        public static final Predicate<Block> ORE_BLOCK_PREDICATE =
            BuiltinBlockPOIGroups.BLOCK_PREDICATE_BUILDER.apply(ORE_BLOCKS);
    }
}
