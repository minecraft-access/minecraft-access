package org.mcaccess.minecraftaccess.mixin;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.GameNarrator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderController;
import org.mcaccess.minecraftaccess.utils.NarratorDummy;

@Mixin(GameNarrator.class)
abstract class GameNarratorMixin {
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/text2speech/Narrator;getNarrator()Lcom/mojang/text2speech/Narrator;"))
    private Narrator redirectGetNarrator() {
        return new NarratorDummy();
    }

    @Inject(at = @At("HEAD"), method = "narrateMessage", cancellable = true)
    private void narrateMessage(String text, boolean interrupt, CallbackInfo callbackInfo) {
        if (!ScreenReaderController.isInitialized()) {
            return;
        }
        ScreenReaderController.narrate(text, interrupt);
        callbackInfo.cancel();
    }
}
