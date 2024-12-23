package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.screen.recipebook.RecipeBookResults;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.gui.screen.recipebook.RecipeGroupButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

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
    ToggleButtonWidget getToggleCraftableButton();

    @Invoker
    Text callGetToggleCraftableButtonText();
}
