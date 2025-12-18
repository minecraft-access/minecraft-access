package org.mcaccess.minecraftaccess.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import org.mcaccess.minecraftaccess.MainClass;

public class TimeIndicator {
    private final Minecraft client = Minecraft.getInstance();
    private Times previousTime = null;

    public void tick() {
        Level level = client.level;

        if (level == null) return;
        if (level.dimensionType().hasFixedTime() || level.dimensionType().hasCeiling()) return;
        if (!level.canSeeSky(BlockPos.containing(client.player.getEyePosition()))) return;

        Times currentTime;

        long time = level.getDayTime() % 24000;

        if (time >= 10000 && time <= 12999) {
            currentTime = Times.AFTERNOON;
        } else if (time >= 13000 && time <= 23999) {
            currentTime = Times.NIGHT;
        } else {
            currentTime = Times.DAY;
        }

        if (currentTime == previousTime) return;

        switch (currentTime) {
            case AFTERNOON -> MainClass.narrate(I18n.get("minecraft_access.time.afternoon"), false);
            case DAY -> MainClass.narrate(I18n.get("minecraft_access.time.day"), false);
            case NIGHT -> MainClass.narrate(I18n.get("minecraft_access.time.night"), false);
        }

        previousTime = currentTime;
    }

    private enum Times {
        DAY,
        AFTERNOON,
        NIGHT
    }
}
