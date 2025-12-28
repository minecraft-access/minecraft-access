package org.mcaccess.minecraftaccess.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.jetbrains.annotations.NotNull;

/**
 * Entrypoint for registering items with Minecraft Access.
 *
 * <p>Implementations must have a default or no-args constructor.
 * This constructor will be called before Minecraft Access is initialised,
 * any initialisation which depends on Minecraft Access should go in {@link #init(AddonRegistry)}.
 *
 * <p>This should be added to {@code fabric.mod.json} on Fabric as an entrypoint:
 * <pre>{@code
 * {
 *      "entrypoints": {
 *          "minecraft_access": [
 *              "com.example.YourMinecraftAccessAddon"
 *          ]
 *      }
 * }
 * }</pre>
 *
 * <p>On NeoForge this should be annotated with {@link NeoForge}.
 *
 * @since 1.12.0
 */
public interface MinecraftAccessAddon {
    /**
     * Addon initialiser.
     *
     * @implNote This is called at the end of Minecraft Access initialisation.
     * @param registry Registry for registering items with Minecraft Access.
     * @since 1.12.0
     */
    void init(@NotNull AddonRegistry registry);

    /**
     * Marks a {@link MinecraftAccessAddon} that should be loaded on NeoForge.
     *
     * <p>This is ignored on Fabric.
     * Use {@code fabric.mod.json} as described in {@link MinecraftAccessAddon} instead for Fabric.
     *
     * @since 1.12.0
     */
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.CLASS)
    @interface NeoForge {
    }
}
