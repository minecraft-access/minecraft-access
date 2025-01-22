package org.mcaccess.minecraftaccess.neoforge.mixin;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.ItemStack;
import org.mcaccess.minecraftaccess.features.SpeakHeldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class InGameHudMixin {
    @Shadow
    private int heldItemTooltipFade;

    @Shadow
    private ItemStack currentStack;

    @Unique
    private final SpeakHeldItem minecraft_access$feature = new SpeakHeldItem();

    /**
     * The de-obfuscated original source code of fabric and neoforge are different,
     * the "renderSelectedItemName(DrawContext, int)" method doesn't exist in fabric side.
     * And we can't apply solution that mentioned in <a href="https://discord.com/channels/792699517631594506/1323926156835029022">this post</a>,
     * since the signatures of the two methods is different.
     */
    @Inject(at = @At("HEAD"), method = "renderSelectedItemName")
    public void speakHeldItem(DrawContext ignored, int yShift, CallbackInfo ci) {
        this.minecraft_access$feature.speakHeldItem(this.currentStack, this.heldItemTooltipFade);
    }
}
