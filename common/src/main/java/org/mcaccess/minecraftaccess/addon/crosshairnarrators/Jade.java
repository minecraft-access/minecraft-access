package org.mcaccess.minecraftaccess.addon.crosshairnarrators;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snownee.jade.JadeClient;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.impl.Tooltip;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.overlay.RayTracing;

import org.mcaccess.minecraftaccess.api.CrosshairNarrator;

public class Jade implements CrosshairNarrator {
    @Override
    public @Nullable HitResult rayCast() {
        return RayTracing.INSTANCE.getTarget();
    }

    @Override
    public @Nullable String narrate(@NotNull HitResult rayCast) {
        if (JadeClient.tickHandler().rootElement == null) {
            return null;
        }
        return JadeClient.tickHandler().rootElement.getTooltip().getNarration();
    }

    @Override
    public @NotNull String narrate(@NotNull BlockPos block) {
        assert Minecraft.getInstance().level != null;
        assert Minecraft.getInstance().player != null;
        BlockAccessor accessor = WailaClientRegistration.instance().blockAccessor()
                .blockState(Minecraft.getInstance().level.getBlockState(block))
                .blockEntity(Minecraft.getInstance().level.getBlockEntity(block))
                .hit(BlockHitResult.miss(
                        block.getCenter(),
                        Direction.getApproximateNearest(Minecraft.getInstance().player.position().subtract(block.getCenter())),
                        block
                ))
                .requireVerification()
                .build();
        Tooltip tooltip = new Tooltip();
        WailaClientRegistration.instance()
                .getAccessorHandler(accessor.getAccessorType())
                .gatherComponents(accessor, provider -> tooltip);
        return tooltip.getNarration();
    }

    @Override
    public @NotNull String narrate(@NotNull Entity entity) {
        EntityAccessor accessor = WailaClientRegistration.instance().entityAccessor()
                .entity(entity)
                .hit(new EntityHitResult(entity))
                .requireVerification()
                .build();
        Tooltip tooltip = new Tooltip();
        WailaClientRegistration.instance()
                .getAccessorHandler(accessor.getAccessorType())
                .gatherComponents(accessor, provider -> tooltip);
        return tooltip.getNarration();
    }
}
