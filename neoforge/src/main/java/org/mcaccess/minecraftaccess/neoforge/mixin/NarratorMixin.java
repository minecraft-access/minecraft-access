package org.mcaccess.minecraftaccess.neoforge.mixin;

import org.mcaccess.minecraftaccess.MainClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.NarratorMode;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.client.toast.ToastManager;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NarratorManager.class)
public class NarratorMixin {
    @Inject(at = @At("HEAD"), method = "narrate(Ljava/lang/String;)V", cancellable = true)
    private void narrate(String text, CallbackInfo callbackInfo) {
        if (MainClass.getScreenReader() == null || !MainClass.getScreenReader().isInitialized()) {
            return;
        }
        if (MinecraftClient.getInstance().options.getNarrator().getValue() != NarratorMode.OFF) {
            MainClass.getScreenReader().say(text, MainClass.interrupt);
            MainClass.interrupt = true; // The default value
        }
        callbackInfo.cancel();
    }

    @Inject(at = @At("HEAD"), method = "narrateChatMessage", cancellable = true)
    private void narrateChatMessage(Text text, CallbackInfo callbackInfo) {
        if (MinecraftClient.getInstance().options.getNarrator().getValue().shouldNarrateChat()) {
            String string = text.getString();
            MainClass.getScreenReader().say(string, false);
        }
        callbackInfo.cancel();
    }

    @Inject(at = @At("HEAD"), method = "narrateSystemMessage", cancellable = true)
    private void narrateSystemMessage(Text text, CallbackInfo callbackInfo) {
        String string = text.getString();
        if (MinecraftClient.getInstance().options.getNarrator().getValue().shouldNarrateSystem() && !string.isEmpty()) {
            MainClass.getScreenReader().say(string, true);
        }
        callbackInfo.cancel();
    }

    @Inject(at = @At("HEAD"), method = "onModeChange", cancellable = true)
    private void onModeChange(NarratorMode mode, CallbackInfo callbackInfo) {
        ToastManager toastManager = MinecraftClient.getInstance().getToastManager();
        if (MainClass.getScreenReader() != null && MainClass.getScreenReader().isInitialized()) {
            MainClass.getScreenReader().say(Text.translatable("options.narrator").append(" : ").append(mode.getName()).getString(), true);
            if (mode == NarratorMode.OFF) {
                SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("narrator.toast.disabled"), null);
            } else {
                SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("narrator.toast.enabled"), mode.getName());
            }
        } else {
            SystemToast.show(toastManager, SystemToast.Type.NARRATOR_TOGGLE, Text.translatable("narrator.toast.disabled"), Text.translatable("options.narrator.notavailable"));
        }
        callbackInfo.cancel();
    }
}
