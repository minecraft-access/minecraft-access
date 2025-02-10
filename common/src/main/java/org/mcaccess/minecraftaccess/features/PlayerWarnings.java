package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.config.config_maps.PlayerWarningConfigMap;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;

/**
 * Warns the player when the health, hunger or food reaches below a certain threshold.
 */
@Slf4j
public class PlayerWarnings {
    private Minecraft minecraftClient;

    private boolean isHealthBelowFirstThreshold;
    private boolean isHealthBelowSecondThreshold;
    private boolean isFoodBelowThreshold;
    private boolean isAirBelowThreshold;
    private boolean isFrostAboveThreshold;

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
        minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen != null) return;

        loadConfigurations();

        double maxHealth = Math.round((minecraftClient.player.getMaxHealth() / 2.0) * 10.0) / 10.0;
        double maxHunger = Math.round((20 / 2.0) * 10.0) / 10.0;
        double maxAir = Math.round((minecraftClient.player.getMaxAirSupply() / 20.0) * 10.0) / 10.0;
        double frostExposurePercent = Math.round((minecraftClient.player.getPercentFrozen() * 100.0) * 10.0) / 10.0;

        healthWarning(PlayerUtils.getHearts(), maxHealth);
        if (!minecraftClient.player.isCreative()) {
            hungerWarning(PlayerUtils.getHunger(), maxHunger);
            airWarning(Math.round((minecraftClient.player.getAirSupply() / 20.0) * 10.0) / 10.0, maxAir);
            frostWarning(frostExposurePercent);
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
            MainClass.speakWithNarrator(I18n.get("minecraft_access.player_warnings.health_low", health, maxHealth), true);
            playWarningSound();
        }

        if (health <= secondHealthThreshold && health > 0 && isHealthBelowFirstThreshold && !isHealthBelowSecondThreshold) {
            isHealthBelowSecondThreshold = true;
            MainClass.speakWithNarrator(I18n.get("minecraft_access.player_warnings.health_low", health, maxHealth), true);
            playWarningSound();
        }

        if (isHealthBelowFirstThreshold && health > firstHealthThreshold) isHealthBelowFirstThreshold = false;
        if (isHealthBelowSecondThreshold && health > secondHealthThreshold) isHealthBelowSecondThreshold = false;
    }

    private void hungerWarning(double hunger, double maxHunger) {
        if (minecraftClient.player == null) return;

        if (hunger <= hungerThreshold && hunger > 0 && !isFoodBelowThreshold) {
            isFoodBelowThreshold = true;
            MainClass.speakWithNarrator(I18n.get("minecraft_access.player_warnings.hunger_low", hunger, maxHunger), true);
            playWarningSound();
        }

        if (isFoodBelowThreshold && hunger > hungerThreshold) isFoodBelowThreshold = false;
    }

    private void airWarning(double air, double maxAir) {
        air = Math.max(air, 0.0);
        if (minecraftClient.player == null) return;

        if (air <= airThreshold && air > 0 && !isAirBelowThreshold) {
            isAirBelowThreshold = true;
            MainClass.speakWithNarrator(I18n.get("minecraft_access.player_warnings.air_low", air, maxAir), true);
            playWarningSound();
        }

        if (isAirBelowThreshold && air > airThreshold) isAirBelowThreshold = false;
    }

    private void frostWarning(double frostExposurePercent) {
        if (minecraftClient.player == null) return;

        if (frostExposurePercent >= frostThreshold && frostExposurePercent < 100 && !isFrostAboveThreshold) {
            isFrostAboveThreshold = true;
            MainClass.speakWithNarrator(I18n.get("minecraft_access.player_warnings.frost_low", frostExposurePercent), true);
            playWarningSound();
        }

        if (isFrostAboveThreshold && frostExposurePercent < frostThreshold) isFrostAboveThreshold = false;
    }

    private void playWarningSound() {
        if (playSound)
            minecraftClient.player.playNotifySound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
    }
}
