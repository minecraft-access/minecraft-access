package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.GameNarrator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.NarratorStatus;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameNarrator.class)
public class GameNarratorMixin {
    @Inject(at = @At("HEAD"), method = "sayNow(Ljava/lang/String;)V", cancellable = true)
    private void sayNow(String text, CallbackInfo callbackInfo) {
        if (MainClass.getScreenReader() == null || !MainClass.getScreenReader().isInitialized()) {
            return;
        }
        if (Minecraft.getInstance().options.narrator().get() != NarratorStatus.OFF) {
            MainClass.getScreenReader().say(text, MainClass.interrupt);
            MainClass.interrupt = true; // The default value
        }
        callbackInfo.cancel();
    }

    @Inject(at = @At("HEAD"), method = "sayChat", cancellable = true)
    private void sayChat(Component text, CallbackInfo callbackInfo) {
        if (Minecraft.getInstance().options.narrator().get().shouldNarrateChat()) {
            String string = text.getString();
            MainClass.getScreenReader().say(string, false);
        }
        callbackInfo.cancel();
    }

    @Inject(at = @At("HEAD"), method = "say", cancellable = true)
    private void say(Component text, CallbackInfo callbackInfo) {
        String string = text.getString();
        if (Minecraft.getInstance().options.narrator().get().shouldNarrateSystem() && !string.isEmpty()) {
            MainClass.getScreenReader().say(string, true);
        }
        callbackInfo.cancel();
    }

    @Inject(at = @At("HEAD"), method = "updateNarratorStatus", cancellable = true)
    private void updateNarratorStatus(NarratorStatus mode, CallbackInfo callbackInfo) {
        ToastManager toastManager = Minecraft.getInstance().getToastManager();
        if (MainClass.getScreenReader() != null && MainClass.getScreenReader().isInitialized()) {
            MainClass.getScreenReader().say(Component.translatable("options.narrator").append(" : ").append(mode.getName()).getString(), true);
            if (mode == NarratorStatus.OFF) {
                SystemToast.addOrUpdate(toastManager, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.translatable("narrator.toast.disabled"), null);
            } else {
                SystemToast.addOrUpdate(toastManager, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.translatable("narrator.toast.enabled"), mode.getName());
            }
        } else {
            SystemToast.addOrUpdate(toastManager, SystemToast.SystemToastId.NARRATOR_TOGGLE, Component.translatable("narrator.toast.disabled"), Component.translatable("options.narrator.notavailable"));
        }
        callbackInfo.cancel();
    }
}
