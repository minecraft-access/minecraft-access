package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.features.BiomeIndicator;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

public class Biome implements AccessMenuFunction {
    @Override
    public void execute() {
        if (BiomeIndicator.getCurrentBiome() == null) {
            return;
        }

        new Translation("minecraft_access.other.biome")
                .variant("dimension", Config.getInstance().features.alwaysNarrateDimensionInBiomeIndicator)
                .variable("biome").put("biome", BiomeIndicator.getCurrentBiome())
                .variable("dimension").put("dimension", Minecraft.getInstance().level.dimension())
                .narrate(false);
    }
}
