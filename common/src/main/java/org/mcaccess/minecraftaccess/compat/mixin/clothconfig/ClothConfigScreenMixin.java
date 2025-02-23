package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import me.shedaniel.clothconfig2.gui.AbstractTabbedConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigTabButton;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.mcaccess.minecraftaccess.utils.ui.NavigationUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

/**
 * remap=false: suppress warnings since cloth isn't part of original game
 */
@Mixin(value = ClothConfigScreen.class, remap = false)
abstract class ClothConfigScreenMixin extends AbstractTabbedConfigScreen {
    ClothConfigScreenMixin(Screen parent, Component title, ResourceLocation backgroundLocation) {
        super(parent, title, backgroundLocation);
    }

    /**
     * Override inherited {@link Screen#setInitialFocus()}'s logic to not automatically focus on next component after reinitialize.
     * Due to unexpected behavior combination, pressing Enter key on {@link ClothConfigTabButton} always results in focusing on cancel button, which is different from mouse clicking on {@link ClothConfigTabButton} (which clean focus to null).
     * ({@link ClothConfigTabButton#onPress()} triggers {@link ClothConfigScreen#init()}, which triggers {@link Screen#setInitialFocus()})
     */
    @Override
    protected void setInitialFocus() {
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
