package org.mcaccess.minecraftaccess.screen_reader;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.screen_reader.android.AndroidPlatform;
import org.mcaccess.minecraftaccess.screen_reader.android.AndroidTtsNative;

/**
 * Narration through the text to speech engine of Android itself.
 *
 * <p>This is used when Minecraft runs inside an Android launcher, where the
 * system engine is the only speech output that works without shipping a speech
 * synthesiser. The rate and pitch sliders of the speech settings are applied
 * here, so the voice can be tuned from inside the game.
 */
@Slf4j
public class ScreenReaderAndroid implements ScreenReaderInterface {
    private boolean initialized;

    @Override
    public void initializeScreenReader() {
        if (!AndroidPlatform.isAndroid()) {
            log.error("The Android screen reader interface was requested outside an Android environment");
            return;
        }

        String language = languageTag();
        log.info("Initializing the Android text to speech engine, language={}", language);
        initialized = AndroidTtsNative.start(language);
        if (!initialized) {
            log.error("Unable to initialize the Android text to speech engine: {}", AndroidTtsNative.error());
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
            log.warn("Unable to narrate using the Android text to speech engine: {}", AndroidTtsNative.error());
        } else {
            log.debug("Narrating(interrupt:{}) = {}", interrupt, narration);
        }
    }

    @Override
    public void closeScreenReader() {
        if (!initialized) return;

        AndroidTtsNative.shutdown();
        initialized = false;
        log.info("Closed the Android text to speech engine");
    }

    /**
     * Reads the language the game is running in and turns it into a BCP-47 tag,
     * for instance {@code zh-CN}, so that the system engine picks a matching voice.
     */
    private @Nullable String languageTag() {
        Minecraft client = Minecraft.getInstance();
        if (client == null) return null;

        String selected = client.getLanguageManager().getSelected();
        if (selected == null || selected.isBlank()) return null;
        return selected.replace('_', '-');
    }
}
