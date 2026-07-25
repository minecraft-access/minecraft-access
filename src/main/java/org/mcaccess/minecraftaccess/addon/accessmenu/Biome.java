package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.ClientConfig;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.features.BiomeIndicator;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class Biome implements AccessMenuFunction {
    @Override
    public void execute() {
        if (Minecraft.getInstance().level == null) return;

        assert BiomeIndicator.getCurrentBiome() != null;
        String currentBiomeName = NarrationUtils.getTranslatedName(BiomeIndicator.getCurrentBiome(), "biome")
                .orElse("");

        if (ClientConfig.getInstance().features.alwaysNarrateDimensionInBiomeIndicator) {
            ResourceKey<@NotNull Level> currentDimension = Minecraft.getInstance().level.dimension();
            MainClass.narrate(I18n.get("minecraft_access.other.biome_and_dimension",
                    currentBiomeName,
                    I18n.get(currentDimension.identifier().toLanguageKey("dimension"))), false);
        } else {
            MainClass.narrate(I18n.get("minecraft_access.other.biome", currentBiomeName), false);
        }
    }
}
