package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import java.util.List;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import net.minecraft.client.gui.components.AbstractWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClothConfigScreen.class, remap = false)
public interface ClothConfigScreenAccessor {
    @Accessor
    List<ClothConfigTabButton> getTabButtons();

    @Accessor
    AbstractWidget getButtonLeftTab();

    @Accessor
    AbstractWidget getButtonRightTab();
}
