package com.github.khanshoaib3.minecraft_access.mixin;

import net.minecraft.client.gui.screen.ingame.RecipeBookScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(RecipeBookScreen.class)
public interface RecipeBookScreenAccessor {
    @Accessor
    RecipeBookWidget<?> getRecipeBook();
}
