package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.features.access_menu.AccessMenu;

@Mixin(Minecraft.class)
abstract class MinecraftMixin {
    @Shadow
    public abstract boolean hasAltDown();

    /**
     * {@link AccessMenu} allows menu functions to be triggered when
     * no screen opened and alt key with number key are pressed.
     * We need to suppress original hotbar slot selecting feature.
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"),
            method = "handleKeybinds",
            cancellable = true)
    private void suppressHotbarSlotSelecting(CallbackInfo ci) {
        if (hasAltDown()) {
            ci.cancel();
        }
    }
}
