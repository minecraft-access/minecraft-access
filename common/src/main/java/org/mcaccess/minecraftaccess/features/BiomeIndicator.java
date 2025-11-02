package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

/**
 * Narrates the name of the biome when entering a different biome.
 */
@Slf4j
public class BiomeIndicator {
    private static final Minecraft CLIENT = Minecraft.getInstance();
    @Nullable
    private Holder<Biome> previousBiome = null;

    public void tick() {
        if (CLIENT.screen != null) return;

        Holder<Biome> currentBiome = getCurrentBiome();
        if (currentBiome != null && currentBiome != previousBiome) {
            NarrationUtils.getTranslatedName(currentBiome, "biome").ifPresent(name -> MainClass.narrate(I18n.get("minecraft_access.other.biome", name), true));
            previousBiome = currentBiome;
        }
    }

    @Nullable
    public static Holder<Biome> getCurrentBiome() {
        if (CLIENT.level == null) return null;
        if (CLIENT.player == null) return null;
        BlockPos pos = CLIENT.player.blockPosition();
        LevelChunk currentChunk = CLIENT.level.getChunkSource().getChunk(pos.getX() >> 4, pos.getZ() >> 4, false);
        if (currentChunk == null) return null;

        return CLIENT.level.getBiome(CLIENT.player.blockPosition());
    }
}
