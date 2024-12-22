package com.github.khanshoaib3.minecraft_access.mixin;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RecipeBookWidget.class)
public interface RecipeBookWidgetAccessor {
    @Accessor
    List<RecipeGroupButtonWidget> getTabButtons();

    @Accessor
    RecipeGroupButtonWidget getCurrentTab();

    @Accessor
    TextFieldWidget getSearchField();

    @Accessor
    RecipeBookResults getRecipesArea();

    @Accessor
    ClientRecipeBook getRecipeBook();

    @Accessor
    RecipeMatcher getRecipeFinder();

    @Accessor
    AbstractRecipeScreenHandler<?, ?> getCraftingScreenHandler();

    @Accessor
    ToggleButtonWidget getToggleCraftableButton();
}
