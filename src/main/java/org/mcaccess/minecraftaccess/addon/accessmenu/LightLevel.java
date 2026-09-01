package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;

import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class LightLevel implements AccessMenuFunction {
    @Override
    public void execute() {
        assert Minecraft.getInstance().level != null;
        assert Minecraft.getInstance().player != null;
        new Translation("minecraft_access.access_menu.light_level")
                .variable("level").put(Minecraft.getInstance().level.getMaxLocalRawBrightness(Minecraft.getInstance().player.blockPosition()))
                .narrate(true);
    }
}
