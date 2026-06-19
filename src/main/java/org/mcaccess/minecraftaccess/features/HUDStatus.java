package org.mcaccess.minecraftaccess.features;

import java.util.ArrayList;
import java.util.List;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.balm.client.platform.util.SessionLocal;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.AttackIndicatorStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.mixin.BossHealthOverlayAccessor;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.events.ChangeDetector;
import org.mcaccess.minecraftaccess.utils.events.ServerChangeDetector;

public class HUDStatus implements BalmClientModule {
    private final SessionLocal<Integer> bossIndex = new SessionLocal<>(() -> 0);

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "hud_status");
    }

    @Override
    public void initialize() {
        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.narrate_bossbar/next"))
                .withDefault(InputBinding.key(InputConstants.KEY_U))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    narrateBossBars(false);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.narrate_bossbar/previous"))
                .withDefault(InputBinding.key(InputConstants.KEY_U, KeyModifiers.of(KeyModifier.SHIFT)))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    narrateBossBars(true);
                    return true;
                })
                .build();

        new ChangeDetector<Boolean>().clientEvent(
                client -> client.gui.hud.isHidden(),
                (client, previous, value) -> MainClass.narrate(
                        I18n.get(String.format("minecraft_access.hud_status.announce_%s", value ? "hidden" : "shown")),
                        true
                )
        );

        new ServerChangeDetector<>(() -> false).levelEvent((client, player, level) -> player.getAttackStrengthScale(0) >= 1, this::attackIndicator);
    }

    private void attackIndicator(Minecraft client, Player player, Level level, Boolean previous, Boolean value) {
        if (!value || client.gui.hud.isHidden() || client.options.attackIndicator().get() == AttackIndicatorStatus.OFF || player.isSpectator()) {
            return;
        }
        level.playPlayerSound(SoundEvents.NOTE_BLOCK_HAT.value(), SoundSource.PLAYERS, 0.6f, 1.0f);
    }

    private void narrateBossBars(boolean isShiftDown) {
        List<LerpingBossEvent> bosses = new ArrayList<>(
                ((BossHealthOverlayAccessor) Minecraft.getInstance().gui.hud.getBossOverlay()).getEvents().values()
        );

        if (bosses.isEmpty()) {
            MainClass.narrate(I18n.get("minecraft_access.other.no_bossbars"), true);
            return;
        }

        if (isShiftDown) {
            bossIndex.value = (bossIndex.value - 1) % bosses.size();
        } else {
            bossIndex.value = (bossIndex.value + 1) % bosses.size();
        }

        LerpingBossEvent currentBoss = bosses.get(bossIndex.value);
        String name = currentBoss.getName().getString();
        int healthPercent = Math.round(currentBoss.getProgress() * 100);
        MainClass.narrate(I18n.get("minecraft_access.other.bossbar_status", name, healthPercent), true);
    }
}
