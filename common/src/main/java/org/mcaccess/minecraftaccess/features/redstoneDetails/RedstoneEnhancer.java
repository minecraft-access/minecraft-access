package org.mcaccess.minecraftaccess.features.redstoneDetails;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.DropperBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.ComparatorBlock;
import net.minecraft.world.level.block.TripWireHookBlock;
import net.minecraft.world.level.block.ObserverBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.mcaccess.minecraftaccess.MainClass;

public class RedstoneEnhancer {
    public static void describe(Level world, Block block, BlockState state, BlockPos pos) {

        BlockPos northPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1);
        BlockPos southPos = new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1);
        BlockPos westPos = new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ());
        BlockPos eastPos = new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ());
        BlockState northState = world.getBlockState(northPos);
        BlockState southState = world.getBlockState(southPos);
        BlockState westState = world.getBlockState(westPos);
        BlockState eastState = world.getBlockState(eastPos);
        Block north = northState.getBlock();
        Block west = westState.getBlock();
        Block east = eastState.getBlock();
        Block south = southState.getBlock();
        String finalText = "Posible blocks to connect: ";

        if (east instanceof DispenserBlock || east instanceof CrafterBlock || east instanceof DropperBlock
                || east instanceof LeverBlock || east instanceof ButtonBlock || east instanceof PressurePlateBlock
                || east instanceof RepeaterBlock || east instanceof ComparatorBlock || east instanceof TripWireHookBlock
                || east instanceof ObserverBlock) {
            finalText += "East, " + east.getName().getString() + ". ";
        }

        if (west instanceof DispenserBlock || west instanceof CrafterBlock || west instanceof DropperBlock
                || west instanceof LeverBlock || west instanceof ButtonBlock || west instanceof PressurePlateBlock
                || west instanceof RepeaterBlock || west instanceof ComparatorBlock || west instanceof TripWireHookBlock
                || west instanceof ObserverBlock) {
            finalText += "West, " + west.getName().getString() + ". ";
        }

        if (north instanceof DispenserBlock || north instanceof CrafterBlock || north instanceof DropperBlock
                || north instanceof LeverBlock || north instanceof ButtonBlock || north instanceof PressurePlateBlock
                || north instanceof RepeaterBlock || north instanceof ComparatorBlock || north instanceof TripWireHookBlock
                || north instanceof ObserverBlock) {
            finalText += "north, " + north.getName().getString() + ". ";
        }

        if (south instanceof DispenserBlock || south instanceof CrafterBlock || south instanceof DropperBlock
                || south instanceof LeverBlock || south instanceof ButtonBlock || south instanceof PressurePlateBlock
                || south instanceof RepeaterBlock || south instanceof ComparatorBlock || south instanceof TripWireHookBlock
                || south instanceof ObserverBlock) {
            finalText += "South, " + south.getName().getString() + ". ";
        }

        finalText += "";


        MainClass.narrate(finalText, false);
    }

}
