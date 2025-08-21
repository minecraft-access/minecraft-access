package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.MainClass;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {
    @Inject(at = @At("HEAD"), method = "turnPlayer", cancellable = true)
    private void lockCamera(double movementTime, CallbackInfo ci) {
        if (MainClass.poiManager.lockingHandler.isPlayerLocked()) {
            ci.cancel();
        }
    }
}
