package org.mcaccess.minecraftaccess.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

/**
 * Registry for registering items with Minecraft Access.
 *
 * @see MinecraftAccessAddon#init(AddonRegistry)
 * @since 1.12.0
 */
public final class AddonRegistry {
    private final Map<ResourceLocation, Status> stats = new LinkedHashMap<>();

    @ApiStatus.Internal
    public AddonRegistry() {
    }

    /**
     * Registers a {@link Status}.
     *
     * @param id A unique identifier for the {@link Status}.
     * @param status The {@link Status} to register.
     * @throws IllegalArgumentException If a {@link Status} is already registered with the provided identifier.
     * @see Status
     * @since 1.12.0
     */
    public void register(@NotNull ResourceLocation id, @NotNull Status status) {
        if (stats.containsKey(id)) {
            throw new IllegalArgumentException(String.format("Stat %s is already registered", id));
        }
        stats.put(id, status);
    }

    @ApiStatus.Internal
    public @UnmodifiableView Map<ResourceLocation, Status> getStats() {
        return Collections.unmodifiableMap(stats);
    }
}
