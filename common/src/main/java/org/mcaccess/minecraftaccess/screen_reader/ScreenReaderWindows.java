package com.github.khanshoaib3.minecraft_access.screen_reader;

import com.sun.jna.Library;
import com.sun.jna.Native;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ScreenReaderWindows implements ScreenReaderInterface {
    tolkInterface mainInstance = null;

    @Override
    public void initializeScreenReader() {
        Path path = Paths.get("Tolk.dll");
        if (!Files.exists(path)) {
            log.error("Tolk not installed!");
            return;
        }

        log.info("Initializing Tolk for windows at: " + path);
        tolkInterface instance = Native.load(path.toString(), tolkInterface.class);
        instance.Tolk_TrySAPI(true);
        instance.Tolk_Load();
        boolean isLoaded = instance.Tolk_IsLoaded() && instance.Tolk_HasSpeech();
        if (isLoaded) {
            log.info("Successfully initialized screen reader");
            mainInstance = instance;
        } else {
            log.error("Unable to initialize screen reader");
        }
    }

    @Override
    public boolean isInitialized() {
        return mainInstance != null;
    }

    @Override
    public void say(String text, boolean interrupt) {
        if (mainInstance == null)
            return;

        char[] ch = new char[text.length() + 1]; // Last character must be null so NVDA decodes the text correctly
        for (int i = 0; i < text.length(); i++) {
            ch[i] = text.charAt(i);
        }
        boolean re = mainInstance.Tolk_Output(ch, interrupt);
        if (re)
            log.info("Speaking(interrupt:" + interrupt + ")= " + text);
        else
            log.error("Unable to speak");
    }

    @Override
    public void closeScreenReader() {
        if (mainInstance == null)
            return;

        mainInstance.Tolk_Unload();
    }

    // https://github.com/ndarilek/tolk
    // Header file for reference = https://github.com/ndarilek/tolk/blob/master/src/Tolk.h
    private interface tolkInterface extends Library {
        /**
         * Initializes Tolk by loading and initializing the screen reader drivers and setting the current screen reader driver, provided at least one of the supported screen readers is active. Also initializes COM if it has not already been initialized on the calling thread. Calling this function more than once will only initialize COM. You should call this function before using the functions below. Use Tolk_IsLoaded to determine if Tolk has been initialized.
         */
        void Tolk_Load();

        /**
         * Tests if Tolk has been initialized.
         *
         * @return true if Tolk has been initialized, false otherwise.
         */
        boolean Tolk_IsLoaded();

        /**
         * Tests if the current screen reader driver supports speech output, if one is set. If none is set, tries to detect the currently active screen reader before testing for speech support. You should call Tolk_Load once before using this function.
         *
         * @return true if the current screen reader driver supports speech, false otherwise.
         */
        boolean Tolk_HasSpeech();

        /**
         * Finalizes Tolk by finalizing and unloading the screen reader drivers and clearing the current screen reader driver, provided one was set. Also uninitializes COM on the calling thread. Calling this function more than once will only uninitialize COM. You should not use the functions below if this function has been called.
         */
        void Tolk_Unload();

        /**
         * Name:         Tolk_TrySAPI
         * Description:  Sets if Microsoft Speech API (SAPI) should be used in the screen reader auto-detection process. The default is not to include SAPI. The SAPI driver will use the system default synthesizer, voice and soundcard. This function triggers the screen reader detection process if needed. For best performance, you should call this function before calling Tolk_Load.
         * Parameters:   trySAPI: whether or not to include SAPI in auto-detection.
         * Returns:      None.
         */
        void Tolk_TrySAPI(boolean trySAPI);

        /**
         * Outputs text through the current screen reader driver, if one is set. If none is set or if it encountered an error, tries to detect the currently active screen reader before outputting the text. This is the preferred function to use for sending text to a screen reader, because it uses all of the supported output methods (speech and/or braille depending on the current screen reader driver). You should call Tolk_Load once before using this function. This function is asynchronous.
         *
         * @param text      text to output.
         * @param interrupt whether to first cancel any previous speech.
         * @return true on success, false otherwise.
         */
        boolean Tolk_Output(char[] text, boolean interrupt);
    }
}
