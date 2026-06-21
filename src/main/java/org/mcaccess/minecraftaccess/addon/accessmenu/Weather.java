package org.mcaccess.minecraftaccess.addon.accessmenu;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.features.BiomeIndicator;

@Slf4j
public class Weather implements AccessMenuFunction {
    @Override
    public void execute() {
        Holder<Biome> currentBiome = BiomeIndicator.getCurrentBiome();
        Level level = Minecraft.getInstance().level;
        if (Minecraft.getInstance().player == null) return;
        if (level == null) return;
        if (currentBiome == null) return;

        Biome.Precipitation currentPrecipitation = currentBiome.value()
                .getPrecipitationAt(Minecraft.getInstance().player.getOnPos(), level.getSeaLevel());

        String weather;
        if (level.isRaining()) {
            String currentPrecipitationStatus = switch (currentPrecipitation) {
                case NONE -> "clear";
                case RAIN -> {
                    if (level.isThundering()) {
                        yield "thunder";
                    } else {
                        yield "rain";
                    }
                }
                case SNOW -> "snow";
            };

            weather = I18n.get("minecraft_access.weather." + currentPrecipitationStatus);
        } else {
            weather = I18n.get("minecraft_access.weather.clear");
        }

        float moonAngle = level.environmentAttributes().getValue(EnvironmentAttributes.MOON_ANGLE, Minecraft.getInstance().player.position()) % 360;
        if ((moonAngle >= 270 || moonAngle <= 90) && level.dimensionType().skybox() == DimensionType.Skybox.OVERWORLD) {
            MoonPhase moonPhase = level.environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, Minecraft.getInstance().player.getEyePosition());
            weather += I18n.get("minecraft_access.other.words_connection") + I18n.get("minecraft_access.weather.moon_phase." + moonPhase.getSerializedName());
        }

        MainClass.narrate(weather, false);
    }
}
