package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class Health implements Status {
    @Override
    public @NotNull String message() {
        assert Minecraft.getInstance().player != null;
        if (Minecraft.getInstance().player.getAbsorptionAmount() > 0) {
            return I18n.get(
                    "minecraft_access.player_status.health_with_absorption",
                    NarrationUtils.narrateNumber(Minecraft.getInstance().player.getHealth() / 2.0),
                    NarrationUtils.narrateNumber(Minecraft.getInstance().player.getMaxHealth() / 2.0),
                    NarrationUtils.narrateNumber(Minecraft.getInstance().player.getAbsorptionAmount() / 2.0)
            );
        } else {
            return I18n.get(
                    "minecraft_access.player_status.health",
                    NarrationUtils.narrateNumber(Minecraft.getInstance().player.getHealth() / 2.0),
                    NarrationUtils.narrateNumber(Minecraft.getInstance().player.getMaxHealth() / 2.0)
            );
        }
    }

    @Override
    public @NotNull Visibility visibility() {
        assert Minecraft.getInstance().gameMode != null;
        return Minecraft.getInstance().gameMode.canHurtPlayer() ? Visibility.NORMAL : Visibility.NONE;
    }

    @Override
    public @NotNull WarningLevel warning() {
        if (visibility() == Visibility.NONE) {
            return WarningLevel.NONE;
        }

        assert Minecraft.getInstance().player != null;
        double health = Minecraft.getInstance().player.getHealth() / 2.0;

        if (health <= Config.getInstance().playerWarnings.secondHealthThreshold) {
            return WarningLevel.CRITICAL;
        }
        if (health <= Config.getInstance().playerWarnings.firstHealthThreshold) {
            return WarningLevel.WARNING;
        }
        return WarningLevel.NONE;
    }
}
