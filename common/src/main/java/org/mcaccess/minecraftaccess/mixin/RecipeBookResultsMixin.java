package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeResultCollection;
import org.mcaccess.minecraftaccess.features.inventory_controls.GroupGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeBookResults.class)
public abstract class RecipeBookResultsMixin {
    @Inject(at = @At("HEAD"), method = "setResults")
    private void saveResultsForRecipeGroupGenerating(List<RecipeResultCollection> resultCollections, boolean resetCurrentPage, boolean filteringCraftable, CallbackInfo ci) {
        GroupGenerator.recipesOnTheScreen = resultCollections;
    }
}
