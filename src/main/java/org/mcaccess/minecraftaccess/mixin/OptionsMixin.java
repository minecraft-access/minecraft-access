package org.mcaccess.minecraftaccess.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.CameraType;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.Options;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.utils.i18n.Translation;

@Mixin(Options.class)
abstract class OptionsMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/OptionInstance;<init>(Ljava/lang/String;Lnet/minecraft/client/OptionInstance$TooltipSupplier;Lnet/minecraft/client/OptionInstance$CaptionBasedToString;Lnet/minecraft/client/OptionInstance$ValueSet;Ljava/lang/Object;Ljava/util/function/Consumer;)V"
            ),
            index = 4,
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            opcode = Opcodes.LDC,
                            args = "stringValue=options.narrator"
                    ),
                    to = @At(
                            value = "FIELD",
                            opcode = Opcodes.PUTFIELD,
                            target = "Lnet/minecraft/client/Options;narrator:Lnet/minecraft/client/OptionInstance;"
                    )
            )
    )
    private Object defaultNarratorStatus(Object original) {
        return NarratorStatus.ALL;
    }

    @ModifyArg(
            method = "<init>",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/KeyMapping;<init>(Ljava/lang/String;ILnet/minecraft/client/KeyMapping$Category;)V"
            ),
            index = 1,
            slice = @Slice(
                    from = @At(
                            value = "CONSTANT",
                            opcode = Opcodes.LDC,
                            args = "stringValue=key.advancements"
                    ),
                    to = @At(
                            value = "FIELD",
                            opcode = Opcodes.PUTFIELD,
                            target = "Lnet/minecraft/client/Options;keyAdvancements:Lnet/minecraft/client/KeyMapping;"
                    )
            )
    )
    private int remapAdvancements(int original) {
        return InputConstants.KEY_O;
    }

    @Inject(at = @At("HEAD"), method = "setCameraType")
    private void narratePerspectiveWhenSet(CameraType perspective, CallbackInfo ci) {
        new Translation("minecraft_access.set_perspective")
                .variable("perspective").put(new Translation("minecraft_access.perspective").variant(perspective.toString().toLowerCase()))
                .narrate(true);
    }
}
