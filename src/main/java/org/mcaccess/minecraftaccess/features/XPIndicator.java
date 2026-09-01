package org.mcaccess.minecraftaccess.features;

import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.events.ServerChangeDetector;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

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
        if (!Config.getInstance().features.xpIndicatorEnabled || !Minecraft.getInstance().gameMode.hasExperience()) {
            return;
        }
        new Translation("minecraft_access.xp_indicator")
                .variant(value > previous ? "increased" : "decreased")
                .variable("level").put(value);
    }
}
