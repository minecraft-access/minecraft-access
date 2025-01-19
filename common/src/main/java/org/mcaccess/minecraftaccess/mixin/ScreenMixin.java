package org.mcaccess.minecraftaccess.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Screen.class)
public abstract class ScreenMixin {
    @Inject(at = @At("TAIL"), method = "hasUsageText", cancellable = true)
    private void removeScreenUsageNarrations(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "addElementNarrations",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/narration/NarrationMessageBuilder;" +
                            "put(Lnet/minecraft/client/gui/screen/narration/NarrationPart;Lnet/minecraft/text/Text;)V"),
            cancellable = true)
    private void removeElementPositionAndUsageNarrations(NarrationMessageBuilder builder, CallbackInfo ci, @Local Screen.SelectedElementNarrationData data) {
        data.selectable.appendNarrations(builder.nextMessage());
        ci.cancel();
    }
}
