package io.github.minecraft_access.minecraft_access.neoforge;

import io.github.minecraft_access.minecraft_access.MainClass;
import net.minecraft.client.MinecraftClient;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = MainClass.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class GameEventHandler {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        MainClass.clientTickEventsMethod(MinecraftClient.getInstance());
    }
}
