package org.mcaccess.minecraftaccess.mixin;

import com.davykager.tolk.Tolk;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Tolk.class)
abstract class TolkMixin {
    @WrapWithCondition(method = "<clinit>", at = @At(value = "INVOKE"))
    private static boolean cancelLibInit(String libName) {
        return false;
    }
}
