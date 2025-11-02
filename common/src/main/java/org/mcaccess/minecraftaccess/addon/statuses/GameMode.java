package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.api.Status;

public class GameMode implements Status {
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public @NotNull String message() {
        assert client.level != null;
        assert client.gameMode != null;
        if (client.level.getLevelData().isHardcore()) {
            if (client.gameMode.getPlayerMode() == GameType.SURVIVAL) {
                return I18n.get("gameMode.hardcore");
            }
            return String.format(
                    "%s %s",
                    client.gameMode.getPlayerMode().getLongDisplayName().getString(),
                    I18n.get("selectWorld.gameMode.hardcore")
            );
        }
        return client.gameMode.getPlayerMode().getLongDisplayName().getString();
    }
}
