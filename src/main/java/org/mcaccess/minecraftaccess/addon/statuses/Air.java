package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.ClientConfig;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class Air implements Status {
    @Override
    public @NotNull String message() {
        assert Minecraft.getInstance().player != null;
        return I18n.get(
                "minecraft_access.player_status.air",
                NarrationUtils.narrateNumber(Math.max(Minecraft.getInstance().player.getAirSupply() / 20.0, 0.0)),
                NarrationUtils.narrateNumber(Minecraft.getInstance().player.getMaxAirSupply() / 20.0)
        );
    }

    @Override
    public @NotNull Visibility visibility() {
        Minecraft client = Minecraft.getInstance();
        assert client.gameMode != null;
        assert client.player != null;
        return client.gameMode.canHurtPlayer()
                && !client.player.canBreatheUnderwater()
                && (client.player.isUnderWater() || client.player.getAirSupply() < Minecraft.getInstance().player.getMaxAirSupply())
                ? Visibility.IMPORTANT
                : Visibility.NONE;
    }

    @Override
    public @NotNull WarningLevel warning() {
        if (visibility() == Visibility.NONE) {
            return WarningLevel.NONE;
        }

        assert Minecraft.getInstance().player != null;
        double air = Math.max(Minecraft.getInstance().player.getAirSupply() / 20.0, 0.0);

        if (air <= ClientConfig.getInstance().playerWarnings.airThreshold && air > 0) {
            return WarningLevel.WARNING;
        }
        return WarningLevel.NONE;
    }
}
