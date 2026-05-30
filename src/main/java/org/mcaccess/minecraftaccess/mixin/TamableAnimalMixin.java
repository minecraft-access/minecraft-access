package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.TamableAnimal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.MainClass;

@Mixin(TamableAnimal.class)
abstract class TamableAnimalMixin {
    @Inject(method = "spawnTamingParticles", at = @At("HEAD"))
    private void narrateEmotion(boolean success, CallbackInfo ci) {
        String name = ((EntityAccessor) this).callGetName().getString();
        if (success) {
            MainClass.narrate(I18n.get("minecraft_access.read_crosshair.like_your_behavior", name), true);
        } else {
            MainClass.narrate(I18n.get("minecraft_access.read_crosshair.dislike_your_behavior", name), true);
        }
    }
}
