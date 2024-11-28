package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.MainClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;
import org.jetbrains.annotations.Nullable;

/**
 * Narrates the name of the biome when entering a different biome.
 */
@Slf4j
public class BiomeIndicator {
    @Nullable
    private String previousBiome = null;

    public BiomeIndicator() {
    }

    public void update() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.world == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.currentScreen != null) return;

        RegistryEntry<Biome> var27 = minecraftClient.world.getBiome(minecraftClient.player.getBlockPos());
        String name = I18n.translate(getBiomeName(var27));

        if (previousBiome == null) {
            previousBiome = name;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.other.biome", name), true);
        } else if (!previousBiome.equalsIgnoreCase(name)) {
            previousBiome = name;
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.other.biome", name), true);
        }
    }

    /**
     * Gets the biome name from registry entry
     *
     * @param biome the biome's registry entry
     * @return the biome's name
     */
    public static String getBiomeName(RegistryEntry<Biome> biome) {
        return I18n.translate(getBiomeTranslationKey(biome));
    }

    /**
     * Gets the biome translation key from registry entry
     *
     * @param biome the biome's registry entry
     * @return the biome's translation key
     */
    private static String getBiomeTranslationKey(RegistryEntry<Biome> biome) {
        return biome.getKeyOrValue().map(
                (biomeKey) -> "biome." + biomeKey.getValue().getNamespace() + "." + biomeKey.getValue().getPath(),
                (biomeValue) -> "[unregistered " + biomeValue + "]" // For unregistered biome
        );
    }
}
