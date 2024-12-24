package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SingleStackRecipe;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SingleStackRecipe.class)
public interface SingleStackRecipeAccessor {
    @Accessor
    ItemStack getResult();
}
