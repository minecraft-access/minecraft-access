package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.GameType;

import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class XP implements AccessMenuFunction {
    @Override
    public void execute() {
        if (Minecraft.getInstance().player == null) return;

        assert Minecraft.getInstance().gameMode != null;
        if (Minecraft.getInstance().gameMode.getPlayerMode() == GameType.SPECTATOR) {
            new Translation.Vanilla("gameMode.spectator")
                    .narrate(true);
            return;
        } else if (Minecraft.getInstance().gameMode.getPlayerMode() == GameType.CREATIVE) {
            new Translation.Vanilla("gameMode.creative")
                    .narrate(true);
            return;
        }

        new Translation("minecraft_access.access_menu.xp")
            .variable("level").put(Minecraft.getInstance().player.experienceLevel)
            .variable("progress").put(Minecraft.getInstance().player.experienceProgress * 100)
            .narrate(true);
    }
}
