package org.mcaccess.minecraftaccess.mixin;

import java.util.List;
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
import org.mcaccess.minecraftaccess.utils.events.ChangeDetector;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

@Mixin(GuiGraphicsExtractor.class)
abstract class GuiGraphicsExtractorMixin {
    @Unique
    private static ChangeDetector<String> previous;

    @Inject(method = "setTooltipForNextFrameInternal", at = @At("HEAD"))
    private void narrateTooltip(Font font, List<ClientTooltipComponent> lines, int xo, int yo, ClientTooltipPositioner positioner, @Nullable Identifier style,
                                boolean replaceExisting, CallbackInfo ci) {
        if (Config.getInstance().inventoryControls.enabled) {
            return;
        }
        Translation.Delimited combined = new Translation.Delimited('\n');
        lines.stream()
                .flatMap(component -> component instanceof ClientTextTooltipAccessor text ? Stream.of(text) : Stream.empty())
                .map(ClientTextTooltipAccessor::getText)
                .forEach(combined::put);
        if (previous.update(combined.getString())) {
            combined.narrate(true);
        }
    }
}
