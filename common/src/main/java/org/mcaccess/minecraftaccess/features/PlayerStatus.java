package org.mcaccess.minecraftaccess.features;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.level.GameType;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
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
            () -> KeyUtils.isAnyPressed(KeyBindingsHandler.Keys.NARRATE_PLAYER_STATUS_KEY.mapping),
            Keystroke.TriggeredAt.PRESSED,
            // 3s interval
            Interval.ms(3000));
    private final Minecraft client = Minecraft.getInstance();

    public void tick() {
        if (client.player == null) return;
        if (client.screen != null) return;

        if (narrationKey.canBeTriggered()) {
            if (client.hasControlDown()) {
                Collection<MobEffectInstance> effects = client.player.getActiveEffects();
                if (effects.isEmpty()) {
                    MainClass.narrate(I18n.get("minecraft_access.effect_narration.no_effects"), true);
                    return;
                }
                String narration = effects.stream().map(NarrationUtils::narrateEffect)
                        .collect(Collectors.joining(I18n.get("minecraft_access.other.words_connection")));
                MainClass.narrate(narration, true);
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
            assert client.gameMode != null;
            GameType currentMode = client.gameMode.getPlayerMode();

            StringBuilder narration = new StringBuilder();

            if (currentMode == GameType.SURVIVAL || currentMode == GameType.ADVENTURE) {
                if (!client.hasAltDown()) {
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
                    narration.append(I18n.get("minecraft_access.player_status.air", NarrationUtils.narrateNumber(air), NarrationUtils.narrateNumber(maxAir)));
                }

                if ((client.player.isInPowderSnow || frostExposurePercent > 0) && client.player.canFreeze()) {
                    narration.append(I18n.get("minecraft_access.player_status.frost", NarrationUtils.narrateNumber(frostExposurePercent)));
                }
            }

            if (narration.isEmpty() && (currentMode == GameType.SURVIVAL || currentMode == GameType.ADVENTURE)) {
                narration.append(I18n.get("minecraft_access.player_status.no_conditional_status"));
            }

            if (!Objects.equals(narration.toString(), I18n.get("minecraft_access.player_status.no_conditional_status"))) {
                addGameMode(narration, currentMode);
            }

            MainClass.narrate(narration.toString(), true);
        }
        narrationKey.updateStateForNextTick();
    }

    public void addGameMode(StringBuilder narration, GameType playerMode) {
        if (!narration.isEmpty()) {
            narration.append(I18n.get("minecraft_access.other.words_connection"));
        }

        narration.append(switch (playerMode) {
            case SURVIVAL -> {
                assert client.level != null;
                yield client.level.getLevelData().isHardcore() ? I18n.get("gameMode.hardcore") : I18n.get("gameMode.survival");
            }
            case CREATIVE -> I18n.get("gameMode.creative");
            case SPECTATOR -> I18n.get("gameMode.spectator");
            case ADVENTURE -> I18n.get("gameMode.adventure");
        });

        //  If the player is in a hard core world but a different game mode
        assert client.level != null;
        if (client.level.getLevelData().isHardcore() && playerMode != GameType.SURVIVAL) {
            narration.append(' ')
                    .append(I18n.get("options.difficulty.hardcore"));
        }
    }
}
