package org.mcaccess.minecraftaccess.utils;

import java.util.function.Function;
import java.util.function.Supplier;

import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.minecraft.world.level.Level;

public class ServerChangeDetector<T> extends ChangeDetector<T> {
    public ServerChangeDetector() {
        this(() -> null);
    }

    public ServerChangeDetector(Supplier<T> reset) {
        super(reset.get());
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> previous = reset.get());
    }

    public void levelEvent(Function<Level, T> update, Callback<Level, T> callback) {
        ClientTickCallback.ClientLevelTick.AFTER.register(level -> {
            T value = update.apply(level);
            updateAndGet(value).ifPresent(previous -> callback.handle(level, previous, value));
        });
    }
}
