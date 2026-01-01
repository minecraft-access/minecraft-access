package org.mcaccess.minecraftaccess.features;

import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

/**
 * This feature narrates when the player xp level is increased or decreased.
 */
public class XPIndicator implements BalmClientModule {
    @Nullable
    private Integer previousXPLevel = null;

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "xp_indicator");
    }

    @Override
    public void initialize() {
        ClientTickCallback.ClientLevelTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            previousXPLevel = null;
        });
    }

    private void tick(Level level) {
        assert Minecraft.getInstance().gameMode != null;
        if (!Config.getInstance().features.xpIndicatorEnabled || !Minecraft.getInstance().gameMode.hasExperience()) {
            return;
        }

        assert Minecraft.getInstance().player != null;
        int currentXPLevel = Minecraft.getInstance().player.experienceLevel;
        if (previousXPLevel == null) {
            previousXPLevel = currentXPLevel;
            return;
        }
        if (previousXPLevel == currentXPLevel) {
            return;
        }

        boolean increased = previousXPLevel < currentXPLevel;
        previousXPLevel = currentXPLevel;

        String narration = I18n.get(
                increased ? "minecraft_access.xp_indicator.increased" : "minecraft_access.xp_indicator.decreased",
                NarrationUtils.narrateNumber(currentXPLevel)
        );
        MainClass.narrate(narration, true);
    }
}
