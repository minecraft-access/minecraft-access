package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.language.I18n;

import org.mcaccess.minecraftaccess.ClientConfig;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.features.TimeIndicator;

public class Time implements AccessMenuFunction {
    @Override
    public void execute() {
        ClientLevel level = Minecraft.getInstance().level;
        assert level != null;
        if (level.dimensionType().hasFixedTime()) {
            MainClass.narrate(I18n.get(level.dimension().identifier().toLanguageKey("dimension")), true);
            return;
        }

        double daytime = TimeIndicator.getCurrentTime();
        int hours = (int) daytime;
        int minutes = (int) ((daytime % 1.0) * 60);

        StringBuilder translationKey = new StringBuilder("minecraft_access.access_menu.time_of_day");
        if (ClientConfig.getInstance().general.use12HourTimeFormat) {
            if (hours == 0) {
                hours = 12;
                translationKey.append("_am");
            } else if (hours > 12) {
                hours -= 12;
                translationKey.append("_pm");
            } else if (hours == 12) {
                translationKey.append("_pm");
            } else {
                translationKey.append("_am");
            }
        }

        String narration = I18n.get(translationKey.toString(), String.format("%02d:%02d", hours, minutes));
        MainClass.narrate(narration, true);
    }
}
