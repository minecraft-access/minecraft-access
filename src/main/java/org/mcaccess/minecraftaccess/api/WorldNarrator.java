package org.mcaccess.minecraftaccess.api;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides narration for blocks and entities.
 *
 * @see AddonRegistry#register(String, WorldNarrator)
 * @since 1.12.0
 */
public interface WorldNarrator {
    /**
     * Preforms a ray cast to determine what the player is looking at.
     *
     * <p>A value of {@code null} is equivalent to a {@link HitResult} with type {@link HitResult.Type#MISS}
     * and indicates that the player is not looking at anything.
     *
     * @return A {@link HitResult} for what the player is looking at or {@code null}.
     * @since 1.12.0
     */
    @Contract(pure = true)
    @Nullable HitResult rayCast();

    /**
     * Generates a description of the block or entity the player is currently looking at to be narrated.
     *
     * <p>A value of {@code null} indicates that there is no narration available.
     *
     * @param rayCast Ray cast from {@link #rayCast()}.
     * @return A string to be narrated to the player or null.
     * @since 1.12.0
     */
    @Contract(pure = true)
    @Nullable String narrate(@NotNull HitResult rayCast);

    /**
     * Generates a description of a given block to be narrated.
     *
     * @param block The block to narrate.
     * @return A string to be narrated to the player.
     * @since 1.12.0
     */
    @Contract(pure = true)
    @NotNull String narrate(@NotNull BlockPos block);

    /**
     * Generates a description of a given entity to be narrated.
     *
     * @param entity The entity to narrate.
     * @return A string to be narrated to the player.
     * @since 1.12.0
     */
    @Contract(pure = true)
    @NotNull String narrate(@NotNull Entity entity);
}
