package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(LivingEntity.class)
abstract class LivingEntityMixin {

    @Inject(method = "forceAddEffect",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    public void narrateEffectApplication(MobEffectInstance instance, Entity entity, CallbackInfo ci) {
        mca$narrateEffectApplication(instance);
    }

    @Inject(method = "addEffect(Lnet/minecraft/world/effect/MobEffectInstance;Lnet/minecraft/world/entity/Entity;)Z",
            at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
    public void narrateEffectApplication2(MobEffectInstance instance, Entity entity, CallbackInfoReturnable<Boolean> cir) {
        mca$narrateEffectApplication(instance);
    }

    @Unique
    private void mca$narrateEffectApplication(MobEffectInstance instance) {
        //noinspection EqualsBetweenInconvertibleTypes
        if (Objects.equals(WorldUtils.getClientPlayer(), this)) {
            String gain = I18n.get("minecraft_access.effect_narration.gained");
            String effectName = NarrationUtils.narrateEffect(instance);
            MainClass.speakWithNarrator(gain + " " + effectName, true);
        }
    }

    @Inject(method = "removeEffectNoUpdate", at = @At("HEAD"))
    public void narrateEffectRemoval(Holder<MobEffect> effect, CallbackInfoReturnable<MobEffectInstance> cir) {
        //noinspection EqualsBetweenInconvertibleTypes
        if (Objects.equals(WorldUtils.getClientPlayer(), this)) {
            String lost = I18n.get("minecraft_access.effect_narration.lost");
            String effectName = I18n.get(effect.value().getDescriptionId());
            MainClass.speakWithNarrator(lost + " " + effectName, true);
        }
    }
}
