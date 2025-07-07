package org.mcaccess.minecraftaccess.features.read_crosshair;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snownee.jade.JadeClient;
import snownee.jade.impl.Tooltip;
import snownee.jade.impl.ui.BoxElementImpl;
import snownee.jade.overlay.RayTracing;

import java.util.Arrays;

public class Jade implements CrosshairNarrator {
    @Override
    public @NotNull HitResult rayCast() {
        return RayTracing.INSTANCE.getTarget();
    }

    @Override
    public @Nullable Object deduplication(boolean speakSide, boolean speakConsecutiveBlocks) {
        BoxElementImpl rootElement = JadeClient.tickHandler().rootElement;
        if (rootElement == null) {
            return null;
        }
        Tooltip tooltip = rootElement.getTooltip();
        String narration = tooltip.getNarration(tooltip.lines.getFirst()::equals);
        return Arrays.asList(
                narration,
                speakConsecutiveBlocks && rayCast() instanceof BlockHitResult blockHitResult ? blockHitResult.getBlockPos() : null
        );
    }

    @Override
    public @NotNull String narrate(boolean speakSide) {
        return JadeClient.tickHandler().rootElement.getTooltip().getNarration();
    }
}
