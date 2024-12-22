package org.mcaccess.minecraftaccess.features;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import org.jetbrains.annotations.Nullable;

/**
 * This feature speaks when the player xp level is increased or decreased.
 */
@Slf4j
public class XPIndicator {
    @Nullable
    private Integer previousXPLevel = null;

    public void update() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.world == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.currentScreen != null) return;

        int currentXPLevel = PlayerUtils.getExperienceLevel();
        if (previousXPLevel == null) {
            previousXPLevel = currentXPLevel;
            return;
        }
        if (previousXPLevel == currentXPLevel) {
            return;
        }

        boolean increased = previousXPLevel < currentXPLevel;
        previousXPLevel = currentXPLevel;

        String toSpeak = (increased) ? I18n.translate("minecraft_access.xp_indicator.increased", currentXPLevel)
                : I18n.translate("minecraft_access.xp_indicator.decreased", currentXPLevel);
        MainClass.speakWithNarrator(toSpeak, true);
    }
}
