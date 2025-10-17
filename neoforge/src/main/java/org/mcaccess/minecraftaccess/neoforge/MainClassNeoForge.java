package org.mcaccess.minecraftaccess.neoforge;

import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;

@Mod(value = MainClass.MOD_ID, dist = Dist.CLIENT)
public class MainClassNeoForge {
    public MainClassNeoForge(IEventBus modEventBus, ModContainer container) {
        modEventBus.register(this);
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> AutoConfig.getConfigScreen(Config.class, parent).get());
    }

    @SubscribeEvent
    private void onClientSetup(final FMLClientSetupEvent event) {
        MainClass.init();
    }
}
