package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.InventoryControlsConfigMap;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;

/**
 * Speak hovered tooltip when Inventory Controls is disabled.
 * Need to get text before they turned into "OrderedText" or "TooltipComponent"
 * (can't easily extract text from these types).
 * So we intercept every "drawTooltip" invoke that in "drawHoverEvent" method.
 */
@Mixin(DrawContext.class)
public class DrawContextMixin {
    @Unique
    private static String minecraft_access$previousTooltipText = "";

    @Inject(at = @At("HEAD"), method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;II)V")
    private void speakHoveredTooltip(TextRenderer textRenderer, Text text, int x, int y, CallbackInfo ci) {
        if (InventoryControlsConfigMap.getInstance().isEnabled()) return;
        minecraft_access$checkAndSpeak(text.getString());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Inject(at = @At("HEAD"), method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V")
    private void speakHoveredTooltip2(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, CallbackInfo ci) {
        if (InventoryControlsConfigMap.getInstance().isEnabled()) return;
        minecraft_access$speakTextList(text);
    }

    @Inject(at = @At("HEAD"), method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II)V")
    private void speakHoveredTooltip3(TextRenderer textRenderer, List<Text> text, int x, int y, CallbackInfo ci) {
        if (InventoryControlsConfigMap.getInstance().isEnabled()) return;
        minecraft_access$speakTextList(text);
    }

    @Inject(method = "drawHoverEvent", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/DrawContext;drawOrderedTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;II)V"))
    private void speakHoveredTooltip4(TextRenderer textRenderer, Style style, int x, int y, CallbackInfo ci, @Local Text text) {
        if (InventoryControlsConfigMap.getInstance().isEnabled()) return;
        minecraft_access$checkAndSpeak(text.getString());
    }

    @Unique
    private static void minecraft_access$speakTextList(List<Text> text) {
        if (InventoryControlsConfigMap.getInstance().isEnabled()) return;

        StringBuilder toSpeak = new StringBuilder();
        for (Text t : text) {
            toSpeak.append(t.getString()).append("\n");
        }

        minecraft_access$checkAndSpeak(toSpeak.toString());
    }

    @Unique
    private static void minecraft_access$checkAndSpeak(String toSpeak) {
        if (minecraft_access$previousTooltipText.equals(toSpeak)) return;
        if (toSpeak.isBlank()) return;

        minecraft_access$previousTooltipText = toSpeak;
        MainClass.speakWithNarrator(minecraft_access$previousTooltipText, true);
    }
}
