package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.utils.i18n.Translation;

@Mixin(TamableAnimal.class)
abstract class TamableAnimalMixin extends Animal {
    TamableAnimalMixin(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("HEAD"), method = "spawnTamingParticles")
    private void narrateEmotion(boolean positive, CallbackInfo ci) {
        new Translation("minecraft_access.read_crosshair.tamable_emotion")
                .variant(positive ? "positive" : "negative")
                .variable("name").put(getName())
                .narrate(true);
    }
}
