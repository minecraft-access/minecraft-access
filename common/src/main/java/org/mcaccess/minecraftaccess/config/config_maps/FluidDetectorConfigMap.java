package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FluidDetectorConfigMap {

    @Setter
    private static FluidDetectorConfigMap instance;

    @SerializedName("Sound Volume")
    private float volume;
    @SerializedName("Range")
    private int range;

    private FluidDetectorConfigMap() {
    }

    public static FluidDetectorConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public static FluidDetectorConfigMap buildDefault() {
        FluidDetectorConfigMap defaultFluidDetectorConfigMap = new FluidDetectorConfigMap();
        defaultFluidDetectorConfigMap.setRange(10);
        defaultFluidDetectorConfigMap.setVolume(0.25f);

        setInstance(defaultFluidDetectorConfigMap);
        return defaultFluidDetectorConfigMap;
    }
}
