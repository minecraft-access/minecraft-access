package org.mcaccess.minecraftaccess.api;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Contract;
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
     * @return An {@link AccessMenuFunctionRegistration} to set additional optional properties.
     * @throws IllegalArgumentException If an {@link AccessMenuFunction} is already registered with the provided identifier and the same mod ID.
     * @throws UnsupportedOperationException Registries are already frozen.
     * @see AccessMenuFunction
     * @since 1.12.0
     */
    public AccessMenuFunctionRegistration register(@NotNull String identifier, @NotNull AccessMenuFunction function) {
        MainClass.register(AccessMenuFunction.class, Identifier.fromNamespaceAndPath(modid, identifier), function);
        return new AccessMenuFunctionRegistration(Identifier.fromNamespaceAndPath(modid, identifier));
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

    /**
     * Allows setting additional optional properties on registered access menu functions.
     *
     * @since 1.12.0
     */
    public static final class AccessMenuFunctionRegistration {
        private final Identifier identifier;

        AccessMenuFunctionRegistration(Identifier identifier) {
            this.identifier = identifier;
        }

        /**
         * Sets the default keybind for this function.
         *
         * @param keycode The primary keycode for the binding from {@link InputConstants}.
         * @param modifiers Additional modifiers which must also be held.
         *                  Supports {@link InputConstants#MOD_SHIFT}, {@link InputConstants#MOD_CONTROL}, {@link InputConstants#MOD_ALT}
         *                  in addition to keycodes from {@link InputConstants}.
         * @return This
         * @see InputConstants
         * @since 1.12.0
         */
        @Contract("_, _ -> this")
        public AccessMenuFunctionRegistration withDefaultKeybind(int keycode, int @NotNull ... modifiers) {
            MainClass.register(DefaultKeybind.class, identifier, new DefaultKeybind(keycode, modifiers));
            return this;
        }

        /** @hidden */
        @ApiStatus.Internal
        public static final class DefaultKeybind {
            /** @hidden */
            @ApiStatus.Internal
            public final int keycode;
            /** @hidden */
            @ApiStatus.Internal
            public final int[] modifiers;

            private DefaultKeybind(int keycode, int[] modifiers) {
                this.keycode = keycode;
                this.modifiers = modifiers;
            }
        }
    }
}
