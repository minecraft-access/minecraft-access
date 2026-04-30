package org.mcaccess.minecraftaccess.utils.i18n;

import java.util.function.Supplier;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Untranslated implements Narratable {
    public static final @NotNull Formatter<Untranslated> FORMATTER = new Factory();
    private final @Nullable Supplier<@NotNull String> value;

    private Untranslated(@Nullable Supplier<@NotNull String> value) {
        this.value = value;
    }

    @Override
    public @NotNull String getString() {
        if (value != null) {
            return value.get();
        }
        return "";
    }

    private static final class Factory extends Formatter<Untranslated> {
        private Factory() {
        }

        @Override
        public @NotNull Untranslated apply(@Nullable Supplier<@NotNull String> value) {
            return new Untranslated(value);
        }
    }
}
