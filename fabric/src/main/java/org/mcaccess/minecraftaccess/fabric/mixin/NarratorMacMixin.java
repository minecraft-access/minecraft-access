package org.mcaccess.minecraftaccess.fabric.mixin;

import com.mojang.text2speech.NarratorMac;
import net.minecraft.client.Minecraft;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NarratorMac.class)
public class NarratorMacMixin {
    @Inject(at = @At("HEAD"), method = "say", remap = false, cancellable = true)
    public void say(String msg, boolean interrupt, CallbackInfo info) {
        if (MainClass.getScreenReader() == null || !MainClass.getScreenReader().isInitialized())
            return;

        if (Minecraft.getInstance().options.narrator().get().shouldNarrateSystem())
            MainClass.getScreenReader().say(msg, interrupt);

        info.cancel();
    }
}
