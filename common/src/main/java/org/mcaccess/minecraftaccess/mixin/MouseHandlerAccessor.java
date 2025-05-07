package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MouseHandler.class)
public interface MouseHandlerAccessor {
    @Invoker
    void invokeOnMove(long windowPointer, double x, double y);

    @Invoker
    void invokeOnPress(long windowPointer, int button, int action, int modifiers);

    @Invoker
    void invokeOnScroll(long windowPointer, double xOffset, double yOffset);
}
