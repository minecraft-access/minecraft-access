package org.mcaccess.minecraftaccess.utils.ui;

import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenDirection;

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
}
