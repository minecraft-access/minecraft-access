package org.mcaccess.minecraftaccess.utils.ui;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

/**
 * An interface version of {@link ObjectSelectionList} for
 * adding navigation and narration support to third party GUI components.
 * This class is suitable for middle layer components that belong to a parent component like {@link Screen}
 * while also having their own children components.
 */
public interface ComponentSelectionList<E extends ComponentSelectionList.Entry> extends ContainerEventHandler, NarratableEntry {

    /**
     * @return 1. if no child, null;
     * 2. if not focused, currently selected child;
     * 3. if focused and tab event, null;
     * 4. if focused and arrow event, next child;
     */
    @Nullable
    @Override
    default ComponentPath nextFocusPath(FocusNavigationEvent event) {
        if (this.children().isEmpty()) {
            return null;
        } else if (this.isFocused() && event instanceof FocusNavigationEvent.ArrowNavigation arrowNavigation) {
            E entry = this.nextEntry(arrowNavigation.direction());
            if (entry != null) {
                return ComponentPath.path(this, ComponentPath.leaf(entry));
            } else {
                this.setSelected(null);
                return null;
            }
        } else if (!this.isFocused()) {
            E entry2 = this.getSelected();
            if (entry2 == null) {
                entry2 = this.nextEntry(event.getVerticalDirectionForInitialFocus());
            }

            return entry2 == null ? null : ComponentPath.path(this, ComponentPath.leaf(entry2));
        } else {
            return null;
        }
    }

    @Nullable
    default E nextEntry(ScreenDirection direction) {
        return nextEntry(direction, e -> true);
    }

    /**
     * Get next (basing on currently selected) qualified child component
     *
     * @param direction search direction
     * @param predicate match condition
     */
    @Nullable
    default E nextEntry(ScreenDirection direction, Predicate<E> predicate) {
        int step = switch (direction) {
            case RIGHT, LEFT -> 0;
            case UP -> -1;
            case DOWN -> 1;
        };
        if (!this.children().isEmpty() && step != 0) {
            E selected = this.getSelected();
            int startIndex;
            if (selected == null) {
                startIndex = step > 0 ? 0 : this.children().size() - 1;
            } else {
                startIndex = this.children().indexOf(selected) + step;
            }

            for (int k = startIndex; k >= 0 && k < this.children().size(); k += step) {
                @SuppressWarnings("unchecked")
                E entry = (E) this.children().get(k);
                if (predicate.test(entry)) {
                    return entry;
                }
            }
        }

        return null;
    }

    @Nullable
    E getSelected();

    void setSelected(@Nullable E selected);

    /**
     * Child component of {@link ComponentSelectionList}
     */
    interface Entry extends GuiEventListener, NarratableEntry {
    }
}
