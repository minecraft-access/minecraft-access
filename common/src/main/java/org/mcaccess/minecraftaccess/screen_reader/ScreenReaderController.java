package org.mcaccess.minecraftaccess.screen_reader;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.system.OsUtils;

@Slf4j
public class ScreenReaderController {
    public static ScreenReaderInterface getAvailable() {
        if (OsUtils.isLinux()) {
            ScreenReaderLinux screenReaderLinux = new ScreenReaderLinux();
            screenReaderLinux.initializeScreenReader();
            return screenReaderLinux;
        }

        if (OsUtils.isMacOS()) {
            ScreenReaderMacOS screenReaderMacOS = new ScreenReaderMacOS();
            screenReaderMacOS.initializeScreenReader();
            return screenReaderMacOS;
        }

        if (OsUtils.isWindows()) {
            ScreenReaderWindows screenReaderWindows = new ScreenReaderWindows();
            screenReaderWindows.initializeScreenReader();
            return screenReaderWindows;
        }

        return null;
    }

    public static void refreshScreenReader() {
        refreshScreenReader(false);
    }

    public static void refreshScreenReader(boolean closeOpenedScreen) {
        log.info("Refreshing screen reader");
        try {
            MainClass.setScreenReader(getAvailable());

            if (!closeOpenedScreen) return;
            if (Minecraft.getInstance() == null) return;
            if (Minecraft.getInstance().player == null) return;
            Minecraft.getInstance().player.clientSideCloseContainer();
            MainClass.speakWithNarrator(I18n.get("minecraft_access.access_menu.screen_reader_refreshed"), true);
        } catch (Exception e) {
            log.error("An error while refreshing screen reader", e);
        }
    }
}
