package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class Hunger implements Status {
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public @NotNull String message() {
        assert client.player != null;
        return I18n.get(
                "minecraft_access.player_status.hunger",
                NarrationUtils.narrateNumber(client.player.getFoodData().getFoodLevel() / 2.0)
        );
    }

    @Override
    public boolean show() {
        assert client.gameMode != null;
        return client.gameMode.canHurtPlayer();
    }

    @Override
    public @NotNull WarningLevel warning() {
        if (!show()) {
            return WarningLevel.NONE;
        }

        assert client.player != null;
        double hunger = client.player.getFoodData().getFoodLevel() / 2.0;

        if (hunger <= Config.getInstance().playerWarnings.hungerThreshold) {
            return WarningLevel.WARNING;
        }
        return WarningLevel.NONE;
    }
}
