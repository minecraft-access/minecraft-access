package io.github.minecraft_access.minecraft_access.features;

import io.github.minecraft_access.minecraft_access.MainClass;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.biome.Biome;

/**
 * Narrates the name of the biome when entering a different biome.
 */
@Slf4j
public class BiomeIndicator {
    private String previousBiome;

    public BiomeIndicator() {
        previousBiome = "";
    }

    public void update() {
        try {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.world == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.currentScreen != null) return;

            RegistryEntry<Biome> var27 = minecraftClient.world.getBiome(minecraftClient.player.getBlockPos());
            String name = I18n.translate(getBiomeName(var27));

            /* Pre 1.19.3
            Identifier id = minecraftClient.world.getRegistryManager().get(Registry.BIOME_KEY).getId(minecraftClient.world.getBiome(minecraftClient.player.getBlockPos()).value()); // Pre 1.19.3
            if (id == null) {
                log.error("\nUnable to detect biome!!\n");
                return;
            }
            String name = I18n.translate("biome." + id.getNamespace() + "." + id.getPath());
            */

            if (!previousBiome.equalsIgnoreCase(name)) {
                previousBiome = name;
                MainClass.speakWithNarrator(I18n.translate("minecraft_access.other.biome", name), true);
            }

        } catch (Exception e) {
            log.error("An error occurred while narrating biome.", e);
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
