package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CommandBlockScreen;
import net.minecraft.client.gui.screen.narration.ScreenNarrator;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenNarrator.class)
public class ScreenNarratorMixin {
    @Unique
    private Screen mca$previousScreen = null;

    /**
     * The original game narration function will repeat whole text input in {@link TextFieldWidget} on every text modifying operation.
     * For example, when editing command in {@link CommandBlockScreen}, the game will say:
     * "Console Command edit box: input text......"
     * <p>
     * It's quite annoying, so we want to suppress these narrations.
     */
    @Inject(at = @At("RETURN"), method = "buildNarratorText", cancellable = true)
    public void suppressTextEditingNarration(boolean includeUnchanged, CallbackInfoReturnable<String> cir) {
        var c = MinecraftClient.getInstance();
        if (c == null || c.currentScreen == null || !(c.currentScreen.getFocused() instanceof TextFieldWidget)) {
            this.mca$previousScreen = null;
            return;
        }

        // Skip every first time the new screen is opened, to speak screen title.
        if (c.currentScreen != mca$previousScreen) {
            this.mca$previousScreen = c.currentScreen;
            return;
        }

        cir.setReturnValue("");
    }
}
