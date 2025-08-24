package org.mcaccess.minecraftaccess.mixin;

import java.util.Objects;

import net.minecraft.client.Minecraft;
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

@SuppressWarnings("EqualsBetweenInconvertibleTypes")
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
    @Inject(method = {"onEffectUpdated", "onEffectAdded"}, at = @At("TAIL"))
    private void narrateEffectApplication(MobEffectInstance effectInstance, boolean forced, Entity entity, CallbackInfo ci) {
        if (Objects.equals(Minecraft.getInstance().player, this)) {
            EffectNarrator.narrateGained(effectInstance);
        }
    }

    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"))
    private void narrateEffectRemoval(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (Objects.equals(Minecraft.getInstance().player, this)) {
            EffectNarrator.narrateLost(effect.value());
        }
    }
}
