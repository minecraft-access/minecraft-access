package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.GameType;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class GetXP implements AccessMenuFunction {
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public void execute() {
        if (client.player == null) return;

        assert client.gameMode != null;
        if (client.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            MainClass.narrate(I18n.get("gameMode.spectator"), true);
            return;
        } else if (client.gameMode.getPlayerMode() == GameType.CREATIVE) {
            MainClass.narrate(I18n.get("gameMode.creative"), true);
            return;
        }

        MainClass.narrate(I18n.get("minecraft_access.access_menu.xp",
                        NarrationUtils.narrateNumber(client.player.experienceLevel),
                        NarrationUtils.narrateNumber(client.player.experienceProgress * 100)),
                true);
    }
}
