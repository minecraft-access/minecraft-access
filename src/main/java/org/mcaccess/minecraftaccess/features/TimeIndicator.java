package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.ClientClockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.Timeline;
import net.minecraft.world.timeline.Timelines;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.events.ServerChangeDetector;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

@Slf4j
public class TimeIndicator implements BalmClientModule {
    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "time_indicator");
    }

    @Override
    public void initialize() {
        new ServerChangeDetector<Times>().levelEvent((client, player, level) -> Times.of(getCurrentTime()), this::onChange);
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

    public static double getCurrentTime() {
        Timeline timeline = Minecraft.getInstance().level.registryAccess().get(Timelines.OVERWORLD_DAY).get().value();
        ClientClockManager clockManager = Minecraft.getInstance().level.clockManager();

        long current = timeline.getCurrentTicks(clockManager);
        Integer total = timeline.periodTicks().orElseThrow();

        return ((current + (total / 4)) % total / (double) total) * 24.0;
    }

    private enum Times {
        DAY,
        AFTERNOON,
        NIGHT;

        private static Times of(double time) {
            if (time >= 12.00 && time < 19.00) {
                return AFTERNOON;
            } else if (time >= 19.00) {
                return NIGHT;
            } else {
                return DAY;
            }
        }
    }
}
