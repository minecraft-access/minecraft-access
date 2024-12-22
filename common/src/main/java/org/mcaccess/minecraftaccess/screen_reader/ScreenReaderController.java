package org.mcaccess.minecraftaccess.screen_reader;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.system.OsUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;

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
            if (MinecraftClient.getInstance() == null) return;
            if (MinecraftClient.getInstance().player == null) return;
            MinecraftClient.getInstance().player.closeScreen();
            MainClass.speakWithNarrator("Screen reader refreshed", true);
        } catch (Exception e) {
            log.error("An error while refreshing screen reader", e);
        }
    }
}
