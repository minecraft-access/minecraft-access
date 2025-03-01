package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationThunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * The {@link net.minecraft.client.gui.narration.ScreenNarrationCollector.Output} class is the only one implementation of {@link NarrationElementOutput}.
 */
@Mixin(targets = "net.minecraft.client.gui.narration.ScreenNarrationCollector$Output")
abstract class NarrationElementOutputMixin {
    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    public void removePositionAndUsageNarrations(NarratedElementType type, NarrationThunk<?> contents, CallbackInfo ci) {
        switch (type) {
            case TITLE:
            case HINT:
                break;
            case POSITION:
            case USAGE:
                ci.cancel();
        }
    }
}
