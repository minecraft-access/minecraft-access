package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.MainClass;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.passive.TameableEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameableEntity.class)
public class TameableEntityMixin {

    @Inject(at = @At("HEAD"), method = "showEmoteParticle")
    private void speakEmotion(boolean positive, CallbackInfo ci) {
        String name = ((EntityAccessor) this).callGetName().getString();
        if (positive) {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.read_crosshair.like_your_behavior", name), true);
        } else {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.read_crosshair.dislike_your_behavior", name), true);
        }
    }
}
