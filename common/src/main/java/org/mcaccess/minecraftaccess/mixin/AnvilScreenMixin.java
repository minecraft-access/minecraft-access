package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {

    @Unique
    private String minecraft_access$previousText;

    /**
     * The "drawForeground" method is continually triggered when enchant cost changes,
     * so there is a repeat check before speaking.
     * Let the original logic build the text, we don't want to repeat that.
     */
    @Inject(method = "drawForeground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V"))
    protected void speakCost(DrawContext context, int mouseX, int mouseY, CallbackInfo ci, @Local Text text) {
        if (text instanceof Text text_) {
            String textString = text_.getString();
            if (!textString.equals(minecraft_access$previousText)) {
                MainClass.speakWithNarrator(textString, true);
                minecraft_access$previousText = textString;
            }
        }
    }

    @Inject(method = "drawForeground", at = @At("RETURN"))
    protected void resetWhenCostDisappears(DrawContext context, int mouseX, int mouseY, CallbackInfo ci, @Local(ordinal = 2) int cost) {
        if (cost <= 0) {
            minecraft_access$previousText = null;
        }
    }
}
