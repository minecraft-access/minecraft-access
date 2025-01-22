package org.mcaccess.minecraftaccess.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.mcaccess.minecraftaccess.MainClass;

@Mod(MainClass.MOD_ID)
public class MainClassNeoForge {
    public MainClassNeoForge(IEventBus modBus) {
        MainClass.init();
    }
}
