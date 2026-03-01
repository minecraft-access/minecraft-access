package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class GameMode implements Status {
    @Override
    public @NotNull String message() {
        assert Minecraft.getInstance().level != null;
        assert Minecraft.getInstance().gameMode != null;
        if (Minecraft.getInstance().level.getLevelData().isHardcore()) {
            if (Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SURVIVAL) {
                return new Translation.Vanilla("gameMode.hardcore").getString();
            }
            return new Translation.Delimited()
                    .put(Minecraft.getInstance().gameMode.getPlayerMode().getLongDisplayName())
                    .put(new Translation.Vanilla("selectWorld.gameMode.hardcore"))
                    .getString();
        }
        return Minecraft.getInstance().gameMode.getPlayerMode().getLongDisplayName().getString();
    }
}
