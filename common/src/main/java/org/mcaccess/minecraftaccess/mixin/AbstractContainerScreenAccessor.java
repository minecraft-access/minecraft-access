package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {
    @Accessor
    int getLeftPos();

    @Accessor
    int getTopPos();

    @Accessor
    AbstractContainerMenu getMenu();

    @Accessor
    int getImageWidth();

    @Accessor
    int getImageHeight();
}