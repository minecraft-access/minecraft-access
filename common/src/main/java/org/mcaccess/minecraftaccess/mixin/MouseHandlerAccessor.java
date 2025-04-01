package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MouseHandler.class)
public interface MouseHandlerAccessor {
    @Invoker("onPress")
    void press(long windowPointer, int button, int action, int modifiers);

    @Invoker("onScroll")
    void scroll(long windowPointer, double xOffset, double yOffset);
}
