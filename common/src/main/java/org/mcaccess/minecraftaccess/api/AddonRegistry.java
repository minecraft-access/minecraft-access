package org.mcaccess.minecraftaccess.api;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

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

    /** @hidden */
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
     * @throws UnsupportedOperationException Registries are already frozen.
     * @see Status
     * @since 1.12.0
     */
    public void register(@NotNull String identifier, @NotNull Status status) {
        MainClass.register(Status.class, Identifier.fromNamespaceAndPath(modid, identifier), status);
    }

    /**
     * Registers an {@link AccessMenuFunction}.
     *
     * @param identifier A unique identifier for the {@link AccessMenuFunction}. This is automatically namespaced by mod ID.
     * @param function The {@link AccessMenuFunction} to register.
     * @throws IllegalArgumentException If an {@link AccessMenuFunction} is already registered with the provided identifier and the same mod ID.
     * @throws UnsupportedOperationException Registries are already frozen.
     * @see AccessMenuFunction
     * @since 1.12.0
     */
    public void register(@NotNull String identifier, @NotNull AccessMenuFunction function) {
        MainClass.register(AccessMenuFunction.class, Identifier.fromNamespaceAndPath(modid, identifier), function);
    }

    /**
     * Registers a {@link WorldNarrator}.
     *
     * @param identifier A unique identifier for the {@link WorldNarrator}. This is automatically namespaced by mod ID.
     * @param worldNarrator The {@link WorldNarrator} to register.
     * @throws IllegalArgumentException If a {@link WorldNarrator} is already registered with the provided identifier and the same mod ID.
     * @throws UnsupportedOperationException Registries are already frozen.
     * @see WorldNarrator
     * @since 1.12.0
     */
    public void register(@NotNull String identifier, @NotNull WorldNarrator worldNarrator) {
        MainClass.register(WorldNarrator.class, Identifier.fromNamespaceAndPath(modid, identifier), worldNarrator);
    }
}
