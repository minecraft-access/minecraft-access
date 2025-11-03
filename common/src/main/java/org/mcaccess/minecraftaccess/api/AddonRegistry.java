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
    private final String modid;
    private final Map<ResourceLocation, Status> statuses = new LinkedHashMap<>();
    private final Map<ResourceLocation, AccessMenuFunction> accessMenu = new LinkedHashMap<>();

    @ApiStatus.Internal
    public AddonRegistry(String modid) {
        this.modid = modid;
    }

    @ApiStatus.Internal
    public @UnmodifiableView Map<ResourceLocation, Status> getStatuses() {
        return Collections.unmodifiableMap(statuses);
    }

    @ApiStatus.Internal
    public @UnmodifiableView Map<ResourceLocation, AccessMenuFunction> getAccessMenuOptions() {
        return Collections.unmodifiableMap(accessMenu);
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
            throw new IllegalArgumentException(String.format("Status %s is already registered", identifier));
        }
        statuses.put(location, status);
    }

    /**
     * Registers an {@link AccessMenuFunction}.
     *
     * @param identifier A unique identifier for the {@link AccessMenuFunction}. This is automatically namespaced by mod ID.
     * @param function The {@link AccessMenuFunction} to register.
     * @throws IllegalArgumentException If an {@link AccessMenuFunction} is already registered with the provided identifier and the same mod ID.
     * @see AccessMenuFunction
     * @since 1.12.0
     */
    public void register(@NotNull String identifier, @NotNull AccessMenuFunction function) {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, identifier);
        if (accessMenu.containsKey(location) || MainClass.ACCESS_MENU_REGISTRY.containsKey(location)) {
            throw new IllegalArgumentException(String.format("Access menu function %s is already registered", identifier));
        }
        accessMenu.put(location, function);
    }
}
