package org.mcaccess.minecraftaccess.fabric;

import net.blay09.mods.balm.client.BalmClient;
import net.blay09.mods.balm.fabric.platform.runtime.FabricLoadContext;
import net.fabricmc.api.ModInitializer;

import org.mcaccess.minecraftaccess.MainClass;

public class MainClassFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        BalmClient.initializeMod(MainClass.MOD_ID, FabricLoadContext.INSTANCE, MainClass::init);
    }
}
