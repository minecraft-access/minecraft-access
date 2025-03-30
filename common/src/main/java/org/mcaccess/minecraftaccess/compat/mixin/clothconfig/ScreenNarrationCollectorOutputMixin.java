package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationThunk;
import org.mcaccess.minecraftaccess.mixin.NarrationThunkAccessor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.stream.IntStream;

@Mixin(targets = "net.minecraft.client.gui.narration.ScreenNarrationCollector$Output")
public class ScreenNarrationCollectorOutputMixin {
    @Shadow
    @Final
    private int depth;

    @Unique
    private static int mca$whiteSpaceCount = 0;

    @ModifyVariable(method = "add", at = @At(value = "HEAD"), argsOnly = true)
    public NarrationThunk<?> makeSameOptionValuesBeingNarrated(NarrationThunk<?> contents) {
        boolean clothScreenOpening = Minecraft.getInstance().screen instanceof ClothConfigScreen;
        boolean mightBeValueDepth = this.depth > 1;
        if (clothScreenOpening && mightBeValueDepth && ((NarrationThunkAccessor) contents).getContents() instanceof String content) {
            String prefix = IntStream.range(0, mca$whiteSpaceCount).mapToObj(i -> " ").reduce("", String::concat);
            mca$whiteSpaceCount = (mca$whiteSpaceCount + 1) % 5;
            return NarrationThunk.from(content + prefix);
        }
        return contents;
    }
}
