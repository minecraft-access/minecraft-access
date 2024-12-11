package io.github.minecraft_access.minecraft_access.features;

import io.github.minecraft_access.minecraft_access.MainClass;
import io.github.minecraft_access.minecraft_access.utils.PlayerUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

/**
 * This feature speaks when the player xp level is increased or decreased.
 */
@Slf4j
public class XPIndicator {
    private int previousXPLevel;

    public XPIndicator() {
        previousXPLevel = -999;
    }

    public void update() {
        try {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.world == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.currentScreen != null) return;

            int currentXPLevel = PlayerUtils.getExperienceLevel();
            if (previousXPLevel == currentXPLevel) return;

            boolean increased = previousXPLevel < currentXPLevel;
            previousXPLevel = currentXPLevel;

            String toSpeak = (increased) ? I18n.translate("minecraft_access.xp_indicator.increased", currentXPLevel)
                    : I18n.translate("minecraft_access.xp_indicator.decreased", currentXPLevel);
            MainClass.speakWithNarrator(toSpeak, true);
        } catch (Exception e) {
            log.error("An error occurred in xp indicator.", e);
        }
    }
}
