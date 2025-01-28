package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.MainClass;

/**
 * Narrates the name of the biome when entering a different biome.
 */
@Slf4j
public class BiomeIndicator {
    @Nullable
    private String previousBiome = null;

    public void update() {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.level == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen != null) return;

        Holder<Biome> var27 = minecraftClient.level.getBiome(minecraftClient.player.blockPosition());
        String name = I18n.get(getBiomeName(var27));

        if (!name.equalsIgnoreCase(previousBiome)) {
            previousBiome = name;
            MainClass.speakWithNarrator(I18n.get("minecraft_access.other.biome", name), true);
        }
    }

    /**
     * Gets the biome name from registry entry
     *
     * @param biome the biome's registry entry
     * @return the biome's name
     */
    public static String getBiomeName(Holder<Biome> biome) {
        return I18n.get(getBiomeTranslationKey(biome));
    }

    /**
     * Gets the biome translation key from registry entry
     *
     * @param biome the biome's registry entry
     * @return the biome's translation key
     */
    private static String getBiomeTranslationKey(Holder<Biome> biome) {
        return biome.unwrap().map(
                (biomeKey) -> "biome." + biomeKey.location().getNamespace() + "." + biomeKey.location().getPath(),
                (biomeValue) -> "[unregistered " + biomeValue + "]" // For unregistered biome
        );
    }
}
