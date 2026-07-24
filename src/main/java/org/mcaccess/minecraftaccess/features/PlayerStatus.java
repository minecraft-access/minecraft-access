package org.mcaccess.minecraftaccess.features;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.ModConfig;
import org.mcaccess.minecraftaccess.addon.CoreAddon;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.events.ServerChangeDetector;

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
                        MainClass.narrate(I18n.get("minecraft_access.effect_narration.no_effects"), true);
                        return true;
                    }
                    String narration = effects.stream().map(NarrationUtils::narrateEffect)
                            .collect(Collectors.joining(I18n.get("minecraft_access.other.words_connection")));
                    MainClass.narrate(narration, true);
                    return true;
                })
                .build();

        new ServerChangeDetector<Boolean>().levelEvent(
                (_, player, _) -> player.isCrouching(),
                (_, player, level, _, value) -> {
                    if (!ModConfig.getInstance().features.crouchAndSprintCues || !value && player.isSprinting()) {
                        return;
                    }
                    level.playPlayerSound(SoundEvents.SHOVEL_FLATTEN, SoundSource.PLAYERS, 1.0f, value ? 0.5f : 0.9f);
                }
        );

        new ServerChangeDetector<Boolean>().levelEvent(
                (_, player, _) -> player.isSprinting() && !player.isCrouching(),
                (_, _, level, _, value) -> {
                    if (ModConfig.getInstance().features.crouchAndSprintCues) {
                        level.playPlayerSound(SoundEvents.SHOVEL_FLATTEN, SoundSource.PLAYERS, 1.0f, value ? 2.5f : 0.9f);
                    }
                }
        );

        for (Map.Entry<Identifier, Status> entry : MainClass.registry(Status.class).entrySet()) {
            new ServerChangeDetector<>(() -> Status.WarningLevel.NONE).levelEvent(
                    (_, _, _) -> entry.getValue().warning(),
                    (_, _, level, previous, value) -> {
                        if (!ModConfig.getInstance().playerWarnings.enabled
                                || !Arrays.asList(ModConfig.getInstance().playerWarnings.statuses).contains(entry.getKey())
                                || value.ordinal() <= previous.ordinal()
                                || !ModConfig.getInstance().playerWarnings.playSound
                        ) {
                            return;
                        }
                        level.playPlayerSound(value.ordinal() >= Status.WarningLevel.CRITICAL.ordinal()
                                ? SoundEvents.ANVIL_PLACE
                                : SoundEvents.RESPAWN_ANCHOR_DEPLETE.value(), SoundSource.PLAYERS, 1.0f, 1.0f);
                        MainClass.narrate(I18n.get("minecraft_access.player_status.warning", entry.getValue().message()), true);
                    }
            );
        }
    }

    private void narratePlayerStatus(boolean hasAltDown) {
        Minecraft client = Minecraft.getInstance();

        List<Status> statuses = ModConfig.getInstance().playerWarnings.statuses.stream()
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
}
