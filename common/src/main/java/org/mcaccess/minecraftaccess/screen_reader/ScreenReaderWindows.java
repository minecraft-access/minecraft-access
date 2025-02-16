package org.mcaccess.minecraftaccess.screen_reader;

import com.davykager.tolk.Tolk;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ScreenReaderWindows implements ScreenReaderInterface {
    @Override
    public void initializeScreenReader() {
        Path path = Paths.get("Tolk.dll");
        if (!Files.exists(path)) {
            log.error("Tolk not installed!");
            return;
        }

        log.info("Initializing Tolk for windows at: {}", path);
        Tolk.trySAPI(true);
        Tolk.load();
        if (Tolk.isLoaded()) {
            log.info("Successfully initialized Tolk speech system with the {} driver.", Tolk.detectScreenReader());
        } else {
            log.error("Unable to initialize Tolk");
        }
    }

    @Override
    public boolean isInitialized() {
        return Tolk.isLoaded();
    }

    @Override
    public void say(String text, boolean interrupt) {
        boolean wasOutputted = Tolk.output(text, interrupt);
        if (wasOutputted)
            log.info("Speaking(interrupt:" + interrupt + ")= " + text);
        else
            log.error("Unable to output the message sent to Tolk");
    }

    @Override
    public void closeScreenReader() {
        Tolk.unload();
    }
}
