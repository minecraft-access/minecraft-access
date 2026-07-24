package org.mcaccess.minecraftaccess.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.blay09.mods.balm.client.platform.config.screen.BalmConfigScreen;

import org.mcaccess.minecraftaccess.MainClass;

public class ModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> BalmConfigScreen.forMod(parent, MainClass.MOD_ID);
    }
}
