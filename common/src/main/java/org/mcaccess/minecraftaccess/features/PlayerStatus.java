package org.mcaccess.minecraftaccess.features;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.condition.IntervalKeystroke;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;

/**
 * Adds a key bind to narrate/speak the player's non potion related statuses.<br>
 * - Speak Player Status Key (default: R) = Speaks the health and hunger.<br>
 */
@Slf4j
public class PlayerStatus {
    IntervalKeystroke narrationKey = new IntervalKeystroke(
            () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().speakPlayerStatusKey),
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
                double health = Math.round((minecraftClient.player.getHealth() / 2.0) * 10.0) / 10.0;
                double maxHealth = Math.round((minecraftClient.player.getMaxHealth() / 2.0) * 10.0) / 10.0;
                double absorption = Math.round((minecraftClient.player.getAbsorptionAmount() / 2.0) * 10.0) / 10.0;
                double hunger = Math.round((minecraftClient.player.getHungerManager().getFoodLevel() / 2.0) * 10.0) / 10.0;
                double maxHunger = Math.round((20 / 2.0) * 10.0) / 10.0;
                double armor = Math.round((minecraftClient.player.getArmor() / 2.0) * 10.0) / 10.0;
                double air = Math.round((minecraftClient.player.getAir() / 20.0) * 10.0) / 10.0;
                double maxAir = Math.round((minecraftClient.player.getMaxAir() / 20.0) * 10.0) / 10.0;
                double frostExposurePercent = Math.round((minecraftClient.player.getFreezingScale() * 100.0) * 10.0) / 10.0;

                boolean isStatusKeyPressed = KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().speakPlayerStatusKey);

                String toSpeak = "";

                if (!(isStatusKeyPressed && Screen.hasAltDown())) {
                    if (absorption > 0) {
                        toSpeak += I18n.translate("minecraft_access.player_status.base_with_absorption", health, absorption, maxHealth, hunger, maxHunger, armor);
                    } else {
                        toSpeak += I18n.translate("minecraft_access.player_status.base", health, maxHealth, hunger, maxHunger, armor);
                    }
                }

                if (Screen.hasControlDown()) {
                    EffectNarration.getInstance().narrateCurrentPlayerEffects();
                    return;
                }

                if ((minecraftClient.player.isSubmergedInWater() || minecraftClient.player.getAir() < minecraftClient.player.getMaxAir()) && !minecraftClient.player.canBreatheInWater()) {
                    air = Math.max(air, 0.0);
                    toSpeak += I18n.translate("minecraft_access.player_status.air", air, maxAir);
                }

                if ((minecraftClient.player.inPowderSnow || frostExposurePercent > 0) && minecraftClient.player.canFreeze())
                    toSpeak += I18n.translate("minecraft_access.player_status.frost", frostExposurePercent);

                if (toSpeak.length() == 0)
                    toSpeak += I18n.translate("minecraft_access.player_status.no_conditional_status");

                MainClass.speakWithNarrator(toSpeak, true);
            }
            narrationKey.updateStateForNextTick();
        } catch (Exception e) {
            log.error("An error occurred in PlayerStatus.", e);
        }
    }
}
