package com.github.khanshoaib3.minecraft_access.mixin;

import net.minecraft.client.gui.screen.recipebook.AnimatedResultButton;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import net.minecraft.recipe.RecipeEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;

@Mixin(AnimatedResultButton.class)
public interface AnimatedResultButtonAccessor {
    // pre 1.20.4: List<Recipe<?>> callGetResults();
    @Invoker
    List<RecipeEntry<?>> callGetResults();

    @Accessor
    RecipeResultCollection getResultCollection();

    @Accessor
    int getCurrentResultIndex();
}
