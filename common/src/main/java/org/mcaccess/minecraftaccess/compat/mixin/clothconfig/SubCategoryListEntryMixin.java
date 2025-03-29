package org.mcaccess.minecraftaccess.compat.mixin.clothconfig;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.gui.entries.SubCategoryListEntry;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import me.shedaniel.math.Rectangle;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import org.mcaccess.minecraftaccess.utils.ui.NavigationUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@SuppressWarnings("rawtypes")
@Mixin(value = SubCategoryListEntry.class, remap = false)
abstract class SubCategoryListEntryMixin extends TooltipListEntry<List<AbstractConfigListEntry>> {
    @SuppressWarnings({"deprecation", "UnstableApiUsage"})
    SubCategoryListEntryMixin(Component fieldName, @Nullable Supplier<Optional<Component[]>> tooltipSupplier) {
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
        if (!isFocused() && isDisplayed()) {
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

    @Override
    public @NotNull NarratableEntry.NarrationPriority narrationPriority() {
        return this.getParent().isFocused() && isFocused() ? NarrationPriority.FOCUSED : NarrationPriority.NONE;
    }

    @Override
    public boolean isFocused() {
        return this.getParent().getFocused() == this;
    }

    @Mixin(value = SubCategoryListEntry.CategoryLabelWidget.class, remap = false)
    abstract static class CategoryLabelWidgetMixin implements GuiEventListener, NarratableEntry {
        @Shadow
        public abstract boolean mouseClicked(double mouseX, double mouseY, int int_1);

        @Shadow
        @Final
        private Rectangle rectangle;

        @Shadow
        @Final
        SubCategoryListEntry this$0;

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

        @WrapOperation(
                method = "updateNarration",
                at = @At(value = "INVOKE",
                        target = "Lme/shedaniel/clothconfig2/gui/entries/SubCategoryListEntry;getFieldName()Lnet/minecraft/network/chat/Component;"),
                remap = true
        )
        public Component updateNarration(SubCategoryListEntry instance, Operation<Component> original) {
            String translationKey = this$0.isExpanded() ? "minecraft_access.gui.subcategory_expanded" : "minecraft_access.gui.subcategory_unexpanded";
            return Component.translatable(translationKey, this$0.getFieldName());
        }
    }
}
