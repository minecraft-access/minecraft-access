package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(RecipeBookComponent.class)
public interface RecipeBookComponentAccessor {
    @Accessor
    List<RecipeBookTabButton> getTabButtons();

    @Accessor
    RecipeBookTabButton getSelectedTab();

    @Accessor
    EditBox getSearchBox();

    @Accessor
    RecipeBookPage getRecipeBookPage();

    @Accessor
    StateSwitchingButton getFilterButton();

    @Invoker
    Component callGetRecipeFilterName();
}
