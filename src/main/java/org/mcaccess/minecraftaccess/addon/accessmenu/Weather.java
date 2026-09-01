package org.mcaccess.minecraftaccess.addon.accessmenu;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.MoonPhase;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;

import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.features.BiomeIndicator;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

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

        Translation.Delimited narration = new Translation.Delimited()
                .put(new Translation("minecraft_access.weather")
                    .variant(switch (currentPrecipitation) {
                        case Biome.Precipitation ignored when !level.isRaining() -> "clear";
                        case NONE -> "clear";
                        case RAIN -> level.isThundering() ? "thunder" : "rain";
                        case SNOW -> "snow";
                        default -> {
                            log.warn("Unexpected Precipitation type in weather status.");
                            yield null;
                        }
                    })
                );

        float moonAngle = level.environmentAttributes().getValue(EnvironmentAttributes.MOON_ANGLE, Minecraft.getInstance().player.position()) % 360;
        if ((moonAngle >= 270 || moonAngle <= 90) && level.dimensionType().skybox() == DimensionType.Skybox.OVERWORLD) {
            MoonPhase moonPhase = level.environmentAttributes().getValue(EnvironmentAttributes.MOON_PHASE, Minecraft.getInstance().player.getEyePosition());
            narration.put(new Translation("minecraft_access.weather.moon_phase").variant(moonPhase.getSerializedName()));
        }

        narration.narrate(false);
    }
}
