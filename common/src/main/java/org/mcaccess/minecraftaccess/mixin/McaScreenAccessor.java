package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface McaScreenAccessor {
    @Accessor
    List<NarratableEntry> getNarratables();
}
