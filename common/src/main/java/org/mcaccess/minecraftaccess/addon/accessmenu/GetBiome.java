package org.mcaccess.minecraftaccess.addon.accessmenu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.features.BiomeIndicator;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class GetBiome implements AccessMenuFunction {
    @Override
    public void execute() {
        if (Minecraft.getInstance().player == null) return;
        if (Minecraft.getInstance().level == null) return;

        Holder<Biome> currentBiome = BiomeIndicator.getCurrentBiome();
        NarrationUtils.getTranslatedName(currentBiome, "biome")
                .ifPresent(name -> MainClass.narrate(I18n.get("minecraft_access.access_menu.biome", name), true));
    }
}
