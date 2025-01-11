package org.mcaccess.minecraftaccess.features;

import org.mcaccess.minecraftaccess.MainClass;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

public class HudStatus {
    @Getter
    private static HudStatus instance = new HudStatus();

    private Boolean wasHidden = MinecraftClient.getInstance().options.hudHidden;

    public void update() {
        Boolean isHidden = MinecraftClient.getInstance().options.hudHidden;

        if (wasHidden != isHidden) {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.hudStatus.announce_".concat(isHidden ? "hidden" : "shown")), true);
            wasHidden = isHidden;
        }
    }
}
