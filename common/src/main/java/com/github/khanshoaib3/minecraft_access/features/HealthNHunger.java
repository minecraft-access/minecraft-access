package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.condition.Interval;
import com.github.khanshoaib3.minecraft_access.utils.condition.IntervalKeystroke;
import com.github.khanshoaib3.minecraft_access.utils.condition.Keystroke;
import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

/**
 * Adds a key bind to narrate/speak the player's health and hunger.<br>
 * - Speak Player Health and Hunger Key (default: R) = Speaks the health and hunger.<br>
 */
@Slf4j
public class HealthNHunger {
    IntervalKeystroke narrationKey = new IntervalKeystroke(
            () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().healthNHungerNarrationKey),
            Keystroke.TriggeredAt.PRESSED,
            // 3s interval
            Interval.ms(3000));

    public void update() {
        try {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.currentScreen != null) return;

            if (narrationKey.canBeTriggered()) {
                double health = minecraftClient.player.getHealth();
                double hunger = minecraftClient.player.getHungerManager().getFoodLevel();
                health = (double) Math.round((health / 2) * 10) / 10;
                hunger = (double) Math.round((hunger / 2) * 10) / 10;
                String toSpeak = I18n.translate("minecraft_access.healthHunger.format", health, hunger);
                MainClass.speakWithNarrator(toSpeak, true);
            }
            narrationKey.updateStateForNextTick();

        } catch (Exception e) {
            log.error("An error occurred in HealthNHunger.", e);
        }
    }
}
