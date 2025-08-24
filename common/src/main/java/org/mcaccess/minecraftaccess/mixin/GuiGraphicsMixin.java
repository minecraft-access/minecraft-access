package org.mcaccess.minecraftaccess.mixin;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {
    @Unique
    private static String previous;

    @Inject(at = @At("HEAD"), method = "setTooltipForNextFrameInternal")
    private void narrateTooltip(Font font, List<ClientTooltipComponent> list, int i, int j, ClientTooltipPositioner clientTooltipPositioner, @Nullable ResourceLocation resourceLocation, boolean bl, CallbackInfo ci) {
        if (Config.getInstance().inventoryControls.enabled) {
            return;
        }
        String combined = list.stream()
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
