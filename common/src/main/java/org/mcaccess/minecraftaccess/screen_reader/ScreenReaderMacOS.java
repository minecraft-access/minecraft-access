package com.github.khanshoaib3.minecraft_access.screen_reader;

import com.github.khanshoaib3.minecraft_access.config.config_maps.SpeechSettingsConfigMap;
import java.lang.ProcessHandle;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScreenReaderMacOS implements ScreenReaderInterface {
    private objcRuntimeInterface objcRuntimeInstance = null;
    private Pointer speechSynthesizer = null;

    @Override
    public void initializeScreenReader() {
        log.info("Initializing MacOS Objective C runtime");
        objcRuntimeInstance = Native.load("objc", objcRuntimeInterface.class);

        // Allocate an instance of the AVSpeechSynthesizer class
        speechSynthesizer = objcRuntimeInstance.objc_msgSend(
            objcRuntimeInstance.objc_getClass("AVSpeechSynthesizer"),
            objcRuntimeInstance.sel_getUid("alloc"));

        // Initialize the instance
        objcRuntimeInstance.objc_msgSend(
            speechSynthesizer,
            objcRuntimeInstance.sel_getUid("init"));

        log.info("Successfully initialized screen reader");
    }

    @Override
    public boolean isInitialized() {
        return speechSynthesizer != null;
    }

    @Override
    public void say(String text, boolean interrupt) {
        if (speechSynthesizer == null)
            return;

        if (interrupt) {
            // Stop speech immediately
            objcRuntimeInstance.objc_msgSend(
                speechSynthesizer,
                objcRuntimeInstance.sel_getUid("stopSpeakingAtBoundary:"),
                0);
        }

        // Get speech rate here in case the user changes it
        SpeechSettingsConfigMap map = SpeechSettingsConfigMap.getInstance();
        float speechRate = map.getSpeechRate()/100;

        // Convert the text to be spoken into an NSString object
        Pointer stringObject = objcRuntimeInstance.objc_msgSend(
            objcRuntimeInstance.objc_getClass("NSString"),
            objcRuntimeInstance.sel_getUid("stringWithUTF8String:"),
            text);

        // Allocate an instance of AVSpeechUtterance
        Pointer utterance = objcRuntimeInstance.objc_msgSend(
            objcRuntimeInstance.objc_getClass("AVSpeechUtterance"),
            objcRuntimeInstance.sel_getUid("alloc"));

        // Initialize the instance with the string to be spoken
        objcRuntimeInstance.objc_msgSend(
            utterance,
            objcRuntimeInstance.sel_getUid("initWithString:"),
            stringObject);

        // Set the prefersAssistiveTechnologySettings property to true to use the voice and other settings from Spoken Content
        // Set it to a byte value of 1 since Objective C booleans map to the char type
        objcRuntimeInstance.objc_msgSend(
            utterance,
            objcRuntimeInstance.sel_getUid("setPrefersAssistiveTechnologySettings:"),
            (byte) 0);

        // Set the rate manually since the previous step does not set it for some reason
        objcRuntimeInstance.objc_msgSend(
            utterance,
            objcRuntimeInstance.sel_getUid("setRate:"),
            speechRate);

        // Speak the utterance
        objcRuntimeInstance.objc_msgSend(
            speechSynthesizer,
            objcRuntimeInstance.sel_getUid("speakUtterance:"),
            utterance);

        log.info("Speaking(interrupt:" + interrupt + ")= " + text);

        // Release the utterance so the Objective C runtime can free it
        objcRuntimeInstance.objc_msgSend(
            utterance,
            objcRuntimeInstance.sel_getUid("release"));
    }

    @Override
    public void closeScreenReader() {
        if (speechSynthesizer == null)
            return;

        // Release the speech synthesizer object so the Objective C runtime can free it
        objcRuntimeInstance.objc_msgSend(
            speechSynthesizer,
            objcRuntimeInstance.sel_getUid("release"));

        log.info("Successfully closed screen reader");
    }

    private interface objcRuntimeInterface extends Library {
        /**
         * Looks up an Objective C class by name.
         * @return A pointer to an Objective C class
         */
        Pointer objc_getClass(String name);

        /**
         * Creates an Objective C selector. Selectors are a reference to a method name, not a specific method, so two methods with the same name will have the same selector.
         * @return A pointer to an Objective C selector
         */
        Pointer sel_getUid(String name);

        /**
         * Sends a message to an Objective C object or class. This is how object creation and method invocation is done.
         * @return A pointer to the result
         */
        Pointer objc_msgSend(Pointer reciever, Pointer sel);
        Pointer objc_msgSend(Pointer reciever, Pointer sel, Object arg1);
    }
}
