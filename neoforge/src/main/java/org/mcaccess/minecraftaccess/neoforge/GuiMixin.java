package org.mcaccess.minecraftaccess.neoforge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemStack;
import org.mcaccess.minecraftaccess.features.SpeakHeldItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Gui.class)
abstract class GuiMixin {
    @Shadow
    private int toolHighlightTimer;

    @Shadow
    private ItemStack lastToolHighlight;

    @Unique
    private final SpeakHeldItem minecraft_access$feature = new SpeakHeldItem();

    /**
     * Same as {@link org.mcaccess.minecraftaccess.mixin.GuiMixin#speakItemName(ProfilerFiller, Operation)}
     * Neoforge patched the original {@link Gui#renderSelectedItemName(GuiGraphics)} method to make it as two methods,
     * and invoke the new method instead.
     * <a href="https://github.com/neoforged/NeoForge/blob/821e47fe8e30663d4511530787a16f9d48f38b3f/patches/net/minecraft/client/gui/Gui.java.patch#L225">patch link</a>
     */
    @WrapOperation(
            method = "renderSelectedItemName(Lnet/minecraft/client/gui/GuiGraphics;I)V",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;pop()V")
    )
    protected void speakItemName(ProfilerFiller profiler, Operation<Void> original) {
        this.minecraft_access$feature.speakHeldItem(this.lastToolHighlight, this.toolHighlightTimer);
        original.call(profiler);
    }
}
