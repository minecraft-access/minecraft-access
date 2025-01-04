package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.api.AbstractConfigEntry;
import me.shedaniel.clothconfig2.gui.AbstractTabbedConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/**
 * remap=false: suppress warnings since cloth isn't part of original game
 */
@Mixin(value = ClothConfigScreen.class, remap = false)
abstract class ClothConfigScreenMixin extends AbstractTabbedConfigScreen {
    ClothConfigScreenMixin(Screen parent, Text title, Identifier backgroundLocation) {
        super(parent, title, backgroundLocation);
    }

    @Final
    @Shadow
    private List<ClothConfigTabButton> tabButtons;
    @Shadow
    public ClothConfigScreen.ListWidget<AbstractConfigEntry<AbstractConfigEntry<?>>> listWidget;

    @Shadow
    public abstract Text getSelectedCategory();

    @Override
    public Text getNarratedTitle() {
        return super.getNarratedTitle().copy().append(". ").append(getSelectedCategory());
    }

    @Inject(at = @At("TAIL"), method = "init")
    void init(CallbackInfo ci) {
        tabButtons.forEach(this::addSelectableChild);
        listWidget.children().forEach(this::addSelectableChild);
    }

    /**
     * Make the first category tab button focused at the beginning,
     * so the next tab key pressing will switch to the second category tab,
     * instead of stepping into the first category.
     */
    @Override
    public void setInitialFocus() {
        this.setInitialFocus(tabButtons.getFirst());
    }
}
