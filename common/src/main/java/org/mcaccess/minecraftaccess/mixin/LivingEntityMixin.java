package org.mcaccess.minecraftaccess.mixin;

import java.util.Objects;

import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.mcaccess.minecraftaccess.features.EffectNarrator;
import org.mcaccess.minecraftaccess.utils.WorldUtils;

@SuppressWarnings("EqualsBetweenInconvertibleTypes")
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {

    @Inject(method = "onEffectUpdated", at = @At("TAIL"))
    public void narrateEffectApplication(MobEffectInstance effectInstance, boolean forced, Entity entity, CallbackInfo ci) {
        if (Objects.equals(WorldUtils.getClientPlayer(), this)) {
            EffectNarrator.narrateGained(effectInstance);
        }
    }

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    public void narrateEffectApplication2(MobEffectInstance effectInstance, Entity entity, CallbackInfo ci) {
        if (Objects.equals(WorldUtils.getClientPlayer(), this)) {
            EffectNarrator.narrateGained(effectInstance);
        }
    }

    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"))
    public void narrateEffectRemoval(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (Objects.equals(WorldUtils.getClientPlayer(), this)) {
            EffectNarrator.narrateLost(effect.value());
        }
    }
}
