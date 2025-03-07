package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.config.config_maps.OtherConfigsMap;
import org.mcaccess.minecraftaccess.utils.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

/**
 * Narrates/Speaks the currently selected hotbar item's name and the action bar.
 * Narrates titles
 */
@Mixin(Gui.class)
public class GuiMixin {
    @Shadow
    private Component title;

    @Shadow
    private Component subtitle;

    @Unique
    private String minecraft_access$previousActionBarContent = "";

    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V")
    public void speakActionbar(Component message, boolean tinted, CallbackInfo ci) {
        OtherConfigsMap configsMap = OtherConfigsMap.getInstance();
        if (configsMap.isActionBarEnabled()) {
            String msg = message.getString();
            boolean contentChanged = !this.minecraft_access$previousActionBarContent.equals(msg);
            if (contentChanged) {
                if (configsMap.isOnlySpeakActionBarUpdates()) {
                    minecraft_access$onlySpeakChangedParts(msg);
                } else {
                    MainClass.speakWithNarratorIfNotEmpty(msg, true);
                }
                this.minecraft_access$previousActionBarContent = msg;
            }
        }
    }

    @Unique
    private void minecraft_access$onlySpeakChangedParts(String msg) {
        List<String> parts = Arrays.asList(StringUtils.splitToParts(msg));
        List<String> previousParts = Arrays.asList(StringUtils.splitToParts(this.minecraft_access$previousActionBarContent));
        parts.removeAll(previousParts);
        String toSpeak = String.join(", ", parts);
        MainClass.speakWithNarratorIfNotEmpty(toSpeak, true);
    }

    @Inject(method = "setTitle", at = @At("TAIL"))
    public void setTitleMixin(Component title, CallbackInfo ci) {
        MainClass.speakWithNarrator(title.getString(), true);
        if (subtitle != null) MainClass.speakWithNarrator(subtitle.getString(), false);
    }

    @Inject(method = "setSubtitle", at = @At("HEAD"))
    public void setSubtitleMixin(Component subtitle, CallbackInfo ci) {
        if (title != null && this.subtitle == null) MainClass.speakWithNarrator(subtitle.getString(), false);
    }
}
