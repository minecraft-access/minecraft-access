package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.NowPlayingToast;
import net.minecraft.client.sounds.MusicManager;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NowPlayingToast.class)
public class NowPlayingToastMixin {
    @Unique
    private static Component minecraft_access$lastSong;

    @Inject(at = @At("RETURN"), method = "getNowPlayingString")
    private static void speakSong(String string, CallbackInfoReturnable<Component> cir) {
        if (cir.getReturnValue().equals(minecraft_access$lastSong)) return;
        minecraft_access$lastSong = cir.getReturnValue();
        if (Minecraft.getInstance().screen != null) return;
        String song = cir.getReturnValue().getString();
        if (song.isEmpty()) return;
        MainClass.speakWithNarrator("Now Playing: " + song, false);
    }
}
