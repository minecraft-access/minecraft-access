package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RCRelativePositionSoundCueConfigMap {
    @Setter
    private static RCRelativePositionSoundCueConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled = true;
    @SerializedName("Min Sound Volume")
    private float minSoundVolume = 0.25f;
    @SerializedName("Max Sound Volume")
    private float maxSoundVolume = 0.4f;

    private RCRelativePositionSoundCueConfigMap() {
    }

    public static RCRelativePositionSoundCueConfigMap buildDefault() {
        RCRelativePositionSoundCueConfigMap m = new RCRelativePositionSoundCueConfigMap();
        m.enabled = true;
        m.minSoundVolume = 0.25f;
        m.maxSoundVolume = 0.4f;
        setInstance(m);
        return m;
    }

    public static RCRelativePositionSoundCueConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }
}
