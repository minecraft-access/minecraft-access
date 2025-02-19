package org.mcaccess.minecraftaccess.utils.ui;

import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;
import org.jetbrains.annotations.Nullable;

public class NavigationUtils {
    /**
     * Only for vertical navigation
     *
     * @return true if Tab backward or Arrow up, otherwise false
     */
    public static boolean isDirectionBackward(FocusNavigationEvent event) {
        if (event instanceof FocusNavigationEvent.ArrowNavigation arrow) {
            return arrow.direction().equals(ScreenDirection.UP);
        } else if (event instanceof FocusNavigationEvent.TabNavigation tab) {
            return !tab.forward();
        }
        return false;
    }

    /**
     * Recursively find the focus path.
     * Ignoring focusable children and return early with an incomplete path may cause unexpected navigation behaviour.
     *
     * @param root  from input component
     * @param event original event
     * @return to deepest focusable child
     */
    public static @Nullable ComponentPath getFocusPathStartFrom(GuiEventListener root, FocusNavigationEvent event) {
        // thankfully, the original method already been recursive style
        return root instanceof ContainerEventHandler container ? container.nextFocusPath(event) : ComponentPath.leaf(root);
    }
}
