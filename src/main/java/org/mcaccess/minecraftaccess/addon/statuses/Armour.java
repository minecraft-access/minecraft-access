package org.mcaccess.minecraftaccess.addon.statuses;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class Armour implements Status {
    @Override
    public @NotNull String message() {
        assert Minecraft.getInstance().player != null;
        return new Translation("minecraft_access.player_status.armour")
                .variable("armour").put(Minecraft.getInstance().player.getArmorValue() / 2.0)
                .getString();
    }

    @Override
    public @NotNull Visibility visibility() {
        assert Minecraft.getInstance().gameMode != null;
        return Minecraft.getInstance().gameMode.canHurtPlayer() ? Visibility.NORMAL : Visibility.NONE;
    }
}
