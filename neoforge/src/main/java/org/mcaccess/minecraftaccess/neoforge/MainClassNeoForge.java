package org.mcaccess.minecraftaccess.neoforge;

import me.shedaniel.autoconfig.AutoConfig;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(value = "minecraft_access", dist = Dist.CLIENT)
public class MainClassNeoForge {
    private static final Logger logger = LoggerFactory.getLogger(MainClass.MOD_ID);

    public MainClassNeoForge(ModContainer container) {
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            logger.error("Minecraft Access can only be run client-side");
            return;
        }

        MainClass.init();
        MainClass.isNeoForge = true;
        container.registerExtensionPoint(IConfigScreenFactory.class, (modContainer, parent) -> AutoConfig.getConfigScreen(Config.class, parent).get());
    }
}
