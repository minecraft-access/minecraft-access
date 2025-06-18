package org.mcaccess.minecraftaccess.features.read_crosshair;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snownee.jade.JadeClient;
import snownee.jade.api.ui.Element;
import snownee.jade.overlay.RayTracing;

import java.util.Arrays;

public class Jade implements CrosshairNarrator {
    private static Jade INSTANCE;

    private Jade() {
    }

    public static Jade getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Jade();
        }
        return INSTANCE;
    }

    @Override
    public @NotNull HitResult rayCast() {
        return RayTracing.INSTANCE.getTarget();
    }

    @Override
    public @Nullable Object deduplication(boolean speakSide, boolean speakConsecutiveBlocks) {
        if (JadeClient.tickHandler().rootElement == null) {
            return null;
        }
        return Arrays.asList(
                JadeClient.tickHandler().rootElement.getTooltip().lines.getFirst().elements().stream().map(Element::cachedNarration).toList(),
                speakConsecutiveBlocks && rayCast() instanceof BlockHitResult blockHitResult ? blockHitResult.getBlockPos() : null
        );
    }

    @Override
    public @NotNull String narrate(boolean speakSide) {
        return JadeClient.tickHandler().rootElement.getTooltip().getNarration();
    }
}
