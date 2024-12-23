package org.mcaccess.minecraftaccess.features;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.config.config_maps.PlayerWarningConfigMap;
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
    private  boolean isFrostAboveThreshold;

    private boolean playSound;
    private double firstHealthThreshold;
    private double secondHealthThreshold;
    private double hungerThreshold;
    private double airThreshold;
    private double frostThreshold;

    public PlayerWarnings() {
        isHealthBelowFirstThreshold = false;
        isHealthBelowSecondThreshold = false;
        isFoodBelowThreshold = false;
        isAirBelowThreshold = false;
        isFrostAboveThreshold = false;

        loadConfigurations();
    }

    public void update() {
        try {
            minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.currentScreen != null) return;

            loadConfigurations();

            double maxHealth = Math.round((minecraftClient.player.getMaxHealth() / 2.0) * 10.0) / 10.0;
            double maxHunger = Math.round((20 / 2.0) * 10.0) / 10.0;
            double maxAir = Math.round((minecraftClient.player.getMaxAir() / 20.0) * 10.0) / 10.0;
            double frostExposurePercent = Math.round((minecraftClient.player.getFreezingScale() * 100.0) * 10.0) / 10.0;

            healthWarning(PlayerUtils.getHearts(), maxHealth);
            hungerWarning(PlayerUtils.getHunger(), maxHunger);
            airWarning(PlayerUtils.getAir(), maxAir);
            frostWarning(frostExposurePercent);
        } catch (Exception e) {
            log.error("An error occurred in PlayerWarnings.", e);
        }
    }

    private void loadConfigurations() {
        PlayerWarningConfigMap map = PlayerWarningConfigMap.getInstance();
        this.playSound = map.isPlaySound();
        this.firstHealthThreshold = map.getFirstHealthThreshold();
        this.secondHealthThreshold = map.getSecondHealthThreshold();
        this.hungerThreshold = map.getHungerThreshold();
        this.airThreshold = map.getAirThreshold();
        this.frostThreshold = map.getFrostThreshold();
    }

    private void healthWarning(double health, double maxHealth) {
        if (minecraftClient.player == null) return;

        if (health <= firstHealthThreshold && health > secondHealthThreshold && !isHealthBelowFirstThreshold && !isHealthBelowSecondThreshold) {
            isHealthBelowFirstThreshold = true;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.player_warnings.health_low", health, maxHealth), true);
            if (playSound) minecraftClient.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }

        if (health <= secondHealthThreshold && health > 0 && isHealthBelowFirstThreshold && !isHealthBelowSecondThreshold) {
            isHealthBelowSecondThreshold = true;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.player_warnings.health_low", health, maxHealth), true);
            if (playSound) minecraftClient.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }

        if (isHealthBelowFirstThreshold && health > firstHealthThreshold) isHealthBelowFirstThreshold = false;
        if (isHealthBelowSecondThreshold && health > secondHealthThreshold) isHealthBelowSecondThreshold = false;
    }

    private void hungerWarning(double hunger, double maxHunger) {
        if (minecraftClient.player == null) return;

        if (hunger <= hungerThreshold && hunger > 0 && !isFoodBelowThreshold) {
            isFoodBelowThreshold = true;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.player_warnings.hunger_low", hunger, maxHunger), true);
            if (playSound) minecraftClient.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }

        if (isFoodBelowThreshold && hunger > hungerThreshold) isFoodBelowThreshold = false;
    }

    private void airWarning(double air, double maxAir) {
        air = Math.max(air, 0.0);
        if (minecraftClient.player == null) return;

        if (air <= airThreshold && air > 0 && !isAirBelowThreshold) {
            isAirBelowThreshold = true;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.player_warnings.air_low", air, maxAir), true);
            if (playSound) minecraftClient.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }

        if (isAirBelowThreshold && air > airThreshold) isAirBelowThreshold = false;
    }

    private void frostWarning(double frostExposurePercent) {
        if (minecraftClient.player == null) return;

        if (frostExposurePercent >= frostThreshold && frostExposurePercent < 100 && !isFrostAboveThreshold) {
            isFrostAboveThreshold = true;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.player_warnings.frost_low", frostExposurePercent), true);
            if (playSound) minecraftClient.player.playSound(SoundEvents.BLOCK_ANVIL_LAND, 1.0f, 1.0f);
        }

        if (isFrostAboveThreshold && frostExposurePercent < frostThreshold) isFrostAboveThreshold = false;
    }
}
