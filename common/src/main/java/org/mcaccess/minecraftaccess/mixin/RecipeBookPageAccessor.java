package org.mcaccess.minecraftaccess.mixin;

import java.util.List;

import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookPage.class)
public interface RecipeBookPageAccessor {
    @Accessor
    List<RecipeButton> getButtons();

    @Accessor
    StateSwitchingButton getForwardButton();

    @Accessor
    StateSwitchingButton getBackButton();
}
