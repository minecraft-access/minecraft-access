package com.github.khanshoaib3.minecraft_access.mixin;


import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;
import com.github.khanshoaib3.minecraft_access.features.SpeakHeldItem;
import com.github.khanshoaib3.minecraft_access.utils.StringUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
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
 */
@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    private int heldItemTooltipFade;

    @Shadow
    private ItemStack currentStack;

    @Unique
    private final SpeakHeldItem minecraft_access$feature = new SpeakHeldItem();

    @Unique
    private String minecraft_access$previousActionBarContent = "";

    /**
     * This method is continually invoked by the InGameHud.render(),
     * so we use previousContent to check if the content has changed and need to be narrated.
     */
    @Inject(at = @At("RETURN"), method = "renderHeldItemTooltip")
    public void renderHeldItemTooltipMixin(DrawContext context, CallbackInfo ci) {
        this.minecraft_access$feature.speakHeldItem(this.currentStack, this.heldItemTooltipFade);
    }

    @Inject(at = @At("HEAD"), method = "setOverlayMessage(Lnet/minecraft/text/Text;Z)V")
    public void speakActionbar(Text message, boolean tinted, CallbackInfo ci) {
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
}
