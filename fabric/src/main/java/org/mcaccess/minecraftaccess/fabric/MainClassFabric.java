package org.mcaccess.minecraftaccess.fabric;

import net.blay09.mods.balm.api.Balm;
import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.fabricmc.api.ModInitializer;

import org.mcaccess.minecraftaccess.MainClass;

public class MainClassFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        Balm.initializeMod(MainClass.MOD_ID, EmptyLoadContext.INSTANCE, () -> {});
        BalmClient.initializeMod(MainClass.MOD_ID, EmptyLoadContext.INSTANCE, MainClass::init);
    }
}
