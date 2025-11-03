package org.mcaccess.minecraftaccess.addon.accessmenu;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;

public class OpenConfig implements AccessMenuFunction {
    @Override
    public void execute() {
        Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, null).get());
    }
}
