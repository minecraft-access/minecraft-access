package org.mcaccess.minecraftaccess.utils.i18n;

import java.util.function.Supplier;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public abstract class Formatter<T> {
    abstract T apply(@Nullable Supplier<@NotNull String> value);

    public final T put(@NotNull String value) {
        return apply(() -> value);
    }

    public final T put(byte value) {
        return put(NarrationUtils.narrateNumber(value));
    }

    public final T put(short value) {
        return put(NarrationUtils.narrateNumber(value));
    }

    public final T put(int value) {
        return put(NarrationUtils.narrateNumber(value));
    }

    public final T put(long value) {
        return put(NarrationUtils.narrateNumber(value));
    }

    public final T put(float value) {
        return put(NarrationUtils.narrateNumber(value));
    }

    public final T put(double value) {
        return put(NarrationUtils.narrateNumber(value));
    }

    public final T put(@NotNull Component value) {
        return apply(value::getString);
    }

    public final T put(@NotNull FormattedCharSequence value) {
        return put(NarrationUtils.formattedCharSequenceToString(value));
    }

    public final T put(@NotNull Narratable value) {
        return apply(value::getString);
    }

    public final T put(@NotNull String type, @NotNull Identifier identifier) {
        return put(new Translation.Vanilla(type, identifier));
    }

    public final T put(@NotNull String type, @NotNull ResourceKey<?> key) {
        return put(new Translation.Vanilla(type, key));
    }

    public final T put(@NotNull String type, @NotNull Holder<?> holder) {
        return apply(Translation.Vanilla.fromHolder(type, holder)
                .<Supplier<String>>map(translation -> translation::getString)
                .orElse(null));
    }

    public final T putIfNotBlank(@NotNull String value) {
        return value.isBlank() ? apply(null) : put(value);
    }
}
