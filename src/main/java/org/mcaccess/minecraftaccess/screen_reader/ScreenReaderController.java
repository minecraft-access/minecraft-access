package org.mcaccess.minecraftaccess.screen_reader;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.whisprs.Whisprs;
import org.mcaccess.whisprs.error.InitializeFailedException;
import org.mcaccess.whisprs.error.NoBackendsException;

@Slf4j
public final class ScreenReaderController {
    @Nullable
    private static Whisprs whisprs = null;

    private ScreenReaderController() {
    }

    public static synchronized void initialize() {
        if (whisprs != null) {
            return;
        }
        try {
            whisprs = new Whisprs();
            log.info("Initialized whisp-rs");
        } catch (InitializeFailedException | NoBackendsException | UnsupportedOperationException e) {
            log.error("Failed to initialize whisp-rs", e);
            whisprs = null;
        }
    }

    public static boolean isInitialized() {
        return whisprs != null;
    }

    public static synchronized void narrate(String text, boolean interrupt) {
        if (whisprs == null) {
            return;
        }
        String narration = formatNarration(text);
        Config.SpeechSettings s = Config.getInstance().speechSettings;
        whisprs.output(null, null, null, s.rate, s.volume, s.pitch, null, narration, interrupt);
        log.info("Narrating(interrupt:{})= {}", interrupt, narration);
    }

    public static synchronized void close() {
        if (whisprs == null) {
            return;
        }
        log.info("Closing whisp-rs");
        whisprs.close();
        whisprs = null;
    }

    public static void refreshScreenReader() {
        refreshScreenReader(false);
    }

    public static synchronized void refreshScreenReader(boolean closeOpenedScreen) {
        log.info("Refreshing screen reader");
        close();
        initialize();

        if (!closeOpenedScreen) return;
        if (Minecraft.getInstance().player == null) return;
        Minecraft.getInstance().player.clientSideCloseContainer();
        MainClass.narrate(I18n.get("minecraft_access.access_menu.screen_reader_refreshed"), true);
    }

    private static String formatNarration(String text) {
        // Remove formatting codes
        // ref: https://minecraft.wiki/w/Formatting_codes
        return text.replaceAll("§.", "");
    }
}
