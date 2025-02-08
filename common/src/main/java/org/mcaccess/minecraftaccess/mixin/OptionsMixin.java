package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.CameraType;
import net.minecraft.client.Options;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Options.class)
public class OptionsMixin {
    @Inject(at = @At("HEAD"), method = "setCameraType")
    void speakPerspectiveWhenSet(CameraType perspective, CallbackInfo ci) {
        String keyword = perspective.toString().toLowerCase();
        String translated = I18n.get("minecraft_access.perspective." + keyword);
        MainClass.speakWithNarrator(I18n.get("minecraft_access.set_perspective", translated), true);
    }
}
