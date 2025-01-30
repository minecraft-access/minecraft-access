package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import com.llamalad7.mixinextras.sugar.Local;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DynamicElementListWidget.class)
abstract class DynamicElementListWidgetMixin {
    @Mixin(DynamicElementListWidget.ElementEntry.class)
    abstract static class ElementEntryMixin {
        /**
         * Do what {@link org.mcaccess.minecraftaccess.mixin.ScreenMixin#removeElementPositionAndUsageNarrations} does
         */
        @Inject(method = "updateNarration",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/gui/narration/NarrationElementOutput;" +
                                "add(Lnet/minecraft/client/gui/narration/NarratedElementType;Lnet/minecraft/network/chat/Component;)V"),
                cancellable = true)
        void removeElementPositionAndUsageNarrations(NarrationElementOutput builder, CallbackInfo ci, @Local Screen.NarratableSearchResult data) {
            data.entry.updateNarration(builder.nest());
            ci.cancel();
        }
    }
}
