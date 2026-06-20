package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.ClientClockManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.Timeline;
import net.minecraft.world.timeline.Timelines;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.events.ServerChangeDetector;

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

        MainClass.narrate(I18n.get("minecraft_access.time." + time.toString()), false);
    }

    public static double getCurrentTime() {
        Timeline timeline = Minecraft.getInstance().level.registryAccess().get(Timelines.OVERWORLD_DAY).get().value();
        ClientClockManager clockManager = Minecraft.getInstance().level.clockManager();

        long current = timeline.getCurrentTicks(clockManager);
        Integer total = timeline.periodTicks().orElseThrow();

        return ((current + ((double) total / 4)) % total / (double) total) * 24.0;
    }

    private enum Times {
        DAY("day"),
        AFTERNOON("afternoon"),
        NIGHT("night");

        private final String keySuffix;

        Times(String keySuffix) {
            this.keySuffix = keySuffix;
        }

        private static Times of(double time) {
            if (time >= 12.00 && time < 19.00) {
                return AFTERNOON;
            } else if (time >= 19.00) {
                return NIGHT;
            } else {
                return DAY;
            }
        }

        @Override
        public String toString() {
            return keySuffix;
        }
    }
}
