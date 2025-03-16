package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.ScreenNarrationCollector;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import org.mcaccess.minecraftaccess.config.ConfigMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenNarrationCollector.class)
public class ScreenNarrationCollectorMixin {
    /**
     * Use ConfigMenu as a placeholder screen since it has no text editing field.
     */
    @Unique
    private static final Screen PLACE_HOLDER = new ConfigMenu("config_menu");
    @Unique
    private Screen previousScreen = PLACE_HOLDER;

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
        if (c == null || c.screen == null || !(c.screen.getFocused() instanceof EditBox)) {
            this.previousScreen = PLACE_HOLDER;
            return;
        }

        // Skip every first time the new screen is opened, to speak screen title.
        if (!c.screen.getClass().equals(this.previousScreen.getClass())) {
            this.previousScreen = c.screen;
            return;
        }

        cir.setReturnValue("");
    }
}
