package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.toasts.NowPlayingToast;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NowPlayingToast.class)
public abstract class NowPlayingToastMixin {
    @Invoker
    public abstract Component callGetNowPlayingString(@Nullable String string);

    @Inject(at = @At("TAIL"), method = "showToast")
    public void speakSong(Options options, CallbackInfo ci) {
        StringBuilder toastTextBuilder = new StringBuilder();
        toastTextBuilder.append(I18n.get("minecraft_access.toast.shown"))
                .append(I18n.get("minecraft_access.other.words_connection"))
                .append(I18n.get("record.nowPlaying",
                        callGetNowPlayingString(Minecraft.getInstance().getMusicManager().getCurrentMusicTranslationKey())
                                .getString()));

        MainClass.speakWithNarrator(toastTextBuilder.toString(), false);
    }
}
