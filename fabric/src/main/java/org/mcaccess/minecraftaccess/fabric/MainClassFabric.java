package org.mcaccess.minecraftaccess.fabric;

import java.util.List;

import net.blay09.mods.balm.api.EmptyLoadContext;
import net.blay09.mods.balm.api.client.BalmClient;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.MinecraftAccessAddon;

public class MainClassFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        List<MainClass.Addon> addons = FabricLoader.getInstance().getEntrypointContainers(MainClass.MOD_ID, MinecraftAccessAddon.class).stream()
                .map(container -> new MainClass.Addon(container.getProvider().getMetadata().getId(), container.getEntrypoint()))
                .toList();
        BalmClient.initializeMod(MainClass.MOD_ID, EmptyLoadContext.INSTANCE, registrars -> MainClass.init(registrars, addons));
    }
}
