package org.mcaccess.minecraftaccess.neoforge;

import me.shedaniel.autoconfig.AutoConfig;
import net.blay09.mods.balm.api.client.BalmClient;
import net.blay09.mods.balm.neoforge.NeoForgeLoadContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;

@Mod(value = MainClass.MOD_ID, dist = Dist.CLIENT)
public class MainClassNeoForge {
    public MainClassNeoForge(IEventBus modEventBus, ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> AutoConfig.getConfigScreen(Config.class, parent).get());
        BalmClient.initializeMod(MainClass.MOD_ID, new NeoForgeLoadContext(modEventBus), MainClass::init);
    }
}
