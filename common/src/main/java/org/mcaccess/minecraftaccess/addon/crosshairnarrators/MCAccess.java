package org.mcaccess.minecraftaccess.addon.crosshairnarrators;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.CrosshairNarrator;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;

public class MCAccess implements CrosshairNarrator {
    @Override
    public @NotNull HitResult rayCast() {
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        return PlayerUtils.crosshairTarget(Math.min(player.blockInteractionRange(), player.entityInteractionRange()));
    }

    @Override
    public @Nullable String narrate(@NotNull HitResult rayCast) {
        return switch (rayCast()) {
            case BlockHitResult blockHitResult -> {
                String side = Config.getInstance().narrateCrosshair.narrateBlockFace
                        ? I18n.get(String.format("minecraft_access.direction.%s", blockHitResult.getDirection().getName()))
                        : "";
                yield NarrationUtils.narrateBlock(blockHitResult.getBlockPos(), side);
            }
            case EntityHitResult entityHitResult -> NarrationUtils.narrateEntity(entityHitResult.getEntity());
            default -> throw new IllegalStateException("Unexpected value: " + rayCast());
        };
    }

    @Override
    public @NotNull String narrate(@NotNull BlockPos block) {
        return NarrationUtils.narrateBlock(block, "");
    }

    @Override
    public @NotNull String narrate(@NotNull Entity entity) {
        return NarrationUtils.narrateEntity(entity);
    }
}
