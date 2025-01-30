package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.AbstractTabbedConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import me.shedaniel.clothconfig2.gui.entries.EmptyEntry;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.mixin.McaScreenAccessor;
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
    ClothConfigScreenMixin(Screen parent, Component title, ResourceLocation backgroundLocation) {
        super(parent, title, backgroundLocation);
    }

    @Final
    @Shadow
    private List<ClothConfigTabButton> tabButtons;

    @Shadow
    public abstract Component getSelectedCategory();

    @Shadow
    public ClothConfigScreen.ListWidget<? extends GuiEventListener> listWidget;

    @Override
    public @NotNull Component getNarrationMessage() {
        return super.getNarrationMessage().copy().append(". ").append(getSelectedCategory());
    }

    @Inject(at = @At("TAIL"), method = "init")
    void init(CallbackInfo ci) {
        // There are two space entries added into listWidget in ClothConfigScreen.init()
        // remove them since they'll affect the element selection
        this.listWidget.children().removeIf(e -> e instanceof EmptyEntry);
        // Add current category's options as selectable of screen,
        // so that they can be narrated in Screen.addElementNarrations()
        List<NarratableEntry> narratables = ((McaScreenAccessor) this).getNarratables();
        narratables.addAll(this.listWidget.children());
    }

    /**
     * Make the selected category tab button focused
     */
    @Override
    public void setInitialFocus() {
        this.setInitialFocus(tabButtons.get(this.selectedCategoryIndex));
    }

    /**
     * Inspired by {@link net.minecraft.client.gui.components.tabs.TabNavigationBar#keyPressed(int, int, int)}.
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
