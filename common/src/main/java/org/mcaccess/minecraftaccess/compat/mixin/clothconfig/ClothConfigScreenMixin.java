package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import me.shedaniel.clothconfig2.gui.AbstractTabbedConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.mixin.ScreenAccessor;
import org.mcaccess.minecraftaccess.utils.ui.NavigationUtils;
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
    @Shadow
    @Final
    private List<ClothConfigTabButton> tabButtons;

    @Shadow
    public abstract Component getSelectedCategory();

    @Shadow
    private AbstractWidget buttonRightTab;

    @Shadow
    private AbstractWidget buttonLeftTab;

    ClothConfigScreenMixin(Screen parent, Component title, ResourceLocation backgroundLocation) {
        super(parent, title, backgroundLocation);
    }

    @Inject(at = @At("TAIL"), method = "init")
    void addComponentsAsNarratables(CallbackInfo ci) {
        // so that child components can be narrated in Screen.addElementNarrations()
        List<NarratableEntry> narratables = ((ScreenAccessor) this).getNarratables();
        narratables.addAll(this.tabButtons);
    }

    /**
     * Override inherited {@link Screen#setInitialFocus()}'s logic to not automatically focus on next component after reinitialize.
     * Due to unexpected behavior combination, pressing Enter key on {@link ClothConfigTabButton} always results in focusing on cancel button, which is different from mouse clicking on {@link ClothConfigTabButton} (which clean focus to null).
     * ({@link ClothConfigTabButton#onPress()} triggers {@link ClothConfigScreen#init()}, which triggers {@link Screen#setInitialFocus()})
     */
    @Override
    protected void setInitialFocus() {
    }

    @Override
    public @NotNull Component getNarrationMessage() {
        return super.getNarrationMessage().copy().append(I18n.get("minecraft_access.other.words_connection")).append(getSelectedCategory());
    }

    /**
     * Inspired by {@link net.minecraft.client.gui.components.tabs.TabNavigationBar#keyPressed(int, int, int)}.
     * Use Control + Tab (and Control + Shift + Tab) to switch between tab buttons.
     */
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Screen.hasControlDown() && keyCode == GLFW.GLFW_KEY_TAB) {
            mca$switchCategory(!Screen.hasShiftDown());
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Unique
    private void mca$switchCategory(boolean forward) {
        int nextIndex = this.selectedCategoryIndex + (forward ? 1 : -1);
        if (Math.clamp(nextIndex, 0, this.tabButtons.size() - 1) != nextIndex) {
            MainClass.speakWithNarrator(I18n.get("minecraft_access.other.reached_the_border"), true);
            return;
        }

        ClothConfigTabButton tabButton = this.tabButtons.get(nextIndex);

        if (tabButton.isMouseOver(tabButton.getX() + 1, tabButton.getY() + 1)) {
            // the tab button is visible, click it
            tabButton.mouseClicked(tabButton.getX() + 1, tabButton.getY() + 1, 0);
        } else {
            // the tab button is invisible, scroll tab menu
            var arrowButton = forward ? this.buttonRightTab : this.buttonLeftTab;
            arrowButton.mouseClicked(arrowButton.getX() + 1, arrowButton.getY() + 1, 0);
            // but the scroll needs ticking to be finished, so directly trigger the tab button by calling onPress
            tabButton.onPress();
        }
    }

    @SuppressWarnings("rawtypes")
    @Mixin(value = ClothConfigScreen.ListWidget.class, remap = false)
    abstract static class ListWidgetMixin extends DynamicElementListWidget {
        @Shadow
        public abstract @NotNull List<GuiEventListener> children();

        @Shadow
        @Final
        private AbstractConfigScreen screen;

        public ListWidgetMixin(Minecraft client, int width, int height, int top, int bottom, ResourceLocation backgroundLocation) {
            super(client, width, height, top, bottom, backgroundLocation);
        }

        /**
         * Focus on the last option if navigating backward from cancel button
         */
        @Override
        public ComponentPath nextFocusPath(FocusNavigationEvent event) {
            List<GuiEventListener> children = children();
            boolean isNotFocused = this.screen.getFocused() != this;
            if (isNotFocused && !children.isEmpty() && NavigationUtils.isDirectionBackward(event)) {
                this.setFocused(null);
                return ComponentPath.path(this, NavigationUtils.getFocusPathStartFrom(children.getLast(), event));
            }
            return super.nextFocusPath(event);
        }
    }
}
