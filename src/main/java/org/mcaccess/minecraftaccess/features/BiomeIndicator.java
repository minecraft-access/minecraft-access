package org.mcaccess.minecraftaccess.features;

import java.util.Optional;

import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.events.ServerChangeDetector;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

/**
 * Narrates the name of the biome when entering a different biome.
 */
public class BiomeIndicator implements BalmClientModule {
    private final ServerChangeDetector<Optional<ResourceKey<@NotNull Level>>> dimension = new ServerChangeDetector<>(Optional::empty);

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "biome_indicator");
    }

    @Override
    public void initialize() {
        new ServerChangeDetector<Optional<Holder<@NotNull Biome>>>(Optional::empty)
                .levelEvent((_, _, _) -> Optional.ofNullable(getCurrentBiome()), this::onChange);
    }

    @Nullable
    public static Holder<@NotNull Biome> getCurrentBiome() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return null;
        if (client.player == null) return null;
        BlockPos pos = client.player.blockPosition();
        LevelChunk currentChunk = client.level.getChunkSource().getChunk(pos.getX() >> 4, pos.getZ() >> 4, false);
        if (currentChunk == null) return null;

        return client.level.getBiome(client.player.blockPosition());
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private void onChange(Minecraft client, Player player, Level level, Optional<Holder<@NotNull Biome>> previous, Optional<Holder<@NotNull Biome>> biome) {
        if (biome.isEmpty()) {
            return;
        }
        if (!Config.getInstance().features.biomeIndicatorEnabled || biome.isEmpty()) {
            dimension.update(Optional.of(level.dimension()));
            return;
        }
        new Translation("minecraft_access.biome_indicator.biome_entered")
                .variant("with_dimension", dimension.update(Optional.of(level.dimension())) || Config.getInstance().features.alwaysNarrateDimensionInBiomeIndicator)
                .variable("biome").put("biome", biome.get())
                .variable("dimension").put("dimension", level.dimension())
                .narrate(false);
    }
}
