package org.mcaccess.minecraftaccess.fabric;

import net.fabricmc.api.ModInitializer;
import org.mcaccess.minecraftaccess.MainClass;

public class MainClassFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MainClass.init();
    }
}
