package com.github.khanshoaib3.minecraft_access.mixin;

import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RecipeBookResults.class)
public interface RecipeBookResultsAccessor {
    @Accessor
    List<AnimatedResultButton> getResultButtons();

    @Accessor
    ToggleButtonWidget getNextPageButton();

    @Accessor
    ToggleButtonWidget getPrevPageButton();
}
