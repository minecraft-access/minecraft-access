package org.mcaccess.minecraftaccess.screen_reader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.davykager.tolk.Tolk;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScreenReaderWindows implements ScreenReaderInterface {
    @Override
    public void initializeScreenReader() {
        Path path = Paths.get("Tolk.dll").toAbsolutePath();

        if (!Files.exists(path)) {
            log.error("Tolk not installed!");
            return;
        }

        log.info("Initializing Tolk for windows at: {}", path);

       System.load(path.toString());
        Tolk.trySAPI(true);
        Tolk.load();

        if (isInitialized()) {
            log.info("Successfully initialized screen reader Tolk with the %s driver".formatted(Tolk.detectScreenReader()));
        } else {
            log.error("Unable to initialize screen reader interface Tolk!");
        }
    }

    @Override
    public boolean isInitialized() {
        return Tolk.isLoaded();
    }

    @Override
    public void narrate(String text, boolean interrupt) {
        if (!isInitialized()) {
            return;
        }

        String narration = formatNarration(text);

        if (Tolk.output(narration, interrupt)) {
            log.info("Narrating(interrupt:{})= {}", interrupt, narration);
        } else {
            log.error("Unable to narrate using Tolk");
        }
    }

    @Override
    public void closeScreenReader() {
        if (!isInitialized()) {
            return;
        }

        Tolk.unload();
    }
}
