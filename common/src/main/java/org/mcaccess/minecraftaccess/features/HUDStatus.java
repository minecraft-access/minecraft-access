package org.mcaccess.minecraftaccess.features;

import org.mcaccess.minecraftaccess.MainClass;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

public class HUDStatus {
    @Getter
    private static HUDStatus instance = new HUDStatus();

    private Boolean wasHidden = MinecraftClient.getInstance().options.hudHidden;

    public void update() {
        Boolean isHidden = MinecraftClient.getInstance().options.hudHidden;

        if (wasHidden != isHidden) {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.hud_status.announce_".concat(isHidden ? "hidden" : "shown")), true);
            wasHidden = isHidden;
        }
    }
}
