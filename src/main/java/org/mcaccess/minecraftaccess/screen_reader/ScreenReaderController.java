package org.mcaccess.minecraftaccess.screen_reader;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

@Slf4j
public final class ScreenReaderController {
    private ScreenReaderController() {
    }

    public static @Nullable ScreenReaderInterface getAvailable() {
        switch (Util.getPlatform()) {
            case WINDOWS -> {
                ScreenReaderWindows screenReaderWindows = new ScreenReaderWindows();
                screenReaderWindows.initializeScreenReader();
                return screenReaderWindows;
            }
            case OSX -> {
                ScreenReaderMacOS screenReaderMacOS = new ScreenReaderMacOS();
                screenReaderMacOS.initializeScreenReader();
                return screenReaderMacOS;
            }
            case LINUX -> {
                ScreenReaderLinux screenReaderLinux = new ScreenReaderLinux();
                screenReaderLinux.initializeScreenReader();
                return screenReaderLinux;
            }
            default -> {
                log.error("No valid ScreenReader interface found");
                return null;
            }
        }
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
        new Translation("minecraft_access.access_menu.screen_reader_refreshed").narrate(true);
    }
}
