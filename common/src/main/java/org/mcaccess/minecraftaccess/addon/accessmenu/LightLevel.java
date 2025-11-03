package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class LightLevel implements AccessMenuFunction {
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public void execute() {
        if (client.player == null) return;
        if (client.level == null) return;

        int light = client.level.getMaxLocalRawBrightness(client.player.blockPosition());
        MainClass.narrate(I18n.get("minecraft_access.access_menu.light_level", NarrationUtils.narrateNumber(light)), true);
    }
}
