package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class Frost implements Status {
    @Override
    public @NotNull String message() {
        assert Minecraft.getInstance().player != null;
        return I18n.get(
                "minecraft_access.player_status.frost",
                NarrationUtils.narrateNumber(Minecraft.getInstance().player.getPercentFrozen() * 100.0)
        );
    }

    @Override
    public boolean show() {
        assert Minecraft.getInstance().gameMode != null;
        assert Minecraft.getInstance().player != null;
        return Minecraft.getInstance().gameMode.canHurtPlayer() && Minecraft.getInstance().player.canFreeze() && (Minecraft.getInstance().player.isInPowderSnow || Minecraft.getInstance().player.getPercentFrozen() > 0);
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

        assert Minecraft.getInstance().player != null;
        double frostExposurePercent = Minecraft.getInstance().player.getPercentFrozen() * 100.0;

        if (frostExposurePercent >= Config.getInstance().playerWarnings.frostThreshold) {
            return WarningLevel.WARNING;
        }
        return WarningLevel.NONE;
    }
}
