package org.mcaccess.minecraftaccess.features;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.mixin.BossHealthOverlayAccessor;
import org.mcaccess.minecraftaccess.utils.events.ClientPlayingTick;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;

public class HUDStatus implements BalmClientModule {
    private Boolean hudWasHidden = null;
    private boolean attackCooldownPlayed = false;
    private int bossIndex = 0;

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "hud_status");
    }

    @Override
    public void initialize() {
        ClientPlayingTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            attackCooldownPlayed = false;
            bossIndex = 0;
        });

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.narrate_bossbars/next"))
                .withDefault(InputBinding.key(InputConstants.KEY_U))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    narrateBossBars(false);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.narrate_bossbars/previous"))
                .withDefault(InputBinding.key(InputConstants.KEY_U, KeyModifiers.of(KeyModifier.SHIFT)))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    narrateBossBars(true);
                    return true;
                })
                .build();
    }

    private void tick(Minecraft client, Player player, Level level) {
        hudVisibilityStatus();
        attackCooldownStatus();
    }

    private void hudVisibilityStatus() {
        Boolean hudIsHidden = Minecraft.getInstance().options.hideGui;

        if (hudWasHidden != hudIsHidden) {
            MainClass.narrate(I18n.get(String.format("minecraft_access.hud_status.announce_%s", hudIsHidden ? "hidden" : "shown")), true);
        }
        hudWasHidden = hudIsHidden;
    }

    private void attackCooldownStatus() {
        Minecraft client = Minecraft.getInstance();
        assert client.gameMode != null;
        boolean indicatorShowing = !hudWasHidden && client.options.attackIndicator().get() != AttackIndicatorStatus.OFF && client.gameMode.getPlayerMode() != GameType.SPECTATOR;
        if (!indicatorShowing) return;

        LocalPlayer player = client.player;
        if (player == null) return;

        float cooldownProgress = player.getAttackStrengthScale(1.0f);
        if (!attackCooldownPlayed && cooldownProgress == 1.0f) {
            assert client.level != null;
            client.level.playPlayerSound(SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.PLAYERS, 0.6f, 1.0f);
            attackCooldownPlayed = true;
        } else if (attackCooldownPlayed && cooldownProgress < 1.0f) {
            attackCooldownPlayed = false;
        }
    }

    private void narrateBossBars(boolean isShiftDown) {
        List<LerpingBossEvent> bosses = new ArrayList<>(
                ((BossHealthOverlayAccessor) Minecraft.getInstance().gui.getBossOverlay()).getEvents().values()
        );

        if (bosses.isEmpty()) {
            MainClass.narrate(I18n.get("minecraft_access.other.no_bossbars"), true);
            return;
        }

        if (isShiftDown) {
            bossIndex = (bossIndex - 1) % bosses.size();
        } else {
            bossIndex = (bossIndex + 1) % bosses.size();
        }

        LerpingBossEvent currentBoss = bosses.get(bossIndex);
        String name = currentBoss.getName().getString();
        int healthPercent = Math.round(currentBoss.getProgress() * 100);
        MainClass.narrate(I18n.get("minecraft_access.other.bossbar_status", name, healthPercent), true);
    }
}
