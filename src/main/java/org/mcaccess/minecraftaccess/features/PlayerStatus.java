package org.mcaccess.minecraftaccess.features;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.addon.CoreAddon;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.events.ServerChangeDetector;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

/**
 * Adds a key bind to narrate the player's non potion related statuses.<br>
 * - Narrate Player Status Key (default: R) = Narrates the health and hunger.<br>
 */
public class PlayerStatus implements BalmClientModule {
    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "player_status");
    }

    @Override
    public void initialize() {
        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "player_status.player_status/normal"))
                .withDefault(InputBinding.key(InputConstants.KEY_R))
                .overrideCategory(KeyMappingCategories.PLAYER_STATUS)
                .handleWorldInput(_ -> {
                    narratePlayerStatus(false);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "player_status.player_status/important"))
                .withDefault(InputBinding.key(InputConstants.KEY_R, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.PLAYER_STATUS)
                .handleWorldInput(_ -> {
                    narratePlayerStatus(true);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "player_status.player_effects"))
                .withDefault(InputBinding.key(InputConstants.KEY_R, KeyModifiers.of(KeyModifier.CONTROL)))
                .overrideCategory(KeyMappingCategories.PLAYER_STATUS)
                .handleWorldInput(_ -> {
                    assert Minecraft.getInstance().player != null;
                    Collection<MobEffectInstance> effects = Minecraft.getInstance().player.getActiveEffects();
                    if (effects.isEmpty()) {
                        new Translation("minecraft_access.effect_narration.no_effects").narrate(true);
                        return true;
                    }
                    Translation.Delimited narration = new Translation.Delimited();
                    effects.stream().map(NarrationUtils::narrateEffect).forEach(narration::put);
                    narration.narrate(true);
                    return true;
                })
                .build();

        new ServerChangeDetector<Boolean>().levelEvent(
                (_, player, _) -> player.isCrouching(),
                (_, player, level, _, value) -> {
                    if (!Config.getInstance().features.crouchAndSprintCues || !value && player.isSprinting()) {
                        return;
                    }
                    level.playPlayerSound(SoundEvents.SHOVEL_FLATTEN, SoundSource.PLAYERS, 1.0f, value ? 0.5f : 0.9f);
                }
        );

        new ServerChangeDetector<Boolean>().levelEvent(
                (_, player, _) -> player.isSprinting() && !player.isCrouching(),
                (_, _, level, _, value) -> {
                    if (Config.getInstance().features.crouchAndSprintCues) {
                        level.playPlayerSound(SoundEvents.SHOVEL_FLATTEN, SoundSource.PLAYERS, 1.0f, value ? 2.5f : 0.9f);
                    }
                }
        );

        for (Map.Entry<Identifier, Status> entry : MainClass.registry(Status.class).entrySet()) {
            new ServerChangeDetector<>(() -> Status.WarningLevel.NONE).levelEvent(
                    (_, _, _) -> entry.getValue().warning(),
                    (_, _, level, previous, value) -> {
                        if (!Config.getInstance().playerWarnings.enabled
                                || !Arrays.asList(Config.getInstance().playerWarnings.statuses).contains(entry.getKey())
                                || value.ordinal() <= previous.ordinal()
                                || !Config.getInstance().playerWarnings.playSound
                        ) {
                            return;
                        }
                        level.playPlayerSound(value.ordinal() >= Status.WarningLevel.CRITICAL.ordinal()
                                ? SoundEvents.ANVIL_PLACE
                                : SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
                        new Translation("minecraft_access.player_status.warning")
                                .variable("message").put(entry.getValue().message())
                                .narrate(true);
                    }
            );
        }
    }

    private void narratePlayerStatus(boolean hasAltDown) {
        Minecraft client = Minecraft.getInstance();

        List<Status> statuses = Arrays.stream(Config.getInstance().playerWarnings.statuses)
                .map(MainClass.registry(Status.class)::get)
                .filter(status -> switch (status.visibility()) {
                    case NONE -> false;
                    case NORMAL -> !hasAltDown;
                    case IMPORTANT -> true;
                })
                .toList();

        if (statuses.isEmpty() && hasAltDown) {
            assert client.gameMode != null;
            if (client.gameMode.getPlayerMode().isSurvival()) {
                new Translation("minecraft_access.player_status.no_conditional_status").narrate(true);
            } else {
                MainClass.narrate(CoreAddon.GAME_MODE_STAT.message(), true);
            }
        } else {
            Translation.Delimited narration = new Translation.Delimited();
            statuses.stream().map(Status::message).forEach(narration::put);
            narration.narrate(true);
        }
    }
}
