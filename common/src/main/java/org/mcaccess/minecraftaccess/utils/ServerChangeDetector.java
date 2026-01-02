package org.mcaccess.minecraftaccess.utils;

import java.util.function.Supplier;

import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;

public class ServerChangeDetector<T> extends ChangeDetector<T> {
    public ServerChangeDetector() {
        this(() -> null);
    }

    public ServerChangeDetector(Supplier<T> reset) {
        super(reset.get());
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> previous = reset.get());
    }

    public void levelEvent(Update<T> update, Callback<T> callback) {
        ClientPlayingTick.AFTER.register((client, player, level) -> {
            T value = update.update(client, player, level);
            updateAndGet(value).ifPresent(previous -> callback.handle(client, player, level, previous, value));
        });
    }

    @FunctionalInterface
    public interface Update<T> {
        T update(@NotNull Minecraft client, @NotNull LocalPlayer player, @NotNull ClientLevel level);
    }

    @FunctionalInterface
    public interface Callback<T> {
        void handle(@NotNull Minecraft client, @NotNull LocalPlayer player, @NotNull ClientLevel level, T previous, T value);
    }
}
