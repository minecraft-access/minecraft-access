package org.mcaccess.minecraftaccess.api;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * A status to be narrated in the player status readout or to trigger warnings.
 *
 * @see AddonRegistry#register(ResourceLocation, Status)
 * @since 1.12.0
 */
public interface Status {
    /**
     * The message to be narrated to the player.
     *
     * <p>This should in most cases contain the name of the status (E.g. "Health") along with some value.
     *
     * @return The message to be narrated.
     * @since 1.12.0
     */
    @Contract(pure = true)
    @NotNull String message();

    /**
     * Weather this status should appear in the player status readout.
     *
     * @implSpec Returns {@code true} by default.
     * @return {@code true} if it should be included, {@code false} if it should be hidden.
     * @since 1.12.0
     */
    @Contract(pure = true)
    default boolean show() {
        return true;
    }

    /**
     * Weather this status should be included in the conditional status readout.
     *
     * <p>It will always be excluded if {@link #show()} is {@code false} regardless of the result of this method.
     *
     * @implSpec Returns {@code false} by default.
     * @return {@code true} if it should be included, {@code false} otherwise.
     * @since 1.12.0
     */
    @Contract(pure = true)
    default boolean important() {
        return false;
    }

    /**
     * Returns the current warning severity for this status.
     *
     * <p>A warning is triggered whenever the returned {@link WarningLevel} increases from the previous tick.
     * When a warning is triggered, a sound is played depending on the {@link WarningLevel} and the {@link #message()} is narrated.
     *
     * @implSpec The default implementation always returns {@link WarningLevel#NONE}, effectively disabling warnings for this status.
     * @return The current warning severity.
     * @see WarningLevel
     * @since 1.12.0
     */
    @Contract(pure = true)
    default @NotNull WarningLevel warning() {
        return WarningLevel.NONE;
    }

    /**
     * Represents a warning level from {@link Status#warning()}.
     *
     * @see Status#warning()
     * @since 1.12.0
     */
    enum WarningLevel {
        /**
         * No warning.
         *
         * <p>No sound will ever be played nor will anything be narrated.
         *
         * @since 1.12.0
         */
        NONE,

        /**
         * A regular warning.
         *
         * <p>The non-severe warning sound will be played and {@link Status#message()} will be narrated.
         *
         * @since 1.12.0
         */
        WARNING,

        /**
         * A critical warning.
         *
         * <p>The severe warning sound will be played and {@link Status#message()} will be narrated.
         *
         * @since 1.12.0
         */
        CRITICAL,

        /**
         * A final warning.
         *
         * <p>Behaves the same as {@link #CRITICAL}.
         * The severe warning sound will be played and {@link Status#message()} will be narrated.
         *
         * @since 1.12.0
         */
        FINAL
    }
}
