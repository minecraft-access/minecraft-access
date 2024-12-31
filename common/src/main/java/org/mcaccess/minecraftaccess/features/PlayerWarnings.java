package org.mcaccess.minecraftaccess.features;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundEvents;

/**
 * Warns the player when the health, hunger or food reaches below a certain threshold.
 */
@Slf4j
public class PlayerWarnings {
    private MinecraftClient minecraftClient;

    private boolean isHealthBelowFirstThreshold;
    private boolean isHealthBelowSecondThreshold;
    private boolean isFoodBelowThreshold;
    private boolean isAirBelowThreshold;
    private boolean isFrostAboveThreshold;

    private static final Config.PlayerWarnings config = Config.getInstance().playerWarnings;

    public void update() {
        minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.currentScreen != null) return;

        double maxHealth = Math.round((minecraftClient.player.getMaxHealth() / 2.0) * 10.0) / 10.0;
        double maxHunger = Math.round((20 / 2.0) * 10.0) / 10.0;
        double maxAir = Math.round((minecraftClient.player.getMaxAir() / 20.0) * 10.0) / 10.0;
        double frostExposurePercent = Math.round((minecraftClient.player.getFreezingScale() * 100.0) * 10.0) / 10.0;

        healthWarning(PlayerUtils.getHearts(), maxHealth);
        if (!minecraftClient.player.isCreative()) {
            hungerWarning(PlayerUtils.getHunger(), maxHunger);
            airWarning(Math.round((minecraftClient.player.getAir() / 20.0) * 10.0) / 10.0, maxAir);
            frostWarning(frostExposurePercent);
        }
    }

    private void healthWarning(double health, double maxHealth) {
        if (minecraftClient.player == null) return;

        if (health <= config.firstHealthThreshold && health > config.secondHealthThreshold && !isHealthBelowFirstThreshold && !isHealthBelowSecondThreshold) {
            isHealthBelowFirstThreshold = true;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.player_warnings.health_low", health, maxHealth), true);
            if (config.playSound) minecraftClient.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }

        if (health <= config.secondHealthThreshold && health > 0 && isHealthBelowFirstThreshold && !isHealthBelowSecondThreshold) {
            isHealthBelowSecondThreshold = true;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.player_warnings.health_low", health, maxHealth), true);
            if (config.playSound) minecraftClient.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }

        if (isHealthBelowFirstThreshold && health > config.firstHealthThreshold) isHealthBelowFirstThreshold = false;
        if (isHealthBelowSecondThreshold && health > config.secondHealthThreshold) isHealthBelowSecondThreshold = false;
    }

    private void hungerWarning(double hunger, double maxHunger) {
        if (minecraftClient.player == null) return;

        if (hunger <= config.hungerThreshold && hunger > 0 && !isFoodBelowThreshold) {
            isFoodBelowThreshold = true;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.player_warnings.hunger_low", hunger, maxHunger), true);
            if (config.playSound) minecraftClient.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }

        if (isFoodBelowThreshold && hunger > config.hungerThreshold) isFoodBelowThreshold = false;
    }

    private void airWarning(double air, double maxAir) {
        air = Math.max(air, 0.0);
        if (minecraftClient.player == null) return;

        if (air <= config.airThreshold && air > 0 && !isAirBelowThreshold) {
            isAirBelowThreshold = true;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.player_warnings.air_low", air, maxAir), true);
            if (config.playSound) minecraftClient.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }

        if (isAirBelowThreshold && air > config.airThreshold) isAirBelowThreshold = false;
    }

    private void frostWarning(double frostExposurePercent) {
        if (minecraftClient.player == null) return;

        if (frostExposurePercent >= config.frostThreshold && frostExposurePercent < 100 && !isFrostAboveThreshold) {
            isFrostAboveThreshold = true;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.player_warnings.frost_low", frostExposurePercent), true);
            if (config.playSound) minecraftClient.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }

        if (isFrostAboveThreshold && frostExposurePercent < config.frostThreshold) isFrostAboveThreshold = false;
    }
}
