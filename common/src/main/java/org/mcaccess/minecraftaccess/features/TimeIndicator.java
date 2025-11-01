package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;

import org.mcaccess.minecraftaccess.MainClass;

@Slf4j
public class TimeIndicator {

    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        Level world = mc.level;

        if (world == null) return;

        long time = world.getDayTime();
        if (time == 10000 || time == 12000) {
            MainClass.narrate(I18n.get("minecraft_access.time.afternoon"), false);
        }

        if (time == 13000 || time == 24000) {
            MainClass.narrate(I18n.get("minecraft_access.time.night"), false);
        }

        if (time == 600 || time == 1000) {
            MainClass.narrate(I18n.get("minecraft_access.time.day"), false);
        }
    }
}
