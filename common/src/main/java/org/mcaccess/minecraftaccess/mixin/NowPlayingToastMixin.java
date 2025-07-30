package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.toasts.NowPlayingToast;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.MainClass;

@Mixin(NowPlayingToast.class)
public abstract class NowPlayingToastMixin {
    @Shadow
    private static Component getNowPlayingString(@Nullable String string) {
        throw new AssertionError();
    }

    @Inject(at = @At("TAIL"), method = "showToast")
    public void narrateSong(Options options, CallbackInfo ci) {
        String toastTextBuilder = I18n.get("minecraft_access.toast.shown")
                + I18n.get("minecraft_access.other.words_connection")
                + I18n.get("record.nowPlaying", getNowPlayingString(Minecraft.getInstance().getMusicManager().getCurrentMusicTranslationKey()).getString());

        MainClass.narrate(toastTextBuilder, false);
    }
}
