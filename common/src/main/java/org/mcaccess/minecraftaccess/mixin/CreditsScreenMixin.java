package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.screen.CreditsScreen;
import net.minecraft.client.resource.language.I18n;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreditsScreen.class)
public class CreditsScreenMixin {
    @Inject(at = @At("HEAD"), method = "init")
    public void init(CallbackInfo ci) {
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.credits_screen.started_tip"), false);
    }
}
