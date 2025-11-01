package org.mcaccess.minecraftaccess.features;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;

import org.mcaccess.minecraftaccess.MainClass;

public class TimeIndicator {
    private final Minecraft client = Minecraft.getInstance();
    private Times currentTime = null;

    private enum Times {
        DAY,
        AFTERNOON,
        NIGHT
    }

    public void tick() {
        Level world = client.level;

        if (world == null) return;

        Times detectedTime;

        long time = world.getDayTime();

        if (time >= 10000 && time <= 12999) {
            detectedTime = Times.AFTERNOON;
        } else if (time >= 13000 && time <= 23999) {
            detectedTime = Times.NIGHT;
        } else {
            detectedTime = Times.DAY;
        }

        narrateTime(detectedTime);
    }

    private void narrateTime(Times time) {
        if (time == currentTime) return;

        switch (time) {
            case AFTERNOON -> MainClass.narrate(I18n.get("minecraft_access.time.afternoon"), false);
            case DAY -> MainClass.narrate(I18n.get("minecraft_access.time.day"), false);
            case NIGHT -> MainClass.narrate(I18n.get("minecraft_access.time.night"), false);
        }
        currentTime = time;
    }
}
