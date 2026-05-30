package org.mcaccess.minecraftaccess.mixin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

@Mixin(GuiGraphicsExtractor.class)
abstract class GuiGraphicsExtractorMixin {
    @Unique
    private static String previous;

    @Inject(method = "setTooltipForNextFrameInternal", at = @At("HEAD"))
    private void narrateTooltip(Font font, List<ClientTooltipComponent> lines, int xo, int yo, ClientTooltipPositioner positioner, @Nullable Identifier style, boolean replaceExisting, CallbackInfo ci) {
        if (Config.getInstance().inventoryControls.enabled) {
            return;
        }
        String combined = lines.stream()
                .flatMap(component -> component instanceof ClientTextTooltipAccessor text ? Stream.of(text) : Stream.empty())
                .map(ClientTextTooltipAccessor::getText)
                .map(NarrationUtils::formattedCharSequenceToString)
                .collect(Collectors.joining("\n"));
        if (combined.equals(previous)) {
            return;
        }
        previous = combined;
        MainClass.narrate(combined, true);
    }
}
