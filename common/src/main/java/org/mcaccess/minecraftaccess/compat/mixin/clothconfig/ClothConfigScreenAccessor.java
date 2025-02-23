package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClothConfigScreen.class, remap = false)
public interface ClothConfigScreenAccessor {
    @Accessor
    AbstractWidget getButtonLeftTab();

    @Accessor
    AbstractWidget getButtonRightTab();
}
