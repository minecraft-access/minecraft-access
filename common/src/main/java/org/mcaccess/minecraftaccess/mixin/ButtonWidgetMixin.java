package com.github.khanshoaib3.minecraft_access.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.MerchantScreen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ButtonWidget.class)
public class ButtonWidgetMixin {
    //    @Inject(at = @At("HEAD"), method = "appendNarrations", cancellable = true) // Pre 1.19.3
    @Inject(at = @At("HEAD"), method = "appendClickableNarrations", cancellable = true) // From 1.19.3
    private void appendNarrations(NarrationMessageBuilder builder, CallbackInfo callbackInfo) {
        if(MinecraftClient.getInstance().currentScreen instanceof MerchantScreen) callbackInfo.cancel();
    }
}
