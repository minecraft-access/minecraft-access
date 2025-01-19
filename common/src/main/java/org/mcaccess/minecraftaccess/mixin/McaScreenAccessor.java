package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(Screen.class)
public interface McaScreenAccessor {
    @Accessor
    List<Selectable> getSelectables();
}
