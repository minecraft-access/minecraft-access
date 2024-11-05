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
 * Adds a key bind to narrate/speak the player's non potion related statuses.<br>
 * - Speak Player Status Key (default: R) = Speaks the health and hunger.<br>
 */
@Slf4j
public class PlayerStatus {
    IntervalKeystroke narrationKey = new IntervalKeystroke(
            () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().healthNHungerNarrationKey),
            Keystroke.TriggeredAt.PRESSED,
            // 3s interval
            Interval.inMilliseconds(3000));

    public void update() {
        try {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.currentScreen != null) return;

            if (narrationKey.canBeTriggered()) {
                double health = minecraftClient.player.getHealth() / 2;
                double maxHealth = minecraftClient.player.getMaxHealth() / 2.0;
                double hunger = minecraftClient.player.getHungerManager().getFoodLevel() / 2.0;
                double maxHunger = 20.0 / 2.0;
                double armor = minecraftClient.player.getArmor();
                double air = Math.max(0, minecraftClient.player.getAir() / 30.0);
                double maxAir = minecraftClient.player.getMaxAir() / 30.0;
                double frostExposurePercent = minecraftClient.player.getFreezingScale() * 100.0;

                String toSpeak = I18n.translate("minecraft_access.player_status.base", health, maxHealth, hunger, maxHunger, armor);

                if ((minecraftClient.player.isSubmergedInWater() || minecraftClient.player.getAir() < minecraftClient.player.getMaxAir()) && !minecraftClient.player.canBreatheInWater())
                toSpeak += I18n.translate("minecraft_access.player_status.air", air, maxAir);

                if ((minecraftClient.player.inPowderSnow || frostExposurePercent > 0) && minecraftClient.player.canFreeze())
                    toSpeak += I18n.translate("minecraft_access.player_status.frost", frostExposurePercent);
            //toSpeak += I18n.translate("minecraft_access.player_status.frost", minecraftClient.player.getFrozenTicks(), minecraftClient.player.getMinFreezeDamageTicks());
            //toSpeak += " scale is " + minecraftClient.player.getFreezingScale();

                MainClass.speakWithNarrator(toSpeak, true);
            }
            narrationKey.updateStateForNextTick();

        } catch (Exception e) {
                log.error("An error occurred in         PlayerStatus.", e);
        }
    }
}
