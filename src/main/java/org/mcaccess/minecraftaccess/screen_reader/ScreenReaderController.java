package org.mcaccess.minecraftaccess.screen_reader;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.util.Util;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.features.AutoLibrarySetup;

@Slf4j
public final class ScreenReaderController {
    private ScreenReaderController() {
    }

    public static @Nullable ScreenReaderInterface getAvailable() {
        switch (Util.getPlatform()) {
            case Util.OS os when os == Util.OS.WINDOWS && AutoLibrarySetup.isAmd64() -> {
                ScreenReaderWindows screenReaderWindows = new ScreenReaderWindows();
                screenReaderWindows.initializeScreenReader();
                return screenReaderWindows;
            }
            case OSX -> {
                ScreenReaderMacOS screenReaderMacOS = new ScreenReaderMacOS();
                screenReaderMacOS.initializeScreenReader();
                return screenReaderMacOS;
            }
            case Util.OS os when os == Util.OS.LINUX && AutoLibrarySetup.isAmd64() -> {
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
        MainClass.narrate(I18n.get("minecraft_access.access_menu.screen_reader_refreshed"), true);
    }
}
