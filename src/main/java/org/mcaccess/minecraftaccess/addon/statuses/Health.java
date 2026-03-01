package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class Health implements Status {
    @Override
    public @NotNull String message() {
        assert Minecraft.getInstance().player != null;
        return new Translation("minecraft_access.player_status.health")
                .variant("with_absorption", Minecraft.getInstance().player.getAbsorptionAmount() > 0)
                .variable("health").put(Minecraft.getInstance().player.getHealth() / 2.0)
                .variable("max").put(Minecraft.getInstance().player.getMaxHealth() / 2.0)
                .variable("absorption").put(Minecraft.getInstance().player.getAbsorptionAmount() / 2.0)
                .getString();
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
