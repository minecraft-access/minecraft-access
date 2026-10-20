package org.mcaccess.minecraftaccess.screen_reader;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.screen_reader.android.AndroidPlatform;
import org.mcaccess.minecraftaccess.screen_reader.android.AndroidTtsNative;

/**
 * Narration through the accessibility APIs of Android itself.
 *
 * <p>This is used when Minecraft runs inside an Android launcher. A screen
 * reader that is already running speaks the narration, and otherwise the text
 * to speech engine of the system does; either way the mod ships no speech
 * synthesiser of its own. The rate and pitch sliders of the speech settings
 * only reach the text to speech engine, a screen reader keeps its own values.
 */
@Slf4j
public class ScreenReaderAndroid implements ScreenReaderInterface {
    private boolean initialized;

    @Override
    @SuppressWarnings("checkstyle:IllegalCatch")
    public void initializeScreenReader() {
        if (!AndroidPlatform.isAndroid()) {
            log.error("The Android screen reader interface was requested outside an Android environment");
            return;
        }

        String language = languageTag();
        log.info("Initializing Android narration, language={}", language);
        try {
            initialized = AndroidTtsNative.start(language);
        } catch (RuntimeException e) {
            // Narration is an accessibility feature and this runs from the mod initialiser,
            // where an exception would abort the entrypoint and crash the game
            log.error("Unable to initialize Android narration", e);
            initialized = false;
        }
        if (!initialized) {
            log.error("Unable to initialize Android narration: {}", AndroidTtsNative.error());
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void narrate(String text, boolean interrupt) {
        if (!initialized) return;

        String narration = formatNarration(text);
        if (narration.isBlank()) return;

        Config.SpeechSettings settings = Config.getInstance().speechSettings;
        float rate = settings.androidSpeechRate / 100f;
        float pitch = settings.androidSpeechPitch / 100f;
        if (!AndroidTtsNative.narrate(narration, rate, pitch, interrupt)) {
            log.warn("Unable to narrate through Android: {}", AndroidTtsNative.error());
        } else {
            log.debug("Narrating(interrupt:{}) = {}", interrupt, narration);
        }
    }

    @Override
    public void closeScreenReader() {
        if (!initialized) return;

        AndroidTtsNative.shutdown();
        initialized = false;
        log.info("Closed Android narration");
    }

    /**
     * Reads the language the game is running in and turns it into a BCP-47 tag,
     * for instance {@code zh-CN}, so that the system engine picks a matching voice.
     */
    private @Nullable String languageTag() {
        Minecraft client = Minecraft.getInstance();
        if (client == null) return null;

        // The screen reader is refreshed from the mod initialiser, before the client has built
        // its language manager, and the system default is a fine fallback in that case
        LanguageManager manager = client.getLanguageManager();
        if (manager == null) return null;
        String selected = manager.getSelected();
        if (selected == null || selected.isBlank()) return null;
        return selected.replace('_', '-');
    }
}
