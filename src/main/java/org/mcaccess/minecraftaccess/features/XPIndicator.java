package org.mcaccess.minecraftaccess.features;

import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.ModConfig;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.events.ServerChangeDetector;

/**
 * This feature narrates when the player xp level is increased or decreased.
 */
public class XPIndicator implements BalmClientModule {
    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "xp_indicator");
    }

    @Override
    public void initialize() {
        new ServerChangeDetector<Integer>().levelEvent((_, _, _) -> {
            assert Minecraft.getInstance().player != null;
            return Minecraft.getInstance().player.experienceLevel;
        }, this::onChange);
    }

    private void onChange(Minecraft client, Player player, Level level, Integer previous, Integer value) {
        assert Minecraft.getInstance().gameMode != null;
        if (!ModConfig.getInstance().features.xpIndicatorEnabled || !Minecraft.getInstance().gameMode.hasExperience()) {
            return;
        }
        MainClass.narrate(I18n.get(
                previous < value ? "minecraft_access.xp_indicator.increased" : "minecraft_access.xp_indicator.decreased",
                NarrationUtils.narrateNumber(value)
        ), true);
    }
}
