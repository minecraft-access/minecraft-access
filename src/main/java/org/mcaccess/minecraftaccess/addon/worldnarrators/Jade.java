package org.mcaccess.minecraftaccess.addon.worldnarrators;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import snownee.jade.JadeClient;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.EntityAccessor;
import snownee.jade.impl.Tooltip;
import snownee.jade.impl.WailaClientRegistration;
import snownee.jade.overlay.RayTracing;

import org.mcaccess.minecraftaccess.api.WorldNarrator;

public class Jade implements WorldNarrator {
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
                        Vec3.atCenterOf(block),
                        Direction.getApproximateNearest(Minecraft.getInstance().player.position().subtract(Vec3.atCenterOf(block))),
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
