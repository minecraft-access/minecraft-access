package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;

/**
 * This feature speaks when the player xp level is increased or decreased.
 */
@Slf4j
public class XPIndicator {
    @Nullable
    private Integer previousXPLevel = null;

    public void update() {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.level == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen != null) return;

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

        String toSpeak = (increased) ? I18n.get("minecraft_access.xp_indicator.increased", currentXPLevel)
                : I18n.get("minecraft_access.xp_indicator.decreased", currentXPLevel);
        MainClass.speakWithNarrator(toSpeak, true);
    }
}
