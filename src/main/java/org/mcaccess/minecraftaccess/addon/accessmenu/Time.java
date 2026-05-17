package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.features.TimeIndicator;
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

        double daytime = TimeIndicator.getCurrentTime();
        int hours = (int) daytime;
        int minutes = (int) ((daytime % 1.0) * 60);

        new Translation("minecraft_access.access_menu.time_of_day")
                .variant(hours >= 12 ? "pm" : "am", Config.getInstance().use12HourTimeFormat)
                .variable("hours").put(Config.getInstance().use12HourTimeFormat ? (hours + 11) % 12 + 1 : hours)
                .variable("minutes").put(minutes)
                .narrate(true);
    }
}
