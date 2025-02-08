package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.TamableAnimal;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public class TamableAnimalMixin {

    @Inject(at = @At("HEAD"), method = "spawnTamingParticles")
    private void speakEmotion(boolean positive, CallbackInfo ci) {
        String name = ((EntityAccessor) this).callGetName().getString();
        if (positive) {
            MainClass.speakWithNarrator(I18n.get("minecraft_access.read_crosshair.like_your_behavior", name), true);
        } else {
            MainClass.speakWithNarrator(I18n.get("minecraft_access.read_crosshair.dislike_your_behavior", name), true);
        }
    }
}
