package org.mcaccess.minecraftaccess.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.CameraType;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.resources.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.MainClass;

@Mixin(Options.class)
abstract class OptionsMixin {
    @Definition(id = "OptionInstance", type = OptionInstance.class)
    @Expression("new OptionInstance('options.narrator', ?, ?, ?, ?, ?)")
    @ModifyArg(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"), index = 4)
    private Object defaultNarratorStatus(Object original) {
        return NarratorStatus.ALL;
    }

    @Definition(id = "KeyMapping", type = KeyMapping.class)
    @Expression("new KeyMapping('key.advancements', ?, ?)")
    @ModifyArg(method = "<init>", at = @At("MIXINEXTRAS:EXPRESSION"))
    private int remapAdvancements(int keysym) {
        return InputConstants.KEY_X;
    }

    @Inject(at = @At("HEAD"), method = "setCameraType")
    private void narratePerspectiveWhenSet(CameraType perspective, CallbackInfo ci) {
        String keyword = perspective.toString().toLowerCase();
        String translated = I18n.get("minecraft_access.perspective." + keyword);
        MainClass.narrate(I18n.get("minecraft_access.set_perspective", translated), true);
    }
}
