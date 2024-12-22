package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.MainClass;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.Perspective;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {
    @Inject(method = "setPerspective", at = @At("HEAD"))
    void speakPerspectiveWhenSet(Perspective perspective, CallbackInfo ci) {
        String keyword = perspective.toString().toLowerCase();
        String translated = I18n.translate("minecraft_access.perspective." + keyword);
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.set_perspective", translated), true);
    }
}
