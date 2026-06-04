package org.mcaccess.minecraftaccess.mixin;

import java.util.Objects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
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

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

@SuppressWarnings("EqualsBetweenInconvertibleTypes")
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
    @Inject(method = "onEffectUpdated", at = @At("TAIL"))
    private void narrateEffectApplication(MobEffectInstance effect, boolean doRefreshAttributes, Entity source, CallbackInfo ci) {
        if (Objects.equals(Minecraft.getInstance().player, this)) {
            if (Minecraft.getInstance().player.hasEffect(effect.getEffect())) return;
            String effectName = NarrationUtils.narrateEffect(effect);
            MainClass.narrate(I18n.get("minecraft_access.effect_narration.gained") + ' ' + effectName, false);
        }
    }

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void narrateEffectApplication2(MobEffectInstance effect, Entity source, CallbackInfo ci) {
        if (Objects.equals(Minecraft.getInstance().player, this)) {
            if (Minecraft.getInstance().player.hasEffect(effect.getEffect())) return;
            String effectName = NarrationUtils.narrateEffect(effect);
            MainClass.narrate(I18n.get("minecraft_access.effect_narration.gained") + ' ' + effectName, false);
        }
    }

    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"))
    private void narrateEffectRemoval(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (Objects.equals(Minecraft.getInstance().player, this)) {
            String effectName = I18n.get(effect.value().getDescriptionId());
            MainClass.narrate(I18n.get("minecraft_access.effect_narration.lost") + ' ' + effectName, false);
        }
    }
}
