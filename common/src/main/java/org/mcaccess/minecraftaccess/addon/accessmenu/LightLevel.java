package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class LightLevel implements AccessMenuFunction {
    @Override
    public void execute() {
        if (Minecraft.getInstance().player == null) return;
        if (Minecraft.getInstance().level == null) return;

        int light = Minecraft.getInstance().level.getMaxLocalRawBrightness(Minecraft.getInstance().player.blockPosition());
        MainClass.narrate(I18n.get("minecraft_access.access_menu.light_level", NarrationUtils.narrateNumber(light)), true);
    }
}
