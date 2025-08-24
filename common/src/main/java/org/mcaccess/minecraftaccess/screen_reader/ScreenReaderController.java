package org.mcaccess.minecraftaccess.screen_reader;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.MainClass;

@Slf4j
public final class ScreenReaderController {
    private ScreenReaderController() {
    }

    public static @Nullable ScreenReaderInterface getAvailable() {
        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.startsWith("windows")) {
            ScreenReaderWindows screenReaderWindows = new ScreenReaderWindows();
            screenReaderWindows.initializeScreenReader();
            return screenReaderWindows;
        }

        if (osName.startsWith("mac")) {
            ScreenReaderMacOS screenReaderMacOS = new ScreenReaderMacOS();
            screenReaderMacOS.initializeScreenReader();
            return screenReaderMacOS;
        }

        if (osName.startsWith("linux")) {
            ScreenReaderLinux screenReaderLinux = new ScreenReaderLinux();
            screenReaderLinux.initializeScreenReader();
            return screenReaderLinux;
        }

        log.error("No valid ScreenReader interface found");
        return null;
    }

    public static void refreshScreenReader() {
        refreshScreenReader(false);
    }

    public static void refreshScreenReader(boolean closeOpenedScreen) {
        log.info("Refreshing screen reader");
        MainClass.setScreenReader(getAvailable());

        if (!closeOpenedScreen) return;
        if (Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.clientSideCloseContainer();
        MainClass.narrate(I18n.get("minecraft_access.access_menu.screen_reader_refreshed"), true);
    }
}
