package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.narration.NarrationThunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(NarrationThunk.class)
public interface NarrationThunkAccessor {
    @Accessor
    Object getContents();
}
