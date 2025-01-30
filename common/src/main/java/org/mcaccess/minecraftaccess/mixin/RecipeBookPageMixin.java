package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.screens.recipebook.RecipeBookPage;
import net.minecraft.client.gui.screens.recipebook.RecipeCollection;
import org.mcaccess.minecraftaccess.features.inventory_controls.GroupGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(RecipeBookPage.class)
public abstract class RecipeBookPageMixin {
    @Inject(at = @At("HEAD"), method = "updateCollections")
    private void saveResultsForRecipeGroupGenerating(List<RecipeCollection> resultCollections, boolean resetCurrentPage, boolean filteringCraftable, CallbackInfo ci) {
        GroupGenerator.recipesOnTheScreen = resultCollections;
    }
}
