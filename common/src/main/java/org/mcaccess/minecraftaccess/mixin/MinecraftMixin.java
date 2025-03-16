package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.mcaccess.minecraftaccess.features.access_menu.AccessMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    /**
     * {@link AccessMenu} allows menu functions to be triggered when
     * no screen opened and alt key with number key are pressed.
     * We need to suppress original hotbar slot selecting feature.
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"),
            method = "handleKeybinds",
            cancellable = true)
    private void suppressHotbarSlotSelecting(CallbackInfo ci) {
        if (Screen.hasAltDown()) {
            ci.cancel();
        }
    }
}
