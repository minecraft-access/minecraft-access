package org.mcaccess.minecraftaccess.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.MinecraftAccessAddon;

public class MainClassFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MainClass.init(FabricLoader.getInstance().getEntrypoints(MainClass.MOD_ID, MinecraftAccessAddon.class));
    }
}
