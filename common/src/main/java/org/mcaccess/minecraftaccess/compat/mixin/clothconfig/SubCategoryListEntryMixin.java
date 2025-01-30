package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
@Mixin(SubCategoryListEntry.class)
abstract class SubCategoryListEntryMixin extends TooltipListEntry<List<AbstractConfigListEntry>> {
    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public SubCategoryListEntryMixin(Component fieldName, @Nullable Supplier<Optional<Component[]>> tooltipSupplier) {
        super(fieldName, tooltipSupplier);
    }

    /**
     * Make {@link SubCategoryListEntry.CategoryLabelWidget} a navigable {@link net.minecraft.client.gui.components.events.GuiEventListener}
     * and can be clicked by space or enter.
     * Note that {@link SubCategoryListEntry} has already been {@link ContainerEventHandler} in its original implementation.
     */
    @Mixin(value = SubCategoryListEntry.CategoryLabelWidget.class, remap = false)
    abstract static class CategoryLabelWidgetMixin implements GuiEventListener {
        @Shadow
        public abstract boolean mouseClicked(double mouseX, double mouseY, int int_1);

        @Shadow
        @Final
        private Rectangle rectangle;

        @Override
        public ComponentPath nextFocusPath(FocusNavigationEvent navigation) {
            return this.isFocused() ? null : ComponentPath.path(this);
        }

        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER) {
                this.mouseClicked(this.rectangle.x + 1, this.rectangle.y + 1, 0);
                return true;
            }
            return false;
        }
    }
}
