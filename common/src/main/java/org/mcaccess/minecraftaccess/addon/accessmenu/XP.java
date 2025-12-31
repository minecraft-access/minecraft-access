package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.GameType;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class XP implements AccessMenuFunction {
    @Override
    public void execute() {
        if (Minecraft.getInstance().player == null) return;

        assert Minecraft.getInstance().gameMode != null;
        if (Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SPECTATOR) {
            MainClass.narrate(I18n.get("gameMode.spectator"), true);
            return;
        } else if (Minecraft.getInstance().gameMode.getPlayerMode() == GameType.CREATIVE) {
            MainClass.narrate(I18n.get("gameMode.creative"), true);
            return;
        }

        MainClass.narrate(I18n.get("minecraft_access.access_menu.xp",
                        NarrationUtils.narrateNumber(Minecraft.getInstance().player.experienceLevel),
                        NarrationUtils.narrateNumber(Minecraft.getInstance().player.experienceProgress * 100)),
                true);
    }
}
