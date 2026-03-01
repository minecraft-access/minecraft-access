package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class Frost implements Status {
    @Override
    public @NotNull String message() {
        assert Minecraft.getInstance().player != null;
        return new Translation("minecraft_access.player_status.frost")
                .variable("frost").put(Minecraft.getInstance().player.getPercentFrozen() * 100.0)
                .getString();
    }

    @Override
    public @NotNull Visibility visibility() {
        assert Minecraft.getInstance().gameMode != null;
        assert Minecraft.getInstance().player != null;
        return Minecraft.getInstance().gameMode.canHurtPlayer()
                && Minecraft.getInstance().player.canFreeze()
                && (Minecraft.getInstance().player.isInPowderSnow || Minecraft.getInstance().player.getPercentFrozen() > 0)
                ? Visibility.IMPORTANT
                : Visibility.NONE;
    }

    @Override
    public @NotNull WarningLevel warning() {
        if (visibility() == Visibility.NONE) {
            return WarningLevel.NONE;
        }

        assert Minecraft.getInstance().player != null;
        double frostExposurePercent = Minecraft.getInstance().player.getPercentFrozen() * 100.0;

        if (frostExposurePercent >= Config.getInstance().playerWarnings.frostThreshold) {
            return WarningLevel.WARNING;
        }
        return WarningLevel.NONE;
    }
}
