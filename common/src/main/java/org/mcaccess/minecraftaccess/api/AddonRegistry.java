package org.mcaccess.minecraftaccess.api;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import org.mcaccess.minecraftaccess.MainClass;

/**
 * Registry for registering items with Minecraft Access.
 *
 * <p>Any usage outside of {@link MinecraftAccessAddon#init(AddonRegistry)} is unsupported and may break at any time.
 *
 * @see MinecraftAccessAddon#init(AddonRegistry)
 * @since 1.12.0
 */
public final class AddonRegistry {
    private final Map<ResourceLocation, Status> statuses = new LinkedHashMap<>();
    private final String modid;

    @ApiStatus.Internal
    public AddonRegistry(String modid) {
        this.modid = modid;
    }

    /**
     * Registers a {@link Status}.
     *
     * @param identifier A unique identifier for the {@link Status}. This is automatically namespaced by mod ID.
     * @param status The {@link Status} to register.
     * @throws IllegalArgumentException If a {@link Status} is already registered with the provided identifier and the same mod ID.
     * @see Status
     * @since 1.12.0
     */
    public void register(@NotNull String identifier, @NotNull Status status) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, identifier);
        if (statuses.containsKey(location) || MainClass.STATUS_REGISTRY.containsKey(location)) {
            throw new IllegalArgumentException(String.format("Stat %s is already registered", identifier));
        }
        statuses.put(location, status);
    }

    @ApiStatus.Internal
    public @UnmodifiableView Map<ResourceLocation, Status> getStatuses() {
        return Collections.unmodifiableMap(statuses);
    }
}
