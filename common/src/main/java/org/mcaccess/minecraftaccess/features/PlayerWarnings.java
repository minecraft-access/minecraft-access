package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;

/**
 * Warns the player when the health, hunger or food reaches below a certain threshold.
 */
@Slf4j
public class PlayerWarnings {
    private LocalPlayer player;

    private boolean isHealthBelowFirstThreshold;
    private boolean isHealthBelowSecondThreshold;
    private boolean isFoodBelowThreshold;
    private boolean isAirBelowThreshold;
    private boolean isFrostAboveThreshold;

    private static final Config.PlayerWarnings config = Config.getInstance().playerWarnings;

    public void update() {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen != null) return;
        player = minecraftClient.player;

        double maxHealth = Math.round((player.getMaxHealth() / 2.0) * 10.0) / 10.0;
        double maxHunger = Math.round((20 / 2.0) * 10.0) / 10.0;
        double maxAir = Math.round((player.getMaxAirSupply() / 20.0) * 10.0) / 10.0;
        double frostExposurePercent = Math.round((player.getPercentFrozen() * 100.0) * 10.0) / 10.0;

        healthWarning(PlayerUtils.getHearts(), maxHealth);
        if (!player.isCreative()) {
            hungerWarning(PlayerUtils.getHunger(), maxHunger);
            airWarning(Math.round((player.getAirSupply() / 20.0) * 10.0) / 10.0, maxAir);
            frostWarning(frostExposurePercent);
        }
    }

    private void healthWarning(double health, double maxHealth) {
        if (health <= config.firstHealthThreshold && health > config.secondHealthThreshold && !isHealthBelowFirstThreshold && !isHealthBelowSecondThreshold) {
            isHealthBelowFirstThreshold = true;
            MainClass.speakWithNarrator(I18n.get("minecraft_access.player_warnings.health_low", health, maxHealth), true);
            if (config.playSound) player.playSound(SoundEvents.ANVIL_LAND, 1.0f, 1.0f);
            playWarningSound();
        }

        if (health <= config.secondHealthThreshold && health > 0 && isHealthBelowFirstThreshold && !isHealthBelowSecondThreshold) {
            isHealthBelowSecondThreshold = true;
            MainClass.speakWithNarrator(I18n.get("minecraft_access.player_warnings.health_low", health, maxHealth), true);
            playWarningSound();
        }

        if (isHealthBelowFirstThreshold && health > config.firstHealthThreshold) isHealthBelowFirstThreshold = false;
        if (isHealthBelowSecondThreshold && health > config.secondHealthThreshold) isHealthBelowSecondThreshold = false;
    }

    private void hungerWarning(double hunger, double maxHunger) {
        if (hunger <= config.hungerThreshold && hunger > 0 && !isFoodBelowThreshold) {
            isFoodBelowThreshold = true;
            MainClass.speakWithNarrator(I18n.get("minecraft_access.player_warnings.hunger_low", hunger, maxHunger), true);
            playWarningSound();
        }

        if (isFoodBelowThreshold && hunger > config.hungerThreshold) isFoodBelowThreshold = false;
    }

    private void airWarning(double air, double maxAir) {
        air = Math.max(air, 0.0);
        if (air <= config.airThreshold && air > 0 && !isAirBelowThreshold) {
            isAirBelowThreshold = true;
            MainClass.speakWithNarrator(I18n.get("minecraft_access.player_warnings.air_low", air, maxAir), true);
            playWarningSound();
        }

        if (isAirBelowThreshold && air > config.airThreshold) isAirBelowThreshold = false;
    }

    private void frostWarning(double frostExposurePercent) {
        if (frostExposurePercent >= config.frostThreshold && frostExposurePercent < 100 && !isFrostAboveThreshold) {
            isFrostAboveThreshold = true;
            MainClass.speakWithNarrator(I18n.get("minecraft_access.player_warnings.frost_low", frostExposurePercent), true);
            playWarningSound();
        }

        if (isFrostAboveThreshold && frostExposurePercent < config.frostThreshold) isFrostAboveThreshold = false;
    }

    private void playWarningSound() {
        if (config.playSound) {
            player.playNotifySound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }
}
