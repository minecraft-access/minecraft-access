package com.github.khanshoaib3.minecraft_access.features.read_crosshair;

import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.ui.IElement;
import snownee.jade.overlay.RayTracing;
import snownee.jade.overlay.WailaTickHandler;

import java.util.Arrays;

public class Jade implements CrosshairNarrator {
    private static Jade INSTANCE;

    private Jade() {}

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
        if (WailaTickHandler.instance().rootElement == null) {
            return null;
        }
        return Arrays.asList(
                WailaTickHandler.instance().rootElement.getTooltip().lines.getFirst().sortedElements().stream().map(IElement::getMessage).toList(),
                speakConsecutiveBlocks && rayCast() instanceof BlockHitResult blockHitResult ? blockHitResult.getBlockPos() : null
        );
    }

    @Override
    public @NotNull String narrate(boolean speakSide) {
        return WailaTickHandler.instance().rootElement.getTooltip().getMessage();
    }
}
