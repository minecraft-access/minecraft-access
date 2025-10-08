package org.mcaccess.minecraftaccess.mixin;

import com.mojang.text2speech.Narrator;
import net.minecraft.client.gui.screens.AccessibilityOnboardingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import org.mcaccess.minecraftaccess.utils.NarratorDummy;

@Mixin(AccessibilityOnboardingScreen.class)
abstract class AccessibilityOnboardingScreenMixin {
    @Redirect(method = "close", at = @At(value = "INVOKE", target = "Lcom/mojang/text2speech/Narrator;getNarrator()Lcom/mojang/text2speech/Narrator;"))
    private Narrator redirectGetNarratorForClose() {
        return new NarratorDummy();
    }

    @Redirect(
            method = "handleInitialNarrationDelay",
            at = @At(value = "INVOKE", target = "Lcom/mojang/text2speech/Narrator;getNarrator()Lcom/mojang/text2speech/Narrator;")
    )
    private Narrator redirectGetNarratorForHandleInitialNarrationDelay() {
        return new NarratorDummy();
    }
}
