package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarrationThunk;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.Config;

/**
 * The {@link ScreenNarrationCollector.Output} class is the only one implementation of {@link NarrationElementOutput}.
 */
@Mixin(targets = "net.minecraft.client.gui.narration.ScreenNarrationCollector$Output")
abstract class NarrationElementOutputMixin {
    @Inject(method = "add", at = @At("HEAD"), cancellable = true)
    private void removePositionAndUsageNarrations(NarratedElementType type, NarrationThunk<?> contents, CallbackInfo ci) {
        if (Config.getInstance().speechSettings.narrateHints) {
            return;
        }

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
