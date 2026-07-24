package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.blay09.mods.balm.client.platform.config.screen.BalmConfigScreen;
import net.minecraft.client.Minecraft;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;

public class OpenConfig implements AccessMenuFunction {
    @Override
    public void execute() {
        Minecraft.getInstance().gui.setScreen(BalmConfigScreen.forMod(null, MainClass.MOD_ID));
    }
}
