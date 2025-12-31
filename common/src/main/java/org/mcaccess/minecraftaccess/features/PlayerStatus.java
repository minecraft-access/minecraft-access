package org.mcaccess.minecraftaccess.features;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.addon.CoreAddon;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.KeyMappingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.condition.IntervalKeystroke;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;


/**
 * Adds a key bind to narrate the player's non potion related statuses.<br>
 * - Narrate Player Status Key (default: R) = Narrates the health and hunger.<br>
 */
@Slf4j
public class PlayerStatus {
    private final Minecraft client = Minecraft.getInstance();
    IntervalKeystroke narrationKey = new IntervalKeystroke(
            KeyMappingsHandler.Keys.NARRATE_PLAYER_STATUS_KEY.mapping::isDown,
            Keystroke.TriggeredAt.PRESSED,
            Interval.sec(3));
    private final Map<Identifier, Status.WarningLevel> lastWarning = new HashMap<>();
    private boolean wasSneaking = false;
    private boolean wasSprinting = false;

    public void tick() {
        if (client.player == null) return;
        if (client.screen != null) return;

        movementTypeStatus();

        if (Config.getInstance().playerWarnings.enabled) {
            for (Identifier key : Config.getInstance().playerWarnings.statuses) {
                Status status = MainClass.registry(Status.class).get(key);
                Status.WarningLevel currentLevel = status.warning();
                if (currentLevel.ordinal() > lastWarning.getOrDefault(key, Status.WarningLevel.NONE).ordinal()) {
                    if (Config.getInstance().playerWarnings.playSound) {
                        SoundEvent soundToPlay = currentLevel.ordinal() > Status.WarningLevel.WARNING.ordinal()
                                ? SoundEvents.ANVIL_PLACE
                                : SoundEvents.RESPAWN_ANCHOR_DEPLETE.value();
                        assert client.level != null;
                        client.level.playPlayerSound(soundToPlay, SoundSource.PLAYERS, 1.0f, 1.0f);
                        MainClass.narrate(I18n.get("minecraft_access.player_status.warning", status.message()), true);
                    }
                }
                lastWarning.put(key, currentLevel);
            }
        }

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

            List<Status> statuses = Arrays.stream(Config.getInstance().playerWarnings.statuses)
                    .map(MainClass.registry(Status.class)::get)
                    .filter(status -> switch (status.visibility()) {
                        case NONE -> false;
                        case NORMAL -> !client.hasAltDown();
                        case IMPORTANT -> true;
                    })
                    .toList();

            if (statuses.isEmpty() && client.hasAltDown()) {
                assert client.gameMode != null;
                if (client.gameMode.getPlayerMode().isSurvival()) {
                    MainClass.narrate(I18n.get("minecraft_access.player_status.no_conditional_status"), true);
                } else {
                    MainClass.narrate(CoreAddon.GAME_MODE_STAT.message(), true);
                }
            } else {
                MainClass.narrate(
                        statuses.stream()
                                .map(Status::message)
                                .collect(Collectors.joining(I18n.get("minecraft_access.other.words_connection"))),
                        true
                );
            }
        }
        narrationKey.updateStateForNextTick();
    }

    private void movementTypeStatus() {
        assert client.player != null;
        boolean isSneaking = client.player.isCrouching();
        boolean isSprinting = client.player.isSprinting() && !isSneaking;

        if (!wasSneaking && isSneaking) {
            client.level.playPlayerSound(SoundEvents.SHOVEL_FLATTEN, SoundSource.PLAYERS, 1.0f, 0.5f);
        } else if (isSprinting && !wasSprinting) {
            client.level.playPlayerSound(SoundEvents.SHOVEL_FLATTEN, SoundSource.PLAYERS, 1.0f, 2.0f);
        } else if (!isSneaking && wasSneaking || !isSprinting && wasSprinting) {
            client.level.playPlayerSound(SoundEvents.SHOVEL_FLATTEN, SoundSource.PLAYERS, 1.0f, 0.9f);
        }

        wasSneaking = isSneaking;
        wasSprinting = isSprinting;
    }
}
