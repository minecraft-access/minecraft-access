package com.github.khanshoaib3.minecraft_access.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.loader.impl.util.log.Log;
import net.fabricmc.loader.impl.util.log.LogCategory;

public class MinecraftAccessServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        Log.error(LogCategory.GENERAL, "Minecraft Access can only be run client-side");
    }
}
