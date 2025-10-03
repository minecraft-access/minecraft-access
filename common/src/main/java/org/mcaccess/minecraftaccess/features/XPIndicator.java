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
    private final Minecraft client = Minecraft.getInstance();
    @Nullable
    private Integer previousXPLevel = null;

    public void tick() {
        if (client.level == null) return;
        if (client.player == null) return;
        if (client.screen != null) return;

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
