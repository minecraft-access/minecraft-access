package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import org.mcaccess.minecraftaccess.utils.ui.NavigationUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
@Mixin(value = SubCategoryListEntry.class, remap = false)
abstract class SubCategoryListEntryMixin extends TooltipListEntry<List<AbstractConfigListEntry>> {
    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    public SubCategoryListEntryMixin(Component fieldName, @Nullable Supplier<Optional<Component[]>> tooltipSupplier) {
        super(fieldName, tooltipSupplier);
    }

    @Shadow
    public abstract boolean isExpanded();

    @Shadow
    @Final
    private SubCategoryListEntry.CategoryLabelWidget widget;

    @Shadow
    public abstract List<AbstractConfigListEntry> filteredEntries();

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent event) {
        // The condition below can't be replaced with "this.isFocused()".
        // Once the subcategory is navigated through and remain unexpanded, the subcategory can't be focused again.
        // Because "subcategory.isFocused()" always returns false, then run the "super.nextFocusPath(event)", and it always returns null.
        // So we use another way to check if current subcategory is focused instead to avoid this problem.
        boolean isFocusedByParent = this.getParent().getFocused() == this;

        if (!isFocusedByParent && isDisplayed()) {
            if (isExpanded()) {
                List<? extends GuiEventListener> children = this.filteredEntries();
                GuiEventListener target = NavigationUtils.isDirectionBackward(event) ? children.getLast() : children.getFirst();
                return ComponentPath.path(this, NavigationUtils.getFocusPathStartFrom(target, event));
            } else {
                return ComponentPath.path(this, ComponentPath.leaf(this.widget));
            }
        } else {
            return super.nextFocusPath(event);
        }
    }

    @Mixin(value = SubCategoryListEntry.CategoryLabelWidget.class, remap = false)
    abstract static class CategoryLabelWidgetMixin implements GuiEventListener, NarratableEntry {
        @Shadow
        public abstract boolean mouseClicked(double mouseX, double mouseY, int int_1);

        @Shadow
        @Final
        private Rectangle rectangle;

        /**
         * Make the label widget expandable through keyboard.
         * Although this widget is treated as one of {@link SubCategoryListEntry#children()},
         * it's this very widget's job to handle mouse operation.
         */
        @Override
        public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (KeyUtils.isSpaceOrEnterPressed()) {
                this.mouseClicked(this.rectangle.x + 1, this.rectangle.y + 1, 0);
                return true;
            }
            return false;
        }
    }
}
