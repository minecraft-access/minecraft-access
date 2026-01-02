package org.mcaccess.minecraftaccess.utils;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public class ChangeDetector<T> {
    protected T previous;

    public ChangeDetector() {
        this(null);
    }

    public ChangeDetector(T value) {
        previous = value;
    }

    public boolean update(T value) {
        return updateAndGet(value).isPresent();
    }

    public Optional<T> updateAndGet(T value) {
        T previous = this.previous;
        this.previous = value;
        if (value == null) {
            return Optional.empty();
        }
        if (Objects.equals(value, previous)) {
            return Optional.empty();
        }
        return Optional.ofNullable(previous);
    }

    public void clientEvent(Function<Minecraft, T> update, Callback<T> callback) {
        ClientTickCallback.AFTER.register(client -> {
            T value = update.apply(client);
            updateAndGet(value).ifPresent(previous -> callback.handle(client, previous, value));
        });
    }

    @FunctionalInterface
    public interface Callback<T> {
        void handle(@NotNull Minecraft client, T previous, T value);
    }
}
