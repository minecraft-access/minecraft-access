package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.utils.i18n.Translation;

@Mixin(TamableAnimal.class)
abstract class TamableAnimalMixin extends Entity {
    TamableAnimalMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "spawnTamingParticles", at = @At("HEAD"))
    private void narrateEmotion(boolean success, CallbackInfo ci) {
        new Translation("minecraft_access.read_crosshair.tamable_emotion")
                .variant(success ? "positive" : "negative")
                .variable("name").put(getName())
                .narrate(true);
    }
}
