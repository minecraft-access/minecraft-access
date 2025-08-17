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
    @Nullable
    private Holder<Biome> previousBiome = null;

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        if (client.player == null) return;
        if (client.screen != null) return;
        BlockPos pos = client.player.blockPosition();
        LevelChunk currentChunk = client.level.getChunkSource().getChunk(pos.getX() >> 4, pos.getZ() >> 4, false);
        if (currentChunk == null) return;

        Holder<Biome> currentBiome = client.level.getBiome(client.player.blockPosition());
        if (currentBiome != previousBiome) {
            previousBiome = currentBiome;
            NarrationUtils.getTranslatedName(currentBiome, "biome").ifPresent(name -> MainClass.narrate(I18n.get("minecraft_access.other.biome", name), true));
        }
    }
}
