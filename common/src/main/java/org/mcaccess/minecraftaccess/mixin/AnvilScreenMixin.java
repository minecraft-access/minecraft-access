package org.mcaccess.minecraftaccess.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.MainClass;
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
    @Inject(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V"))
    protected void speakCost(GuiGraphics context, int mouseX, int mouseY, CallbackInfo ci, @Local Component text) {
        if (text instanceof Component text_) {
            String textString = text_.getString();
            if (!textString.equals(minecraft_access$previousText)) {
                MainClass.speakWithNarrator(textString, true);
                minecraft_access$previousText = textString;
            }
        }
    }

    @Inject(method = "renderLabels", at = @At("RETURN"))
    protected void resetWhenCostDisappears(GuiGraphics context, int mouseX, int mouseY, CallbackInfo ci, @Local(ordinal = 2) int cost) {
        if (cost <= 0) {
            minecraft_access$previousText = null;
        }
    }
}
