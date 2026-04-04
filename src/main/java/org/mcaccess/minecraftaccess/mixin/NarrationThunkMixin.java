package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.narration.NarrationThunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.mcaccess.minecraftaccess.duck.NarrationThunkExt;

@Mixin(NarrationThunk.class)
abstract class NarrationThunkMixin implements NarrationThunkExt {
    @Unique
    private Object deduplication;

    @Override
    public void setDeduplication(Object deduplication) {
        this.deduplication = deduplication;
    }

    @Inject(method = "equals", at = @At("HEAD"), cancellable = true)
    private void useDeduplicationForEquals(Object other, CallbackInfoReturnable<Boolean> cir) {
        if (deduplication != null) {
            cir.setReturnValue(other instanceof NarrationThunkMixin thunk && deduplication == thunk.deduplication);
        }
    }
}
