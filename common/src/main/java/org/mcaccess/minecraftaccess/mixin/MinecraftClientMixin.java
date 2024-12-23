package org.mcaccess.minecraftaccess.mixin;

import org.mcaccess.minecraftaccess.features.access_menu.AccessMenu;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    /**
     * {@link AccessMenu} allows menu functions to be triggered when
     * no screen opened and alt key with number key are pressed.
     * We need to suppress original hotbar slot selecting feature.
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;isSpectator()Z"),
            method = "handleInputEvents",
            cancellable = true)
    private void suppressHotbarSlotSelecting(CallbackInfo ci) {
        if (Screen.hasAltDown()) {
            ci.cancel();
        }
    }
}
