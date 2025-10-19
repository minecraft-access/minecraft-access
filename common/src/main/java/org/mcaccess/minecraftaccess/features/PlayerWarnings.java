package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

/**
 * Warns the player when the health, hunger or food reaches below a certain threshold.
 */
@Slf4j
public class PlayerWarnings {
    private final Minecraft client = Minecraft.getInstance();
    private LocalPlayer player;

    private boolean isHealthBelowFirstThreshold;
    private boolean isHealthBelowSecondThreshold;

    private boolean isFoodBelowThreshold;
    private boolean isAirBelowThreshold;
    private boolean isFrostAboveThreshold;

    private DurabilityWarningStatus lastMainHandStatus = DurabilityWarningStatus.NONE;
    private DurabilityWarningStatus lastOffHandStatus = DurabilityWarningStatus.NONE;

    private DurabilityWarningStatus lastHelmitStatus = DurabilityWarningStatus.NONE;
    private DurabilityWarningStatus lastChestplateStatus = DurabilityWarningStatus.NONE;
    private DurabilityWarningStatus lastLeggingsStatus = DurabilityWarningStatus.NONE;
    private DurabilityWarningStatus lastBootsStatus = DurabilityWarningStatus.NONE;

    private static final Config.PlayerWarnings CONFIG = Config.getInstance().playerWarnings;

    public void tick() {
        player = client.player;

        healthWarning();
        hungerWarning();
        airWarning();
        frostWarning();
    }

    private void healthWarning() {
        double health = player.getHealth() / 2;
        double maxHealth = Math.round((player.getMaxHealth() / 2.0) * 10.0) / 10.0;

        if (health <= CONFIG.firstHealthThreshold && health > CONFIG.secondHealthThreshold && !isHealthBelowFirstThreshold && !isHealthBelowSecondThreshold) {
            isHealthBelowFirstThreshold = true;
            MainClass.narrate(I18n.get("minecraft_access.player_warnings.health_low", NarrationUtils.narrateNumber(health), NarrationUtils.narrateNumber(maxHealth)), true);
            playWarningSound(false);
        }

        if (health <= CONFIG.secondHealthThreshold && health > 0 && isHealthBelowFirstThreshold && !isHealthBelowSecondThreshold) {
            isHealthBelowSecondThreshold = true;
            MainClass.narrate(I18n.get("minecraft_access.player_warnings.health_low", NarrationUtils.narrateNumber(health), NarrationUtils.narrateNumber(maxHealth)), true);
            playWarningSound(true);
        }

        if (isHealthBelowFirstThreshold && health > CONFIG.firstHealthThreshold) isHealthBelowFirstThreshold = false;
        if (isHealthBelowSecondThreshold && health > CONFIG.secondHealthThreshold) isHealthBelowSecondThreshold = false;
    }

    private void hungerWarning() {
        double hunger = player.getFoodData().getFoodLevel() / 2;
        double maxHunger = Math.round((20 / 2.0) * 10.0) / 10.0;

        if (hunger <= CONFIG.hungerThreshold && hunger > 0 && !isFoodBelowThreshold) {
            isFoodBelowThreshold = true;
            MainClass.narrate(I18n.get("minecraft_access.player_warnings.hunger_low", NarrationUtils.narrateNumber(hunger), NarrationUtils.narrateNumber(maxHunger)), true);
            playWarningSound(false);
        }

        if (isFoodBelowThreshold && hunger > CONFIG.hungerThreshold) isFoodBelowThreshold = false;
    }

    private void airWarning() {
        double air = Math.max(player.getAirSupply() / 20.0, 0.0);
        double maxAir = Math.round((player.getMaxAirSupply() / 20.0) * 10.0) / 10.0;
        if (air <= CONFIG.airThreshold && air > 0 && !isAirBelowThreshold) {

            isAirBelowThreshold = true;
            MainClass.narrate(I18n.get("minecraft_access.player_warnings.air_low", NarrationUtils.narrateNumber(air), NarrationUtils.narrateNumber(maxAir)), true);
            playWarningSound(false);
        }

        if (isAirBelowThreshold && air > CONFIG.airThreshold) isAirBelowThreshold = false;
    }

    private void frostWarning() {
        double frostExposurePercent = Math.round((player.getPercentFrozen() * 100.0) * 10.0) / 10.0;

        if (frostExposurePercent >= CONFIG.frostThreshold && frostExposurePercent < 100 && !isFrostAboveThreshold) {
            isFrostAboveThreshold = true;
            MainClass.narrate(I18n.get("minecraft_access.player_warnings.frost_low", NarrationUtils.narrateNumber(frostExposurePercent)), true);
            playWarningSound(false);
        }

        if (isFrostAboveThreshold && frostExposurePercent < CONFIG.frostThreshold) isFrostAboveThreshold = false;
    }

    private void playWarningSound(boolean severe) {
        if (CONFIG.playSound) {
            SoundEvent soundToPlay = severe ? SoundEvents.ANVIL_PLACE : SoundEvents.RESPAWN_ANCHOR_DEPLETE.value();
            player.playNotifySound(soundToPlay, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
    }

    private void playWarningSound(DurabilityWarningStatus status) {
        playWarningSound(status.ordinal() > DurabilityWarningStatus.FIRST.ordinal());
    }

    public void durabilityWarnings() {
        if (player == null) return;

        if (CONFIG.durabilityWarnings.enableHeldItems) {
            DurabilityWarningStatus mainHandStatus = checkDurabilityLevel(player.getMainHandItem());
            DurabilityWarningStatus offHandStatus = checkDurabilityLevel(player.getOffhandItem());

            if (mainHandStatus.ordinal() > lastMainHandStatus.ordinal()) {
                playWarningSound(mainHandStatus);
            }

            if (offHandStatus.ordinal() > lastOffHandStatus.ordinal()) {
                playWarningSound(offHandStatus);
            }

            lastMainHandStatus = mainHandStatus;
            lastOffHandStatus = offHandStatus;
        }

        if (CONFIG.durabilityWarnings.enableWornArmor) {
            Inventory inventory = player.getInventory();

            //DurabilityWarningStatus helmitStatus = checkDurabilityLevel();
            //DurabilityWarningStatus chestplateStatus = checkDurabilityLevel();
            //DurabilityWarningStatus leggingsStatus = checkDurabilityLevel();
            //DurabilityWarningStatus bootsStatus = checkDurabilityLevel();
        }
    }

    private DurabilityWarningStatus checkDurabilityLevel(ItemStack itemStack) {
        if (itemStack == null || !itemStack.isDamageableItem() || !itemStack.isDamaged()) return DurabilityWarningStatus.NONE;

        int durability = itemStack.getMaxDamage() - itemStack.getDamageValue();

        if (itemStack.nextDamageWillBreak()) return DurabilityWarningStatus.NEXT_WILL_BREAK;
        if (durability <= CONFIG.durabilityWarnings.secondThreshold) return DurabilityWarningStatus.SECOND;
        if (durability <= CONFIG.durabilityWarnings.firstThreshold) return DurabilityWarningStatus.FIRST;

        return DurabilityWarningStatus.NONE;
    }

    private enum DurabilityWarningStatus {
        NONE,
        FIRST,
        SECOND,
        NEXT_WILL_BREAK
    }
}
