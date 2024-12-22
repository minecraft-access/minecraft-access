package com.github.khanshoaib3.minecraft_access.screen_reader;


public interface ScreenReaderInterface {

    /**
     * Initializes the available screen reader
     */
    void initializeScreenReader();

    /**
     * Check whether screen reader is initialized or not
     * @return Returns true if screen reader is initialized
     */
    boolean isInitialized();

    /**
     * Speaks the given text
     * @param text The text to be spoken
     * @param interrupt Specifies whether to skip speaking the previous text or not
     */
    void say(String text, boolean interrupt);

    /**
     * Closes the screen reader
     */
    void closeScreenReader();
}
