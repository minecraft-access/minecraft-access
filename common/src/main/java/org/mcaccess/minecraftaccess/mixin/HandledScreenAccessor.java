package com.github.khanshoaib3.minecraft_access.mixin;

import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandledScreen.class)
public interface HandledScreenAccessor {
    @Accessor("x")
    int getX();

    @Accessor("y")
    int getY();

    @Accessor
    ScreenHandler getHandler();

    @Accessor
    int getBackgroundWidth();

    @Accessor
    int getBackgroundHeight();
}