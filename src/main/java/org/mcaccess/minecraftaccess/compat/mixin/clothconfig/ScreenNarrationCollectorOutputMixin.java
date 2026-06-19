package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import java.util.stream.IntStream;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationThunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import org.mcaccess.minecraftaccess.mixin.NarrationThunkAccessor;

@Mixin(targets = "net.minecraft.client.gui.narration.ScreenNarrationCollector$Output")
abstract class ScreenNarrationCollectorOutputMixin {
    @Shadow
    @Final
    private int depth;

    @Unique
    private static int whiteSpaceCount = 0;

    @ModifyVariable(method = "add", at = @At("HEAD"), argsOnly = true)
    private NarrationThunk<?> makeSameOptionValuesBeingNarrated(NarrationThunk<?> contents) {
        boolean clothScreenOpening = Minecraft.getInstance().gui.screen() instanceof ClothConfigScreen;
        boolean mightBeValueDepth = depth > 1;
        if (clothScreenOpening && mightBeValueDepth && ((NarrationThunkAccessor) contents).getContents() instanceof String content) {
            String prefix = IntStream.range(0, whiteSpaceCount).mapToObj(i -> " ").reduce("", String::concat);
            whiteSpaceCount = (whiteSpaceCount + 1) % 5;
            return NarrationThunk.from(content + prefix);
        }
        return contents;
    }
}
