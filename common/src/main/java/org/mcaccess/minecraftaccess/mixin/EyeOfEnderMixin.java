package org.mcaccess.minecraftaccess.mixin;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.level.Level;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.features.point_of_interest.LockingHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Auto locks to the eye of ender when used.
 */
@Slf4j
@Mixin(EyeOfEnder.class)
public abstract class EyeOfEnderMixin extends Entity implements ItemSupplier {
    @Shadow
    private int life;

    public EyeOfEnderMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    private void tick(CallbackInfo callbackInfo) {
        if (this.life != 1) return;
        if (!Config.getInstance().poi.locking.autoLockEyeOfEnderEntity)
            return;

       log.debug("Auto locking on eye of ender entity");
        LockingHandler.getInstance().lockOnEntity(this);
        MainClass.speakWithNarrator(I18n.get("minecraft_access.point_of_interest.locking.tracking_eye_of_ender"), true);
    }
}
