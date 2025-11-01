package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;
import org.mcaccess.minecraftaccess.MainClass;

@Slf4j
public class WeatherIndicator {
    private int actual = 0;

    public void tick() {
        Minecraft mc = Minecraft.getInstance();
        Level world = mc.level;

        if (world == null) return;
        boolean rain = world.isRaining();
        boolean thunder = world.isThundering();

        if (rain) {
            interprete(2);
        }

        if (thunder) {
            interprete(3);
        }

        if (!rain && !thunder) {
            interprete(1);
        }
    }

    private void interprete(int weather) {
        if (weather == actual) return;

        if (weather == 1) {
            MainClass.narrate(I18n.get("minecraft_access.weather.clear"), false);
            actual = 1;
        }

        if (weather == 2) {
            MainClass.narrate(I18n.get("minecraft_access.weather.rain"), false);
            actual = 2;
        }

        if (weather == 3) {
            MainClass.narrate(I18n.get("minecraft_access.weather.thunder"), false);
            actual = 3;
        }
    }
}
