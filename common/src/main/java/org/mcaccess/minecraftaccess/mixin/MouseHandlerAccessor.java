package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.MouseHandler;
import net.minecraft.client.input.MouseButtonInfo;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MouseHandler.class)
public interface MouseHandlerAccessor {
    @Invoker
    void invokeOnMove(long windowPointer, double x, double y);

    @Invoker
    void invokeOnButton(long window, MouseButtonInfo buttonInfo, int action);

    @Invoker
    void invokeOnScroll(long windowPointer, double xOffset, double yOffset);
}
