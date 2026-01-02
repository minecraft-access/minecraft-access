package org.mcaccess.minecraftaccess.features;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.addon.CoreAddon;
import org.mcaccess.minecraftaccess.api.Status;
import org.mcaccess.minecraftaccess.utils.events.ClientPlayingTick;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;


/**
 * Adds a key bind to narrate the player's non potion related statuses.<br>
 * - Narrate Player Status Key (default: R) = Narrates the health and hunger.<br>
 */
public class PlayerStatus implements BalmClientModule {
    private final Map<Identifier, Status.WarningLevel> lastWarning = new HashMap<>();
    private boolean wasSneaking = false;
    private boolean wasSprinting = false;

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "player_status");
    }

    @Override
    public void initialize() {
        ClientPlayingTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            lastWarning.clear();
            wasSneaking = false;
            wasSprinting = false;
        });

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.player_status/normal"))
                .withDefault(InputBinding.key(InputConstants.KEY_R))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    narratePlayerStatus(false);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.player_status/important"))
                .withDefault(InputBinding.key(InputConstants.KEY_R, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    narratePlayerStatus(true);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.player_effects"))
                .withDefault(InputBinding.key(InputConstants.KEY_R, KeyModifiers.of(KeyModifier.CONTROL)))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
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


    }

    private void tick(Minecraft client, Player player, Level level) {
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
                        level.playPlayerSound(soundToPlay, SoundSource.PLAYERS, 1.0f, 1.0f);
                        MainClass.narrate(I18n.get("minecraft_access.player_status.warning", status.message()), true);
                    }
                }
                lastWarning.put(key, currentLevel);
            }
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

    private void movementTypeStatus() {
        Minecraft client = Minecraft.getInstance();
        assert client.player != null;
        assert client.level != null;
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
