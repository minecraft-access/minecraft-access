package org.mcaccess.minecraftaccess.utils.events;

import net.blay09.mods.balm.platform.event.EventMapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface ClientPlayingTick {
    EventMapper<@NotNull ClientPlayingTick> AFTER = EventMapper.createUnbound("ClientWorldTick.After");

    void handle(@NotNull Minecraft client, @NotNull LocalPlayer player, @NotNull ClientLevel level);
}
