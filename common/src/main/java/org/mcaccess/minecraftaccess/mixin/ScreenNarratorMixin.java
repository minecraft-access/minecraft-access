package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.config.ConfigMenu;
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
    /**
     * Use ConfigMenu as a placeholder screen since it has no text editing field.
     */
    @Unique
    private static final Screen PLACE_HOLDER = new ConfigMenu("config_menu");
    @Unique
    private Screen previousScreen = PLACE_HOLDER;

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
            this.previousScreen = PLACE_HOLDER;
            return;
        }

        // Skip every first time the new screen is opened, to speak screen title.
        if (!c.currentScreen.getClass().equals(this.previousScreen.getClass())) {
            this.previousScreen = c.currentScreen;
            return;
        }

        cir.setReturnValue("");
    }
}
