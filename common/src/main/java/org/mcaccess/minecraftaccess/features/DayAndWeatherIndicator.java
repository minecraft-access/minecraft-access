package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.Level;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;


@Slf4j
public class DayAndWeatherIndicator {
    private int weather = 0;

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = client.player;
        Level world = client.level;
        if (world == null || player == null) return;
        if (client.screen != null) return;
        dayTime(client, world, player);
        weather(client, world, player);
        getWeatherFromKey();
    }

    public void dayTime(Minecraft client, Level world, LocalPlayer player) {
        long actualTime = world.dayTime();
        if (actualTime == 6000 || actualTime == 7000 || actualTime == 8000) {
            MainClass.narrate("Está amaneciendo", false);
        }

        if (actualTime == 17000) {
            MainClass.narrate("es de tarde, está por anocheser.", false);
        }

        if (actualTime == 7000 || actualTime == 8000) {
            MainClass.narrate("ya anocheció", true);
        }
    }

    public void weather(Minecraft client, Level world, LocalPlayer player) {
        Boolean rain = world.isRaining();
        boolean thunder = world.isThundering();
        int isWeather = 0;
        if (rain) {
            isWeather = 2;
        }
        if (thunder) {
            isWeather = 3;
        }
        if (!rain && !thunder) {
            isWeather = 1;
        }
        if (isWeather != weather) {
            interpreteWeather(isWeather, false);
            weather = isWeather;
        }
    }

    private void getWeatherFromKey() {
        if (KeyUtils.isAnyPressed(KeyBindingsHandler.Keys.WEATHER.mapping)) {
            interpreteWeather(weather, true);
        }
    }

    private void interpreteWeather(int i, boolean b) {
        if (i == 1) {
            MainClass.narrate(I18n.get("minecraft_access.weather.clear"), b);
        }
        if (i == 2) {
            MainClass.narrate(I18n.get("minecraft_access.weather.rain"), b);
        }
        if (i == 3) {
            MainClass.narrate(I18n.get("minecraft_access.weather.thunder"), b);
        }
    }

}
