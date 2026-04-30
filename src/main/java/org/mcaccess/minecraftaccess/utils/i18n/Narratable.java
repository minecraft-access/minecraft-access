package org.mcaccess.minecraftaccess.utils.i18n;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;

public interface Narratable {
    @Contract(pure = true)
    @NotNull String getString();

    default void narrate(boolean interrupt) {
        MainClass.narrate(getString(), interrupt);
    }
}
