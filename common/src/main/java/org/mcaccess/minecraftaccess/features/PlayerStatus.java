package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;

/**
 * Adds a key bind to narrate the player's non potion related statuses.<br>
 * - Narrate Player Status Key (default: R) = Narrates the health and hunger.<br>
 */
@Slf4j
public class PlayerStatus {
    private boolean isStatusKeyDown = false;

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        if (client.screen != null) return;

        if (KeyBindingsHandler.NARRATE_PLAYER_STATUS_KEY.mapping.consumeClick()) {
            if (!isStatusKeyDown) {
                isStatusKeyDown = true;
                if (Screen.hasControlDown()) {
                    PlayerUtils.narrateCurrentPlayerEffects();
                    return;
                }

                double health = Math.round((client.player.getHealth() / 2.0) * 10.0) / 10.0;
                double maxHealth = Math.round((client.player.getMaxHealth() / 2.0) * 10.0) / 10.0;
                double absorption = Math.round((client.player.getAbsorptionAmount() / 2.0) * 10.0) / 10.0;
                double hunger = Math.round((client.player.getFoodData().getFoodLevel() / 2.0) * 10.0) / 10.0;
                double maxHunger = Math.round((20 / 2.0) * 10.0) / 10.0;
                double armor = Math.round((client.player.getArmorValue() / 2.0) * 10.0) / 10.0;
                double air = Math.round((client.player.getAirSupply() / 20.0) * 10.0) / 10.0;
                double maxAir = Math.round((client.player.getMaxAirSupply() / 20.0) * 10.0) / 10.0;
                double frostExposurePercent = Math.round((client.player.getPercentFrozen() * 100.0) * 10.0) / 10.0;

                StringBuilder narration = new StringBuilder();

                if (PlayerUtils.isSurvival() || PlayerUtils.isAdventure()) {
                    if (!Screen.hasAltDown()) {
                        if (absorption > 0) {
                            narration.append(I18n.get(
                                    "minecraft_access.player_status.base_with_absorption",
                                    NarrationUtils.narrateNumber(health),
                                    NarrationUtils.narrateNumber(absorption),
                                    NarrationUtils.narrateNumber(maxHealth),
                                    NarrationUtils.narrateNumber(hunger),
                                    NarrationUtils.narrateNumber(maxHunger),
                                    NarrationUtils.narrateNumber(armor))
                            );
                        } else {
                            narration.append(I18n.get(
                                    "minecraft_access.player_status.base",
                                    NarrationUtils.narrateNumber(health),
                                    NarrationUtils.narrateNumber(maxHealth),
                                    NarrationUtils.narrateNumber(hunger),
                                    NarrationUtils.narrateNumber(maxHunger),
                                    NarrationUtils.narrateNumber(armor))
                            );
                        }
                    }

                    if ((client.player.isUnderWater() || client.player.getAirSupply() < client.player.getMaxAirSupply()) && !client.player.canBreatheUnderwater()) {
                        air = Math.max(air, 0.0);
                        narration.append(I18n.get("minecraft_access.player_status.air",
                                NarrationUtils.narrateNumber(air),
                                NarrationUtils.narrateNumber(maxAir)));
                    }

                    if ((client.player.isInPowderSnow || frostExposurePercent > 0) && client.player.canFreeze()) {
                        narration.append(I18n.get("minecraft_access.player_status.frost", NarrationUtils.narrateNumber(frostExposurePercent)));
                    }
                }

                if (narration.isEmpty() && (PlayerUtils.isSurvival() || PlayerUtils.isAdventure())) {
                    narration.append(I18n.get("minecraft_access.player_status.no_conditional_status"));
                }

                if (!narration.toString().equals(I18n.get("minecraft_access.player_status.no_conditional_status"))) {
                    addGameMode(narration);
                }

                MainClass.narrate(narration.toString(), true);
            }
        } else if (!KeyBindingsHandler.NARRATE_PLAYER_STATUS_KEY.mapping.isDown()) {
            isStatusKeyDown = false;
        }
    }

    public void addGameMode(StringBuilder narration) {
        if (!narration.isEmpty()) {
            narration.append(I18n.get("minecraft_access.other.words_connection"));
        }

        narration.append(switch (Minecraft.getInstance().gameMode.getPlayerMode()) {
            case SURVIVAL -> PlayerUtils.isHardCore() ? I18n.get("gameMode.hardcore") : I18n.get("gameMode.survival");
            case CREATIVE -> I18n.get("gameMode.creative");
            case SPECTATOR -> I18n.get("gameMode.spectator");
            case ADVENTURE -> I18n.get("gameMode.adventure");
        });

        //  If the player is in a hard core world but a different game mode
        if (PlayerUtils.isHardCore() && !PlayerUtils.isSurvival()) {
            narration.append(' ')
                    .append(I18n.get("options.difficulty.hardcore"));
        }
    }
}
