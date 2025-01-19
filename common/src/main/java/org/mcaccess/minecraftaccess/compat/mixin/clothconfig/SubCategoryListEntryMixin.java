package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.navigation.GuiNavigation;
import net.minecraft.client.gui.navigation.GuiNavigationPath;
import net.minecraft.text.Text;
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
    public SubCategoryListEntryMixin(Text fieldName, @Nullable Supplier<Optional<Text[]>> tooltipSupplier) {
        super(fieldName, tooltipSupplier);
    }

    /**
     * Make {@link SubCategoryListEntry.CategoryLabelWidget} a navigable {@link Element}
     * and can be clicked by space or enter.
     * Note that {@link SubCategoryListEntry} has already been {@link ParentElement} in its original implementation.
     */
    @Mixin(value = SubCategoryListEntry.CategoryLabelWidget.class, remap = false)
    abstract static class CategoryLabelWidgetMixin implements Element {
        @Shadow
        public abstract boolean mouseClicked(double mouseX, double mouseY, int int_1);

        @Shadow
        @Final
        private Rectangle rectangle;

        @Override
        public GuiNavigationPath getNavigationPath(GuiNavigation navigation) {
            return this.isFocused() ? null : GuiNavigationPath.of(this);
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
