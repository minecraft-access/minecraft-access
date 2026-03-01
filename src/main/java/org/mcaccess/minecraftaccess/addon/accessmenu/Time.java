package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class Time implements AccessMenuFunction {
    @Override
    public void execute() {
        ClientLevel level = Minecraft.getInstance().level;
        assert level != null;
        if (level.dimensionType().hasFixedTime()) {
            new Translation("dimension", level.dimension())
                    .narrate(true);
            return;
        }

        long daytime = level.getGameTime() + 6000;
        int hours = (int) (daytime / 1000) % 24;
        int minutes = (int) ((daytime % 1000) * 60 / 1000);

        new Translation("minecraft_access.access_menu.time_of_day")
                .variant(hours >= 12 ? "pm" : "am", Config.getInstance().use12HourTimeFormat)
                .variable("hours").put(Config.getInstance().use12HourTimeFormat ? (hours + 11) % 12 + 1 : hours)
                .variable("minutes").put(minutes)
                .narrate(true);
    }
}
