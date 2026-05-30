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
    private String previousLine;

    /*
     * The "drawForeground" method is continually triggered when enchant cost changes,
     * so there is a repeat check before narrating.
     * Let the original logic build the text, we don't want to repeat that.
     */
    @Inject(method = "extractLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V"))
    private void narrateCost(GuiGraphicsExtractor graphics, int xm, int ym, CallbackInfo ci, @Local Component line) {
        if (line instanceof Component component) {
            String lineString = component.getString();
            if (!lineString.equals(previousLine)) {
                MainClass.narrate(lineString, true);
                previousLine = lineString;
            }
        }
    }

    @Inject(method = "extractLabels", at = @At("RETURN"))
    private void resetWhenCostDisappears(GuiGraphicsExtractor graphics, int xm, int ym, CallbackInfo ci, @Local(ordinal = 2) int cost) {
        if (cost <= 0) {
            previousLine = null;
        }
    }
}
