package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AnvilScreen.class)
public interface AnvilScreenAccessor {
    @Accessor
    EditBox getName();
}
