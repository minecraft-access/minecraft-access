package org.mcaccess.minecraftaccess.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
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
@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Unique
    private static String minecraft_access$previousTooltipText = "";

    @Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;II)V")
    private void speakHoveredTooltip(Font font, Component text, int mouseX, int mouseY, CallbackInfo ci) {
        if (Config.getInstance().inventoryControls.enabled) return;
        minecraft_access$checkAndSpeak(text.getString());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @Inject(at = @At("HEAD"), method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;II)V")
    private void speakHoveredTooltip2(Font font, List<Component> tooltipLines, Optional<TooltipComponent> visualTooltipComponent, int mouseX, int mouseY, CallbackInfo ci) {
        if (Config.getInstance().inventoryControls.enabled) return;
        minecraft_access$speakTextList(tooltipLines);
    }

    @Inject(at = @At("HEAD"), method = "renderComponentTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V")
    private void speakHoveredTooltip3(Font font, List<Component> tooltipLines, int mouseX, int mouseY, CallbackInfo ci) {
        if (Config.getInstance().inventoryControls.enabled) return;
        minecraft_access$speakTextList(tooltipLines);
    }

    @Inject(method = "renderComponentHoverEffect", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;II)V"))
    private void speakHoveredTooltip4(Font font, Style style, int mouseX, int mouseY, CallbackInfo ci, @Local Component text) {
        if (Config.getInstance().inventoryControls.enabled) return;
        minecraft_access$checkAndSpeak(text.getString());
    }

    @Unique
    private static void minecraft_access$speakTextList(List<Component> text) {
        if (Config.getInstance().inventoryControls.enabled) return;

        StringBuilder toSpeak = new StringBuilder();
        for (Component t : text) {
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
