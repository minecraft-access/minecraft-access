package org.mcaccess.minecraftaccess.addon.accessmenu;

import me.shedaniel.autoconfig.AutoConfigClient;
import net.minecraft.client.Minecraft;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;

public class OpenConfig implements AccessMenuFunction {
    @Override
    public void execute() {
        Minecraft.getInstance().gui.setScreen(AutoConfigClient.getConfigScreen(Config.class, null).get());
    }
}
