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

import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

@SuppressWarnings("EqualsBetweenInconvertibleTypes")
@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {
    @Inject(method = "onEffectUpdated", at = @At("TAIL"))
    private void narrateEffectApplication(MobEffectInstance effect, boolean doRefreshAttributes, Entity source, CallbackInfo ci) {
        if (Objects.equals(Minecraft.getInstance().player, this)) {
            if (Minecraft.getInstance().player.hasEffect(effect.getEffect())) return;
            new Translation("minecraft_access.effect_narration")
                    .variant("gained")
                    .variable("effect").put(NarrationUtils.narrateEffect(effect))
                    .narrate(false);
        }
    }

    @Inject(method = "onEffectAdded", at = @At("TAIL"))
    private void narrateEffectApplication2(MobEffectInstance effect, Entity source, CallbackInfo ci) {
        if (Objects.equals(Minecraft.getInstance().player, this)) {
            if (Minecraft.getInstance().player.hasEffect(effect.getEffect())) return;
            new Translation("minecraft_access.effect_narration")
                    .variant("gained")
                    .variable("effect").put(NarrationUtils.narrateEffect(effect))
                    .narrate(false);
        }
    }

    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"))
    private void narrateEffectRemoval(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        if (Objects.equals(Minecraft.getInstance().player, this)) {
            new Translation("minecraft_access.effect_narration")
                    .variant("lsot")
                    .variable("effect").put(new Translation.Vanilla(effect.value().getDescriptionId()))
                    .narrate(false);
        }
    }
}
