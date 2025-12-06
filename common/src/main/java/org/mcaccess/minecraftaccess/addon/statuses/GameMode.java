package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.api.Status;

public class GameMode implements Status {
    @Override
    public @NotNull String message() {
        assert Minecraft.getInstance().level != null;
        assert Minecraft.getInstance().gameMode != null;
        if (Minecraft.getInstance().level.getLevelData().isHardcore()) {
            if (Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SURVIVAL) {
                return I18n.get("gameMode.hardcore");
            }
            return String.format(
                    "%s %s",
                    Minecraft.getInstance().gameMode.getPlayerMode().getLongDisplayName().getString(),
                    I18n.get("selectWorld.gameMode.hardcore")
            );
        }
        return Minecraft.getInstance().gameMode.getPlayerMode().getLongDisplayName().getString();
    }
}
