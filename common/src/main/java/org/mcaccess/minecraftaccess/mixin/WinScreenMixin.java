package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WinScreen.class)
public class WinScreenMixin {
    @Inject(at = @At("HEAD"), method = "init")
    public void init(CallbackInfo ci) {
        MainClass.speakWithNarrator(I18n.get("minecraft_access.credits_screen.started_tip"), false);
    }
}
