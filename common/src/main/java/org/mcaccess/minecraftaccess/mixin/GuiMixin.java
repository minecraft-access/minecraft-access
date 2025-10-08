package org.mcaccess.minecraftaccess.mixin;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;

/**
 * Narrates the currently selected hotbar item's name and the action bar.
 * Narrates titles
 */
@Mixin(Gui.class)
abstract class GuiMixin {
    @Shadow
    private Component title;

    @Shadow
    private Component subtitle;

    @Unique
    private String previousActionBarContent = "";

    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V")
    private void narrateActionbar(Component message, boolean tinted, CallbackInfo ci) {
        Config config = Config.getInstance();
        if (config.features.actionBarEnabled) {
            String msg = message.getString();
            boolean contentChanged = !previousActionBarContent.equals(msg);
            if (contentChanged) {
                if (config.features.onlyNarrateActionBarUpdates) {
                    onlyNarrateChangedParts(msg);
                } else {
                    MainClass.narrate(msg, true);
                }
                previousActionBarContent = msg;
            }
        }
    }

    @Unique
    private void onlyNarrateChangedParts(String msg) {
        List<String> parts = Arrays.asList(splitToParts(msg));
        List<String> previousParts = Arrays.asList(splitToParts(previousActionBarContent));
        parts.removeAll(previousParts);
        String narration = String.join(", ", parts);
        MainClass.narrate(narration, true);
    }

    @Inject(method = "setTitle", at = @At("TAIL"))
    private void setTitleMixin(Component title, CallbackInfo ci) {
        MainClass.narrate(title.getString(), true);
        if (subtitle != null) MainClass.narrate(subtitle.getString(), false);
    }

    @Inject(method = "setSubtitle", at = @At("HEAD"))
    private void setSubtitleMixin(Component subtitle, CallbackInfo ci) {
        if (title != null && this.subtitle == null) MainClass.narrate(subtitle.getString(), false);
    }

    @Unique
    private String[] splitToParts(String msg) {
        if (msg.contains(",")) {
            return msg.split(",");
        } else {
            return msg.split("\\s");
        }
    }
}
