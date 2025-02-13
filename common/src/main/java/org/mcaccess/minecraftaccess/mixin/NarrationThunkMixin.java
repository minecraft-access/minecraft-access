package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.narration.NarrationThunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Mixin(NarrationThunk.class)
public class NarrationThunkMixin<T> {
    @Mutable
    @Final
    @Shadow
    private T contents;

    /**
     * <a href="https://minecraft.wiki/w/Formatting_codes">wiki page of formatting codes</a>
     */
    @SuppressWarnings("unchecked")
    @Inject(at = @At("TAIL"), method = "<init>")
    private void removeFormattingCodes(Object value, BiConsumer<Consumer<String>, T> transformer, CallbackInfo ci) {
        if (value instanceof String) {
            this.contents = (T) ((String) value).replaceAll("§.", "");
        }
    }
}
