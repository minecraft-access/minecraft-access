package org.mcaccess.minecraftaccess.mixin;

import java.util.List;

import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeBookTabButton;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

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
    CycleButton<Boolean> getFilterButton();

    @Invoker
    Component callGetRecipeFilterName();
}
