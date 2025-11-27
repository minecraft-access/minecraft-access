package org.mcaccess.minecraftaccess.addon.crosshairnarrators;

import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;
import snownee.jade.JadeClient;
import snownee.jade.overlay.RayTracing;

import org.mcaccess.minecraftaccess.api.CrosshairNarrator;

public class Jade implements CrosshairNarrator {
    @Override
    public @Nullable HitResult rayCast() {
        return RayTracing.INSTANCE.getTarget();
    }

    @Override
    public @Nullable String narrate() {
        if (JadeClient.tickHandler().rootElement == null) {
            return null;
        }
        return JadeClient.tickHandler().rootElement.getTooltip().getNarration();
    }
}
