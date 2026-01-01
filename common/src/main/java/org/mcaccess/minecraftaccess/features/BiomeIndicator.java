package org.mcaccess.minecraftaccess.features;

import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

/**
 * Narrates the name of the biome when entering a different biome.
 */
public class BiomeIndicator implements BalmClientModule {
    @Nullable
    private Holder<Biome> previousBiome = null;
    private ResourceKey<Level> previousDimension;

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "biome_indicator");
    }

    @Override
    public void initialize() {
        ClientTickCallback.ClientLevelTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            previousBiome = null;
            previousDimension = null;
        });
    }

    private void tick(Level level) {
        if (!Config.getInstance().features.biomeIndicatorEnabled) {
            return;
        }
        if (Minecraft.getInstance().screen != null) return;

        String narration;

        Holder<Biome> currentBiome = getCurrentBiome();
        if (currentBiome != null && currentBiome != previousBiome) {
            String currentBiomeName = NarrationUtils.getTranslatedName(currentBiome, "biome")
                    .orElse("");
            ResourceKey<Level> currentDimension = level.dimension();
            if (!currentDimension.equals(previousDimension) || Config.getInstance().features.alwaysNarrateDimensionInBiomeIndicator) {
                narration = I18n.get("minecraft_access.other.biome_and_dimension",
                        currentBiomeName,
                        I18n.get(currentDimension.identifier().toLanguageKey("dimension")));
                previousDimension = currentDimension;
            } else {
                narration = I18n.get("minecraft_access.other.biome", currentBiomeName);
            }

            if (!narration.isEmpty()) {
                MainClass.narrate(I18n.get("minecraft_access.biome_indicator.biome_entered", narration), false);
            }

            previousBiome = currentBiome;
        }
    }

    @Nullable
    public static Holder<Biome> getCurrentBiome() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return null;
        if (client.player == null) return null;
        BlockPos pos = client.player.blockPosition();
        LevelChunk currentChunk = client.level.getChunkSource().getChunk(pos.getX() >> 4, pos.getZ() >> 4, false);
        if (currentChunk == null) return null;

        return client.level.getBiome(client.player.blockPosition());
    }
}
