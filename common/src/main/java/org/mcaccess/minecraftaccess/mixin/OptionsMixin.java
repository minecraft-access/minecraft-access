package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.CameraType;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarratorDummy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.objectweb.asm.Opcodes;

@Mixin(Options.class)
public class OptionsMixin {
    @ModifyArg(
      method = "<init>",
      at = @At(value = "INVOKE", target = "Lnet/minecraft/client/OptionInstance;<init>(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplier;Lnet/minecraft/client/OptionInstance$CaptionBasedToString;Lnet/minecraft/client/OptionInstance$ValueSet;Ljava/lang/Object;Ljava/util/function/Consumer;)V"),
      slice = @Slice(
        from = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/Options;particles:Lnet/minecraft/client/OptionInstance;"), 
        to = @At(value = "FIELD", opcode = Opcodes.PUTFIELD, target = "Lnet/minecraft/client/Options;narrator:Lnet/minecraft/client/OptionInstance;")),
      index = 4)
    private static Object defaultNarratorStatus(Object original) {
        return NarratorStatus.ALL;
    }

    @Inject(at = @At("HEAD"), method = "setCameraType")
    void speakPerspectiveWhenSet(CameraType perspective, CallbackInfo ci) {
        String keyword = perspective.toString().toLowerCase();
        String translated = I18n.get("minecraft_access.perspective." + keyword);
        MainClass.speakWithNarrator(I18n.get("minecraft_access.set_perspective", translated), true);
    }
}
