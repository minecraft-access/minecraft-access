package org.mcaccess.minecraftaccess.mixin;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.features.point_of_interest.LockingHandler;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EyeOfEnderEntity;
import net.minecraft.entity.FlyingItemEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Auto locks to the eye of ender when used.
 */
@Slf4j
@Mixin(EyeOfEnderEntity.class)
public abstract class EyeOfEnderEntityMixin extends Entity implements FlyingItemEntity {
    @Shadow
    private int lifespan;

    public EyeOfEnderEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo callbackInfo) {
        if (this.lifespan != 1) return;
        if (!Config.getInstance().poi.locking.autoLockEyeOfEnderEntity)
            return;

       log.debug("Auto locking on eye of ender entity");
        LockingHandler.getInstance().lockOnEntity(this);
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.point_of_interest.locking.tracking_eye_of_ender"), true);
    }
}
