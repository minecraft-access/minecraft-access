package org.mcaccess.minecraftaccess.utils;

import com.mojang.text2speech.Narrator;

public class NarratorDummy implements Narrator {
    @Override
    public boolean active() {
        return true;
    }

    @Override
    public void clear() {
    }

    @Override
    public void destroy() {
    }

    @Override
    public void say(String msg, boolean interrupt, float volume) {
    }
}
