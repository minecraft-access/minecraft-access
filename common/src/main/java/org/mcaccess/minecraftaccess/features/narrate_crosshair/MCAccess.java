package org.mcaccess.minecraftaccess.features.narrate_crosshair;

import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;

public class MCAccess implements CrosshairNarrator {
    @Override
    public @NotNull HitResult rayCast() {
        LocalPlayer player = Minecraft.getInstance().player;
        return PlayerUtils.crosshairTarget(Math.min(player.blockInteractionRange(), player.entityInteractionRange()));
    }

    @Override
    public @Nullable Object deduplication(boolean narrateSide, boolean narrateConsecutiveBlocks) {
        HitResult hit = rayCast();
        if (hit.getType() == HitResult.Type.MISS) {
            return null;
        }
        return switch (hit) {
            case BlockHitResult blockHitResult -> {
                String side = narrateSide ? blockHitResult.getDirection().getName() : "";
                yield Arrays.asList(
                        NarrationUtils.narrateBlockForContentChecking(blockHitResult.getBlockPos(), side).getB(),
                        narrateConsecutiveBlocks ? blockHitResult.getBlockPos() : null
                );
            }
            case EntityHitResult entityHitResult -> NarrationUtils.narrateEntity(entityHitResult.getEntity());
            default -> null;
        };
    }

    @Override
    public @NotNull String narrate(boolean narrateSide) {
        return switch (rayCast()) {
            case BlockHitResult blockHitResult -> {
                String side = narrateSide ? I18n.get(String.format("minecraft_access.direction.%s", blockHitResult.getDirection().getName())) : "";
                yield NarrationUtils.narrateBlockForContentChecking(blockHitResult.getBlockPos(), side).getA();
            }
            case EntityHitResult entityHitResult -> NarrationUtils.narrateEntity(entityHitResult.getEntity());
            default -> throw new IllegalStateException("Unexpected value: " + rayCast());
        };
    }
}
