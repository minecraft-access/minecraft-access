package org.mcaccess.minecraftaccess.neoforge;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import net.minecraft.client.option.KeyBinding;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = MainClass.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventHandler {
    @SubscribeEvent
    public static void registerKeybindings(RegisterKeyMappingsEvent event) {
        for (KeyBinding kb : KeyBindingsHandler.getInstance().getKeys()) {
            event.register(kb);
        }
    }
}
