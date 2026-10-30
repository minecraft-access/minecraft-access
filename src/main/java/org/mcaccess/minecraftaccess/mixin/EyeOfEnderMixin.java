package org.mcaccess.minecraftaccess.mixin;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.ClientConfig;
import org.mcaccess.minecraftaccess.MainClass;

/**
 * Auto locks to the eye of ender when used.
 */
@Slf4j
@Mixin(EyeOfEnder.class)
abstract class EyeOfEnderMixin extends Entity implements ItemSupplier {
    @Shadow
    private int life;

    protected EyeOfEnderMixin(EntityType<? extends EyeOfEnder> type, Level level) {
        super(type, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (life != 1) return;
        if (!ClientConfig.getInstance().poiLocking.autoLockEyeOfEnderEntity) {
            return;
        }

        log.debug("Auto locking on eye of ender entity");
        MainClass.poiManager.lockingHandler.lockOnEntity(this);
        MainClass.narrate(I18n.get("minecraft_access.point_of_interest.locking.tracking_eye_of_ender"), true);
    }
}
