package com.github.khanshoaib3.minecraft_access.features.read_crosshair;

import com.github.khanshoaib3.minecraft_access.utils.NarrationUtils;
import com.github.khanshoaib3.minecraft_access.utils.PlayerUtils;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class MCAccess implements CrosshairNarrator {
    private static MCAccess INSTANCE;

    private MCAccess() {}

    public static MCAccess getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MCAccess();
        }
        return INSTANCE;
    }

    @Override
    public @NotNull HitResult rayCast() {
        return PlayerUtils.crosshairTarget(PlayerUtils.getInteractionRange());
    }

    @Override
    public @Nullable Object deduplication(boolean speakSide, boolean speakConsecutiveBlocks) {
        HitResult hit = rayCast();
        if (hit.getType() == HitResult.Type.MISS) {
            return null;
        }
        return switch (hit) {
            case BlockHitResult blockHitResult -> {
                String side = speakSide ? blockHitResult.getSide().getName() : "";
                yield Arrays.asList(
                        NarrationUtils.narrateBlockForContentChecking(blockHitResult.getBlockPos(), side).getRight(),
                        speakConsecutiveBlocks ? blockHitResult.getBlockPos() : null
                );
            }
            case EntityHitResult entityHitResult -> NarrationUtils.narrateEntity(entityHitResult.getEntity());
            default -> null;
        };
    }

    @Override
    public @NotNull String narrate(boolean speakSide) {
        return switch (rayCast()) {
            case BlockHitResult blockHitResult -> {
                String side = speakSide ? I18n.translate(String.format("minecraft_access.direction.%s", blockHitResult.getSide().getName())) : "";
                yield NarrationUtils.narrateBlockForContentChecking(blockHitResult.getBlockPos(), side).getLeft();
            }
            case EntityHitResult entityHitResult -> NarrationUtils.narrateEntity(entityHitResult.getEntity());
            default -> throw new IllegalStateException("Unexpected value: " + rayCast());
        };
    }
}
