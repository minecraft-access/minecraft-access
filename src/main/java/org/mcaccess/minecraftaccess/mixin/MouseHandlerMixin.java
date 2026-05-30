package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.MouseHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.MainClass;

@Mixin(MouseHandler.class)
abstract class MouseHandlerMixin {
    @Inject(method = "turnPlayer", at = @At("HEAD"), cancellable = true)
    private void lockCamera(CallbackInfo ci) {
        if (MainClass.poiManager.lockingHandler.isPlayerLocked()) {
            ci.cancel();
        }
    }
}
