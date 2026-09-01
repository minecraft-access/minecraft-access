package org.mcaccess.minecraftaccess.mixin;

import java.util.List;

import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.mcaccess.minecraftaccess.features.inventory_controls.GroupGenerator;

@Mixin(RecipeBookPage.class)
abstract class RecipeBookPageMixin {
    @Inject(method = "updateCollections", at = @At("HEAD"))
    private void saveResultsForRecipeGroupGenerating(List<RecipeCollection> recipeCollections, boolean resetPage, boolean isFiltering, CallbackInfo ci) {
        GroupGenerator.recipesOnTheScreen = recipeCollections;
    }
}
