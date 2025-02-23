package org.mcaccess.minecraftaccess.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.screens.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Unique
    Logger mca$log = LoggerFactory.getLogger("org.mcaccess.minecraftaccess.mixin.ScreenMixin");

    @WrapOperation(method = "keyPressed",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;changeFocus(Lnet/minecraft/client/gui/ComponentPath;)V"))
    private void debugLogFocusedComponent(Screen instance, ComponentPath path, Operation<Void> original) {
        mca$log.debug("Focus on path: {}", path);
        original.call(instance, path);
    }
}
