package org.mcaccess.minecraftaccess.features;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.MainClass;

public class HUDStatus {
    @Getter
    private static HUDStatus instance = new HUDStatus();

    private Boolean wasHidden = Minecraft.getInstance().options.hideGui;

    public void update() {
        Boolean isHidden = Minecraft.getInstance().options.hideGui;

        if (wasHidden != isHidden) {
            MainClass.speakWithNarrator(I18n.get("minecraft_access.hud_status.announce_".concat(isHidden ? "hidden" : "shown")), true);
            wasHidden = isHidden;
        }
    }
}
