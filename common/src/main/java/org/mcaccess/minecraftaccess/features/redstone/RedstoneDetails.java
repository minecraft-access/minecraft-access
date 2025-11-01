package org.mcaccess.minecraftaccess.features.redstone;

import net.minecraft.client.resources.language.I18n;
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
import org.mcaccess.minecraftaccess.MainClass;

public class RedstoneDetails {

    public static void describe(Level world, Block block, BlockPos pos) {
        // Get around blocks
        Block north = world.getBlockState(pos.north()).getBlock();
        Block south = world.getBlockState(pos.south()).getBlock();
        Block west  = world.getBlockState(pos.west()).getBlock();
        Block east  = world.getBlockState(pos.east()).getBlock();

        String finalText = I18n.get("minecraft_access.redstonedetails.possible") + ". ";

        if (isRedstoneBlock(east))  finalText += I18n.get("minecraft_access.direction.east")  + ": " + east.getName().getString()  + ". ";
        if (isRedstoneBlock(west))  finalText += I18n.get("minecraft_access.direction.west")  + ": " + west.getName().getString()  + ". ";
        if (isRedstoneBlock(north)) finalText += I18n.get("minecraft_access.direction.north") + ": " + north.getName().getString() + ". ";
        if (isRedstoneBlock(south)) finalText += I18n.get("minecraft_access.direction.south") + ": " + south.getName().getString() + ". ";

        MainClass.narrate(finalText, false);
    }

    // This Method checks if is a redstone block
    private static boolean isRedstoneBlock(Block block) {
        return block instanceof DispenserBlock
                || block instanceof CrafterBlock
                || block instanceof DropperBlock
                || block instanceof LeverBlock
                || block instanceof ButtonBlock
                || block instanceof PressurePlateBlock
                || block instanceof RepeaterBlock
                || block instanceof ComparatorBlock
                || block instanceof TripWireHookBlock
                || block instanceof ObserverBlock;
    }
}
