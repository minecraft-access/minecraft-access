package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class Armour implements Status {
    private final Minecraft client = Minecraft.getInstance();

    @Override
    public @NotNull String message() {
        assert client.player != null;
        return I18n.get(
                "minecraft_access.player_status.armour",
                NarrationUtils.narrateNumber(client.player.getArmorValue() / 2.0)
        );
    }

    @Override
    public boolean show() {
        assert client.gameMode != null;
        return client.gameMode.canHurtPlayer();
    }
}
