package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.NowPlayingToast;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.utils.i18n.Translation;

@Mixin(NowPlayingToast.class)
abstract class NowPlayingToastMixin {
    @Shadow
    private static Component getNowPlayingString(@Nullable String string) {
        throw new AssertionError();
    }

    @Inject(at = @At("TAIL"), method = "showToast")
    private void narrateSong(CallbackInfo ci) {
        new Translation.Delimited()
                .put(new Translation("minecraft_access.toast.shown"))
                .put(new Translation.Vanilla("record.nowPlaying")
                        .put(getNowPlayingString(Minecraft.getInstance().getMusicManager().getCurrentMusicTranslationKey())))
                .narrate(false);
    }
}
