package org.mcaccess.minecraftaccess.mixin;

import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.mcaccess.minecraftaccess.features.access_menu.AccessMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    /**
     * {@link AccessMenu} allows menu functions to be triggered when
     * no screen opened and alt key with number key are pressed.
     * We need to suppress original hotbar slot selecting feature.
     */
    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z"),
            method = "handleKeybinds",
            cancellable = true)
    private void suppressHotbarSlotSelecting(CallbackInfo ci) {
        if (Screen.hasAltDown()) {
            ci.cancel();
        }
    }

    /**
     * Minecraft brings up the accessibility onboarding screen on first
     * launch, which speaks a message without using GameNarrator. This
     * behaviour requires Flite on Linux, and the narrator should be enabled
     * by default anyway because all other Minecraft Access features are
     * enabled by default.
     */
    @Redirect(method = "addInitialScreens", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", ordinal = 0))
    private boolean redirectAdd(List<?> list, Object ScreenFactory) {
      return false;
    }
}
