package com.github.khanshoaib3.minecraft_access.neoforge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.khanshoaib3.minecraft_access.MainClass;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

@Mod(MainClass.MOD_ID)
public class MainClassNeoForge {
    private static final Logger logger = LoggerFactory.getLogger(MainClass.MOD_ID);

    public MainClassNeoForge(IEventBus modBus) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            logger.error("Minecraft Access can only be run client-side");
            return;
        }

        MainClass.init();
        MainClass.isNeoForge = true;
        NeoForge.EVENT_BUS.register(GameEventHandler.class);
        modBus.register(ModEventHandler.class);
    }
}
