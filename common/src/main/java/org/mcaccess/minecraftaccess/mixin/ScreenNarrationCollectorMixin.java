package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenNarrationCollector.class)
public class ScreenNarrationCollectorMixin {
    @Unique
    private Screen mca$previousScreen = null;

    /**
     * The original game narration function will repeat whole text input in {@link EditBox} on every text modifying operation.
     * For example, when editing command in {@link CommandBlockEditScreen}, the game will say:
     * "Console Command edit box: input text......"
     * <p>
     * It's quite annoying, so we want to suppress these narrations.
     */
    @Inject(at = @At("RETURN"), method = "collectNarrationText", cancellable = true)
    public void suppressTextEditingNarration(boolean collectAll, CallbackInfoReturnable<String> cir) {
        var c = Minecraft.getInstance();
        if (c.screen == null || !(c.screen.getFocused() instanceof EditBox)) {
            this.mca$previousScreen = null;
            return;
        }

        // Skip every first time the new screen is opened, to speak screen title.
        if (!c.screen.getClass().equals(this.mca$previousScreen.getClass())) {
            this.mca$previousScreen = c.screen;
            return;
        }

        cir.setReturnValue("");
    }
}
