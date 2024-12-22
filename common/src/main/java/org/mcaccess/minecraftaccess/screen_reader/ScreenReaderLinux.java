package com.github.khanshoaib3.minecraft_access.screen_reader;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ScreenReaderLinux implements ScreenReaderInterface {
    private libSpeechdWrapperInterface mainInstance = null;

    @Override
    public void initializeScreenReader() {
        Path path = Paths.get("libspeechdwrapper.so").toAbsolutePath();
        if(!Files.exists(path))
        {
            log.error("libspeechdwrapper not installed!");
            return;
        }

       log.info("Initializing libspeechdwrapper for linux at: " + path);
        libSpeechdWrapperInterface instance = Native.load(path.toString(), libSpeechdWrapperInterface.class);
        int re = instance.Initialize();

        // Try initializing again after 3 seconds (when no screen reader is running, the library is unable to connect with the speechd socket)
        if (re == -1) {
            try {
                log.error("Unable to initialize screen reader, trying again in 3 seconds.");
                TimeUnit.SECONDS.sleep(3);
                re = instance.Initialize();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (re == 1) {
            mainInstance = instance;
           log.info("Successfully initialized screen reader");
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

        libSpeechdWrapperInterface.GoString.ByValue str = new libSpeechdWrapperInterface.GoString.ByValue();
        str.p = text;
        str.n = text.length();

        int re = mainInstance.Speak(str, interrupt);
        if (re == 1) {
           log.info("Speaking(interrupt:" + interrupt + ")= " + text);
        } else {
            log.error("Unable to speak");
        }
    }

    @Override
    public void closeScreenReader() {
        if (mainInstance == null)
            return;

        int re = mainInstance.Close();
        if (re == 1) {
           log.info("Successfully closed screen reader");
        } else {
            log.error("Unable to close screen reader");
        }
    }

    private interface libSpeechdWrapperInterface extends Library {
        class GoString extends Structure {
            public static class ByValue extends GoString implements Structure.ByValue {
            }

            public String p;
            public long n;

            @SuppressWarnings({"unchecked", "rawtypes"})
            protected List getFieldOrder() {
                return Arrays.asList("p", "n");
            }
        }

        /**
         * Connects with the speechd socket. If no screen reader is activated, it fails to connect on the first try although if we again try to initialize after 3-4 seconds, it connects successfully.
         * @return 1 if successful and -1 if unsuccessful.
         */
        int Initialize();

        /**
         * Speaks the given text.  Important!! string variable is accessed in other languages in a special way, refer to <a href="https://github.com/vladimirvivien/go-cshared-examples">go-cshared-examples</a> for examples
         * @param text the text to speak.
         * @param interrupt whether to cancel the previous speech or not.
         * @return 1 if successful and -1 if unsuccessful.
         */
        int Speak(GoString.ByValue text, boolean interrupt);

        /**
         * Disconnects with the speechd socket.
         * @return 1 if successful and -1 if unsuccessful.
         */
        int Close();
    }
}
