package org.mcaccess.minecraftaccess.neoforge;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.mcaccess.minecraftaccess.MainClass;

@Mod(value = "minecraft_access", dist = Dist.CLIENT)
public class MainClassNeoForge {
    public MainClassNeoForge(IEventBus modBus) {
        MainClass.init();
    }
}
