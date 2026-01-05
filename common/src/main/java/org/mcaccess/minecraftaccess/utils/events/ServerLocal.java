package org.mcaccess.minecraftaccess.utils.events;

import java.util.function.Supplier;

import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import org.jetbrains.annotations.Contract;

public class ServerLocal<T> {
    private T value;

    public ServerLocal(Supplier<T> reset) {
        value = reset.get();
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> value = reset.get());
    }

    @Contract(pure = true)
    public T get() {
        return value;
    }

    @Contract(mutates = "this")
    public void set(T value) {
        this.value = value;
    }
}
