package org.mcaccess.minecraftaccess.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.MainClass;

@Mixin(AnvilScreen.class)
abstract class AnvilScreenMixin {
    @Unique
    private String previousText;

    /*
     * The "drawForeground" method is continually triggered when enchant cost changes,
     * so there is a repeat check before narrating.
     * Let the original logic build the text, we don't want to repeat that.
     */
    @Inject(method = "renderLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    private void narrateCost(GuiGraphicsExtractor context, int mouseX, int mouseY, CallbackInfo ci, @Local Component text) {
        if (text instanceof Component component) {
            String textString = component.getString();
            if (!textString.equals(previousText)) {
                MainClass.narrate(textString, true);
                previousText = textString;
            }
        }
    }

    @Inject(method = "renderLabels", at = @At("RETURN"))
    private void resetWhenCostDisappears(GuiGraphicsExtractor context, int mouseX, int mouseY, CallbackInfo ci, @Local(ordinal = 2) int cost) {
        if (cost <= 0) {
            previousText = null;
        }
    }
}
