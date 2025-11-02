package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class Air implements Status {
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public @NotNull String message() {
        assert client.player != null;
        return I18n.get(
                "minecraft_access.player_status.air",
                NarrationUtils.narrateNumber(Math.max(client.player.getAirSupply() / 20.0, 0.0)),
                NarrationUtils.narrateNumber(client.player.getMaxAirSupply() / 20.0)
        );
    }

    @Override
    public boolean show() {
        assert client.gameMode != null;
        assert client.player != null;
        return client.gameMode.canHurtPlayer()
                && !client.player.canBreatheUnderwater()
                && (client.player.isUnderWater() || client.player.getAirSupply() < client.player.getMaxAirSupply());
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
        double air = Math.max(client.player.getAirSupply() / 20.0, 0.0);

        if (air <= Config.getInstance().playerWarnings.airThreshold && air > 0) {
            return WarningLevel.WARNING;
        }
        return WarningLevel.NONE;
    }
}
