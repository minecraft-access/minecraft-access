package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class Air implements Status {
    @Override
    public @NotNull String message() {
        assert Minecraft.getInstance().player != null;
        return new Translation("minecraft_access.player_status.air")
                .variable("current").put(Math.max(Minecraft.getInstance().player.getAirSupply() / 20.0, 0.0))
                .variable("max").put(Minecraft.getInstance().player.getMaxAirSupply() / 20.0)
                .getString();
    }

    @Override
    public @NotNull Visibility visibility() {
        assert Minecraft.getInstance().gameMode != null;
        assert Minecraft.getInstance().player != null;
        return Minecraft.getInstance().gameMode.canHurtPlayer()
                && !Minecraft.getInstance().player.canBreatheUnderwater()
                && (Minecraft.getInstance().player.isUnderWater() || Minecraft.getInstance().player.getAirSupply() < Minecraft.getInstance().player.getMaxAirSupply())
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

        if (air <= Config.getInstance().playerWarnings.airThreshold && air > 0) {
            return WarningLevel.WARNING;
        }
        return WarningLevel.NONE;
    }
}
