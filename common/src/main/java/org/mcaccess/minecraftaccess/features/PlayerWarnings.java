package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

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

    private static final Config.PlayerWarnings CONFIG = Config.getInstance().playerWarnings;

    public void tick() {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen != null) return;
        player = minecraftClient.player;

        double maxHealth = Math.round((player.getMaxHealth() / 2.0) * 10.0) / 10.0;
        double maxHunger = Math.round((20 / 2.0) * 10.0) / 10.0;
        double maxAir = Math.round((player.getMaxAirSupply() / 20.0) * 10.0) / 10.0;
        double frostExposurePercent = Math.round((player.getPercentFrozen() * 100.0) * 10.0) / 10.0;

        healthWarning(player.getHealth()/2, maxHealth);
        hungerWarning(player.getFoodData().getFoodLevel()/2, maxHunger);
        airWarning(Math.round((player.getAirSupply() / 20.0) * 10.0) / 10.0, maxAir);
        frostWarning(frostExposurePercent);
    }

    private void healthWarning(double health, double maxHealth) {
        if (health <= CONFIG.firstHealthThreshold && health > CONFIG.secondHealthThreshold && !isHealthBelowFirstThreshold && !isHealthBelowSecondThreshold) {
            isHealthBelowFirstThreshold = true;
            MainClass.narrate(I18n.get("minecraft_access.player_warnings.health_low", NarrationUtils.narrateNumber(health), NarrationUtils.narrateNumber(maxHealth)), true);
            playWarningSound();
        }

        if (health <= CONFIG.secondHealthThreshold && health > 0 && isHealthBelowFirstThreshold && !isHealthBelowSecondThreshold) {
            isHealthBelowSecondThreshold = true;
            MainClass.narrate(I18n.get("minecraft_access.player_warnings.health_low", NarrationUtils.narrateNumber(health), NarrationUtils.narrateNumber(maxHealth)), true);
            playWarningSound();
        }

        if (isHealthBelowFirstThreshold && health > CONFIG.firstHealthThreshold) isHealthBelowFirstThreshold = false;
        if (isHealthBelowSecondThreshold && health > CONFIG.secondHealthThreshold) isHealthBelowSecondThreshold = false;
    }

    private void hungerWarning(double hunger, double maxHunger) {
        if (hunger <= CONFIG.hungerThreshold && hunger > 0 && !isFoodBelowThreshold) {
            isFoodBelowThreshold = true;
            MainClass.narrate(I18n.get("minecraft_access.player_warnings.hunger_low", NarrationUtils.narrateNumber(hunger), NarrationUtils.narrateNumber(maxHunger)), true);
            playWarningSound();
        }

        if (isFoodBelowThreshold && hunger > CONFIG.hungerThreshold) isFoodBelowThreshold = false;
    }

    private void airWarning(double air, double maxAir) {
        air = Math.max(air, 0.0);
        if (air <= CONFIG.airThreshold && air > 0 && !isAirBelowThreshold) {
            isAirBelowThreshold = true;
            MainClass.narrate(I18n.get("minecraft_access.player_warnings.air_low", NarrationUtils.narrateNumber(air), NarrationUtils.narrateNumber(maxAir)), true);
            playWarningSound();
        }

        if (isAirBelowThreshold && air > CONFIG.airThreshold) isAirBelowThreshold = false;
    }

    private void frostWarning(double frostExposurePercent) {
        if (frostExposurePercent >= CONFIG.frostThreshold && frostExposurePercent < 100 && !isFrostAboveThreshold) {
            isFrostAboveThreshold = true;
            MainClass.narrate(I18n.get("minecraft_access.player_warnings.frost_low", NarrationUtils.narrateNumber(frostExposurePercent)), true);
            playWarningSound();
        }

        if (isFrostAboveThreshold && frostExposurePercent < CONFIG.frostThreshold) isFrostAboveThreshold = false;
    }

    private void playWarningSound() {
        if (CONFIG.playSound) {
            player.playNotifySound(SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }
}
