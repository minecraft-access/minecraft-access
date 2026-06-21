package org.mcaccess.minecraftaccess.features;

import java.util.Optional;

import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
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
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.events.ServerChangeDetector;

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
        new ServerChangeDetector<Optional<Holder<@NotNull Biome>>>()
                .levelEvent((client, player, level) -> Optional.ofNullable(getCurrentBiome()), this::onChange);
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
        if (!Config.getInstance().features.biomeIndicatorEnabled || biome.isEmpty()) {
            dimension.update(Optional.of(level.dimension()));
            return;
        }
        String currentBiomeName = biome.flatMap(b -> NarrationUtils.getTranslatedName(b, "biome")).orElse("");
        if (dimension.update(Optional.of(level.dimension())) || Config.getInstance().features.alwaysNarrateDimensionInBiomeIndicator) {
            MainClass.narrate(I18n.get("minecraft_access.biome_indicator.biome_entered",
                    I18n.get("minecraft_access.other.biome_and_dimension",
                            currentBiomeName,
                            I18n.get(level.dimension().identifier().toLanguageKey("dimension"))
                    )
            ), false);
        } else {
            MainClass.narrate(I18n.get("minecraft_access.biome_indicator.biome_entered",
                    I18n.get("minecraft_access.other.biome", currentBiomeName)
            ), false);
        }
    }
}
