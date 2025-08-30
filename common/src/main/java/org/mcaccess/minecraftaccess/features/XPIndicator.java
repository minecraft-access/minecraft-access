package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

/**
 * This feature narrates when the player xp level is increased or decreased.
 */
@Slf4j
public class XPIndicator {
    @Nullable
    private Integer previousXPLevel = null;

    public void tick() {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient.level == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen != null) return;

        int currentXPLevel = Minecraft.getInstance().player.experienceLevel;
        if (previousXPLevel == null) {
            previousXPLevel = currentXPLevel;
            return;
        }
        if (previousXPLevel == currentXPLevel) {
            return;
        }

        boolean increased = previousXPLevel < currentXPLevel;
        previousXPLevel = currentXPLevel;

        String narration = I18n.get(
                increased ? "minecraft_access.xp_indicator.increased" : "minecraft_access.xp_indicator.decreased",
                NarrationUtils.narrateNumber(currentXPLevel)
        );
        MainClass.narrate(narration, true);
    }
}
