package org.mcaccess.minecraftaccess.utils.i18n;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.PlainTextContents;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import org.mcaccess.minecraftaccess.MainClass;

public interface Narratable {
    @Contract(pure = true)
    @NotNull String getString();

    default void narrate(boolean interrupt) {
        MainClass.narrate(getString(), interrupt);
    }

    @Contract(pure = true)
    default @NonNull Component toComponent() {
        return MutableComponent.create(new PlainTextContents() {
            @Override
            public @NonNull String text() {
                return getString();
            }
        });
    }
}
