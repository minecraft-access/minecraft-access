package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import com.llamalad7.mixinextras.sugar.Local;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
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
        @Inject(method = "appendNarrations",
                at = @At(value = "INVOKE",
                        target = "Lnet/minecraft/client/gui/screen/narration/NarrationMessageBuilder;" +
                                "put(Lnet/minecraft/client/gui/screen/narration/NarrationPart;Lnet/minecraft/text/Text;)V"),
                cancellable = true)
        void removeElementPositionAndUsageNarrations(NarrationMessageBuilder builder, CallbackInfo ci, @Local Screen.SelectedElementNarrationData data) {
            data.selectable.appendNarrations(builder.nextMessage());
            ci.cancel();
        }
    }
}
