package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class Frost implements Status {
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public @NotNull String message() {
        assert client.player != null;
        return I18n.get(
                "minecraft_access.player_status.frost",
                NarrationUtils.narrateNumber(client.player.getPercentFrozen() * 100.0)
        );
    }

    @Override
    public boolean show() {
        assert client.gameMode != null;
        assert client.player != null;
        return client.gameMode.canHurtPlayer() && client.player.canFreeze() && (client.player.isInPowderSnow || client.player.getPercentFrozen() > 0);
    }

    @Override
    public boolean important() {
        return true;
    }

    @Override
    public @NotNull WarningLevel warning() {
        if (!show()) {
            return WarningLevel.NONE;
        }

        assert client.player != null;
        double frostExposurePercent = client.player.getPercentFrozen() * 100.0;

        if (frostExposurePercent >= Config.getInstance().playerWarnings.frostThreshold) {
            return WarningLevel.WARNING;
        }
        return WarningLevel.NONE;
    }
}
