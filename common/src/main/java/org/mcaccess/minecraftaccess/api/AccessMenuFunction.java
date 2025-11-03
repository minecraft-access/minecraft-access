package org.mcaccess.minecraftaccess.api;

import org.jetbrains.annotations.Contract;

/**
 * A function triggerable from the Access Menu or a keybind.
 *
 * @since 1.12.0
 */
public interface AccessMenuFunction {
    /**
     * Executed when this function is triggered.
     *
     * @implSpec {@link #enabled()} is always checked before calling this.
     * @since 1.12.0
     */
    void execute();

    /**
     * Weather this function is enabled.
     *
     * @return {@code true} to enable, {@code false} to disable.
     * @since 1.12.0
     */
    @Contract(pure = true)
    default boolean enabled() {
        return true;
    }
}
