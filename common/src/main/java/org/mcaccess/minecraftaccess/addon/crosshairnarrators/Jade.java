package org.mcaccess.minecraftaccess.addon.crosshairnarrators;

import java.util.Arrays;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snownee.jade.JadeClient;
import snownee.jade.impl.Tooltip;
import snownee.jade.impl.ui.BoxElementImpl;
import snownee.jade.overlay.RayTracing;

import org.mcaccess.minecraftaccess.api.CrosshairNarrator;

public class Jade implements CrosshairNarrator {
    @Override
    public @NotNull HitResult rayCast() {
        return RayTracing.INSTANCE.getTarget();
    }

    @Override
    public @Nullable Object deduplication(boolean narrateSide, boolean narrateConsecutiveBlocks) {
        BoxElementImpl rootElement = JadeClient.tickHandler().rootElement;
        if (rootElement == null) {
            return null;
        }
        Tooltip tooltip = rootElement.getTooltip();
        String narration = tooltip.getNarration(tooltip.lines.getFirst()::equals);
        return Arrays.asList(
                narration,
                narrateConsecutiveBlocks && rayCast() instanceof BlockHitResult blockHitResult ? blockHitResult.getBlockPos() : null
        );
    }

    @Override
    public @NotNull String narrate(boolean narrateSide) {
        return JadeClient.tickHandler().rootElement.getTooltip().getNarration();
    }
}
