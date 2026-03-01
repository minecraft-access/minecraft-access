package org.mcaccess.minecraftaccess.features;

import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.events.ServerChangeDetector;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class TimeIndicator implements BalmClientModule {
    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "time_indicator");
    }

    @Override
    public void initialize() {
        new ServerChangeDetector<Times>().levelEvent((client, player, level) -> Times.of(level.getGameTime()), this::onChange);
    }

    private void onChange(Minecraft client, Player player, Level level, Times previous, Times time) {
        if (level.dimensionType().hasFixedTime() || level.dimensionType().hasCeiling()) return;
        assert Minecraft.getInstance().player != null;
        if (!level.canSeeSky(BlockPos.containing(Minecraft.getInstance().player.getEyePosition()))) return;

        new Translation("minecraft_access.time")
                .variant(switch (time) {
                    case AFTERNOON -> "afternoon";
                    case DAY -> "day";
                    case NIGHT -> "night";
                })
                .narrate(false);
    }

    private enum Times {
        DAY,
        AFTERNOON,
        NIGHT;

        private static Times of(long ticks) {
            long time = ticks % 24000;

            if (time >= 10000 && time < 13000) {
                return AFTERNOON;
            } else if (time >= 13000) {
                return NIGHT;
            } else {
                return DAY;
            }
        }
    }
}
