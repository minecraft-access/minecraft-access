package org.mcaccess.minecraftaccess.utils.events;

import java.util.function.Supplier;

import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;

public class ServerLocal<T> {
    public T value;

    public ServerLocal(Supplier<T> reset) {
        value = reset.get();
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> value = reset.get());
    }
}
