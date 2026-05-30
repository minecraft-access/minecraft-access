package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Button.class)
abstract class ButtonMixin {
    @Inject(method = "updateWidgetNarration", at = @At("HEAD"), cancellable = true)
    private void appendNarrations(CallbackInfo ci) {
        if (Minecraft.getInstance().screen instanceof MerchantScreen) ci.cancel();
    }
}
