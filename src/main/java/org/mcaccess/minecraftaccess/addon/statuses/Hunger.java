package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.ModConfig;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class Hunger implements Status {
    @Override
    public @NotNull String message() {
        assert Minecraft.getInstance().player != null;
        return I18n.get(
                "minecraft_access.player_status.hunger",
                NarrationUtils.narrateNumber(Minecraft.getInstance().player.getFoodData().getFoodLevel() / 2.0)
        );
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
        double hunger = Minecraft.getInstance().player.getFoodData().getFoodLevel() / 2.0;

        if (hunger <= ModConfig.getInstance().playerWarnings.hungerThreshold) {
            return WarningLevel.WARNING;
        }
        return WarningLevel.NONE;
    }
}
