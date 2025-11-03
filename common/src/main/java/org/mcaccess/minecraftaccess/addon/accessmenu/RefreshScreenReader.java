package org.mcaccess.minecraftaccess.addon.accessmenu;

import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderController;

public class RefreshScreenReader implements AccessMenuFunction {
    @Override
    public void execute() {
        ScreenReaderController.refreshScreenReader(true);
    }
}
