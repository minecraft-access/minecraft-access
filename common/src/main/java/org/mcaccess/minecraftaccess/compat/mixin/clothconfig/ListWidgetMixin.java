package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import java.util.List;

import me.shedaniel.clothconfig2.gui.AbstractConfigScreen;
import me.shedaniel.clothconfig2.gui.ClothConfigScreen;
import me.shedaniel.clothconfig2.gui.widget.DynamicElementListWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import org.mcaccess.minecraftaccess.utils.ui.NavigationUtils;

@SuppressWarnings("rawtypes")
@Mixin(value = ClothConfigScreen.ListWidget.class, remap = false)
abstract class ListWidgetMixin extends DynamicElementListWidget {
    @Shadow
    @Final
    private AbstractConfigScreen screen;

    protected ListWidgetMixin(Minecraft client, int width, int height, int top, int bottom, ResourceLocation backgroundLocation) {
        super(client, width, height, top, bottom, backgroundLocation);
    }

    @Override
    @Shadow
    public abstract @NotNull List<GuiEventListener> children();

    /**
     * Focus on the last option if navigating backward from cancel button
     */
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        List<GuiEventListener> children = children();
        if (!isFocused() && !children.isEmpty() && NavigationUtils.isDirectionBackward(event)) {
            setFocused(null);
            return ComponentPath.path(this, NavigationUtils.getFocusPathStartFrom(children.getLast(), event));
        }
        return super.nextFocusPath(event);
    }

    @Override
    public boolean isFocused() {
        return screen.getFocused() == this;
    }
}
