package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;

public class GetTime implements AccessMenuFunction {
    @Override
    public void execute() {
        assert Minecraft.getInstance().player != null;
        long daytime = Minecraft.getInstance().player.level().getDayTime() + 6000;
        int hours = (int) (daytime / 1000) % 24;
        int minutes = (int) ((daytime % 1000) * 60 / 1000);

        StringBuilder translationKey = new StringBuilder("minecraft_access.access_menu.time_of_day");
        if (Config.getInstance().use12HourTimeFormat) {
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
