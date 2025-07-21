package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.condition.IntervalKeystroke;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

/**
 * Adds a key bind to narrate the player's non potion related statuses.<br>
 * - Narrate Player Status Key (default: R) = Narrates the health and hunger.<br>
 */
@Slf4j
public class PlayerStatus {
    IntervalKeystroke narrationKey = new IntervalKeystroke(
            () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().narratePlayerStatusKey),
            Keystroke.TriggeredAt.PRESSED,
            // 3s interval
            Interval.ms(3000));

    public void tick() {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen != null) return;

        if (narrationKey.canBeTriggered()) {
            if (Screen.hasControlDown()) {
                PlayerUtils.narrateCurrentPlayerEffects();
                return;
            }

            double health = Math.round((minecraftClient.player.getHealth() / 2.0) * 10.0) / 10.0;
            double maxHealth = Math.round((minecraftClient.player.getMaxHealth() / 2.0) * 10.0) / 10.0;
            double absorption = Math.round((minecraftClient.player.getAbsorptionAmount() / 2.0) * 10.0) / 10.0;
            double hunger = Math.round((minecraftClient.player.getFoodData().getFoodLevel() / 2.0) * 10.0) / 10.0;
            double maxHunger = Math.round((20 / 2.0) * 10.0) / 10.0;
            double armor = Math.round((minecraftClient.player.getArmorValue() / 2.0) * 10.0) / 10.0;
            double air = Math.round((minecraftClient.player.getAirSupply() / 20.0) * 10.0) / 10.0;
            double maxAir = Math.round((minecraftClient.player.getMaxAirSupply() / 20.0) * 10.0) / 10.0;
            double frostExposurePercent = Math.round((minecraftClient.player.getPercentFrozen() * 100.0) * 10.0) / 10.0;

            boolean isStatusKeyPressed = KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().narratePlayerStatusKey);

            String narration = "";

            if (PlayerUtils.isSurvival() || PlayerUtils.isAdventure()) {
                if (!Screen.hasAltDown()) {
                    if (absorption > 0) {
                        narration += I18n.get("minecraft_access.player_status.base_with_absorption", NarrationUtils.narrateNumber(health), NarrationUtils.narrateNumber(absorption), NarrationUtils.narrateNumber(maxHealth), NarrationUtils.narrateNumber(hunger), NarrationUtils.narrateNumber(maxHunger), NarrationUtils.narrateNumber(armor));
                    } else {
                        narration += I18n.get("minecraft_access.player_status.base", NarrationUtils.narrateNumber(health), NarrationUtils.narrateNumber(maxHealth), NarrationUtils.narrateNumber(hunger), NarrationUtils.narrateNumber(maxHunger), NarrationUtils.narrateNumber(armor));
                    }
                }

                if ((minecraftClient.player.isUnderWater() || minecraftClient.player.getAirSupply() < minecraftClient.player.getMaxAirSupply()) && !minecraftClient.player.canBreatheUnderwater()) {
                    air = Math.max(air, 0.0);
                    narration += I18n.get("minecraft_access.player_status.air", NarrationUtils.narrateNumber(air), NarrationUtils.narrateNumber(maxAir));
                }

                if ((minecraftClient.player.isInPowderSnow || frostExposurePercent > 0) && minecraftClient.player.canFreeze())
                    narration += I18n.get("minecraft_access.player_status.frost", NarrationUtils.narrateNumber(frostExposurePercent));
            }

            if (narration.isEmpty() && (PlayerUtils.isSurvival() || PlayerUtils.isAdventure()))
                narration += I18n.get("minecraft_access.player_status.no_conditional_status");

            if (!narration.equals(I18n.get("minecraft_access.player_status.no_conditional_status")))
                narration = addGameMode(narration);

            MainClass.narrate(narration, true);
        }
        narrationKey.updateStateForNextTick();
    }

    public String addGameMode(String narration) {
        if (!narration.isEmpty())
            narration += I18n.get("minecraft_access.other.words_connection");

        narration += switch (Minecraft.getInstance().gameMode.getPlayerMode()) {
            case SURVIVAL -> PlayerUtils.isHardCore() ? I18n.get("gameMode.hardcore") : I18n.get("gameMode.survival");
            case CREATIVE -> I18n.get("gameMode.creative");
            case SPECTATOR -> I18n.get("gameMode.spectator");
            case ADVENTURE -> I18n.get("gameMode.adventure");
        };

        //  If the player is in a hard core world but a different game mode
        if (PlayerUtils.isHardCore() && !PlayerUtils.isSurvival()) {
            narration += " " + I18n.get("options.difficulty.hardcore");
        }

        return narration;
    }
}
