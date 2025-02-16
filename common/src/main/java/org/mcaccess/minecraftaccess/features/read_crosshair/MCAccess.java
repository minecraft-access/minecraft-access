package org.mcaccess.minecraftaccess.features.read_crosshair;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;

import java.util.Arrays;

public class MCAccess implements CrosshairNarrator {
    private static MCAccess INSTANCE;

    private MCAccess() {
    }

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
                String side = speakSide ? blockHitResult.getDirection().getName() : "";
                yield Arrays.asList(
                        NarrationUtils.narrateBlockForContentChecking(blockHitResult.getBlockPos(), side).getB(),
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
                String side = speakSide ? I18n.get(String.format("minecraft_access.direction.%s", blockHitResult.getDirection().getName())) : "";
                yield NarrationUtils.narrateBlockForContentChecking(blockHitResult.getBlockPos(), side).getA();
            }
            case EntityHitResult entityHitResult -> NarrationUtils.narrateEntity(entityHitResult.getEntity());
            default -> throw new IllegalStateException("Unexpected value: " + rayCast());
        };
    }
}
