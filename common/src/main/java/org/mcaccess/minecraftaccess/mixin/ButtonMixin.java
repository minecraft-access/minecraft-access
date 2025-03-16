package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Button.class)
public class ButtonMixin {
    @Inject(at = @At("HEAD"), method = "updateWidgetNarration", cancellable = true) // From 1.19.3
    private void appendNarrations(NarrationElementOutput builder, CallbackInfo callbackInfo) {
        if(Minecraft.getInstance().screen instanceof MerchantScreen) callbackInfo.cancel();
    }
}
