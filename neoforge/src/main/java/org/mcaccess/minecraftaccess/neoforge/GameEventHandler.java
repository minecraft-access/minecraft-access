package org.mcaccess.minecraftaccess.neoforge;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.mcaccess.minecraftaccess.MainClass;

@EventBusSubscriber(modid = MainClass.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class GameEventHandler {
    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        MainClass.clientTickEventsMethod(Minecraft.getInstance());
    }
}
