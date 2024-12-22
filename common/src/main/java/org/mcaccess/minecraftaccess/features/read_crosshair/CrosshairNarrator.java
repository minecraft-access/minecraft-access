package com.github.khanshoaib3.minecraft_access.features.read_crosshair;

import net.minecraft.util.hit.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface CrosshairNarrator {
    @NotNull HitResult rayCast();

    /**
     * Determines weather the player is looking at the same block or entity.
     * {@link #narrate(boolean)} will only be called
     * if this method returns a different non-null value than its previous call.
     * A value of null indicates that the player is not currently looking at any block
     * and thus {@link #narrate(boolean)} MUST NOT be called.
     * @param speakSide Whether the block side the player is pointing at should be narrated
     * @return Any object comparable with {@link java.util.Objects#equals(Object, Object)} or null
     */
    @Nullable Object deduplication(boolean speakSide, boolean speakConsecutiveBlocks);

    /**
     * Generates a description of the block or entity the player is currently looking at to be read out.
     * This will never be called if {@link #deduplication(boolean, boolean)} returns null.
     * @param speakSide Weather the block side the player is pointing at should be narrated
     * @return A non-null string to be read to the player
     */
    @NotNull String narrate(boolean speakSide);
}
