package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import net.minecraft.client.gui.widget.ClickableWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(value = ClothConfigScreen.class, remap = false)
public interface ClothConfigScreenAccessor {
    @Accessor
    List<ClothConfigTabButton> getTabButtons();

    @Accessor
    ClickableWidget getButtonLeftTab();

    @Accessor
    ClickableWidget getButtonRightTab();
}
