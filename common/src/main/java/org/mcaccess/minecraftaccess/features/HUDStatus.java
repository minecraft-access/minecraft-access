package org.mcaccess.minecraftaccess.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.MainClass;

public class HUDStatus {
    private Boolean wasHidden = Minecraft.getInstance().options.hideGui;

    public void tick() {
        Boolean isHidden = Minecraft.getInstance().options.hideGui;

        if (wasHidden != isHidden) {
            MainClass.narrate(I18n.get("minecraft_access.hud_status.announce_".concat(isHidden ? "hidden" : "shown")), true);
            wasHidden = isHidden;
        }
    }
}
