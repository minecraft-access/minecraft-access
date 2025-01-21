package org.mcaccess.minecraftaccess.fabric.mixin;

import com.mojang.text2speech.NarratorWindows;
import net.minecraft.client.Minecraft;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NarratorWindows.class)
public class NarratorWindowsMixin {

    @Inject(at = @At("HEAD"), method = "say", remap = false, cancellable = true)
    public void say(String msg, boolean interrupt, CallbackInfo info) {
        if (MainClass.getScreenReader() != null && MainClass.getScreenReader().isInitialized()) {
            if (Minecraft.getInstance().options.narrator().get().shouldNarrateSystem())
                MainClass.getScreenReader().say(msg, interrupt);

            info.cancel();
        }
    }
}
