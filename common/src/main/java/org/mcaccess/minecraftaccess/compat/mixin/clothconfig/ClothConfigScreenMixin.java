package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.AbstractTabbedConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import me.shedaniel.clothconfig2.gui.entries.EmptyEntry;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
    public abstract Text getSelectedCategory();

    @Shadow
    public ClothConfigScreen.ListWidget<? extends Element> listWidget;

    @Override
    public Text getNarratedTitle() {
        return super.getNarratedTitle().copy().append(". ").append(getSelectedCategory());
    }

    @Inject(at = @At("TAIL"), method = "init")
    void init(CallbackInfo ci) {
        // There are two space entries added into listWidget in ClothConfigScreen.init()
        // remove them since they'll affect the element selection
        listWidget.children().removeIf(e -> e instanceof EmptyEntry);
    }

    /**
     * Make the selected category tab button focused
     */
    @Override
    public void setInitialFocus() {
        this.setInitialFocus(tabButtons.get(this.selectedCategoryIndex));
    }

    /**
     * Inspired by {@link net.minecraft.client.gui.widget.TabNavigationWidget#trySwitchTabsWithKey(int)}.
     * Use Control + Tab (and Control + Shift + Tab) to switch between tab buttons.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_TAB) {
            mca$loopSelectCategory(!Screen.hasShiftDown());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Unique
    private void mca$loopSelectCategory(boolean forward) {
        int nextIndex = Math.floorMod(this.selectedCategoryIndex + (forward ? 1 : -1), this.tabButtons.size());
        ClothConfigTabButton tabButton = this.tabButtons.get(nextIndex);
        this.setFocused(tabButton);
        tabButton.onPress();
    }
}
