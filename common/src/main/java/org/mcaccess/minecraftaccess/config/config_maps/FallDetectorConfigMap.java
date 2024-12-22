package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FallDetectorConfigMap {
    @Setter
    private static FallDetectorConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled;
    @SerializedName("Range")
    private int range;
    @SerializedName("Depth Threshold")
    private int depth;
    @SerializedName("Play Alternate Sound")
    private boolean playAlternateSound;
    @SerializedName("Sound Volume")
    private float volume;
    @SerializedName("Delay (in milliseconds)")
    private int delay;

    private FallDetectorConfigMap() {
    }

    public static FallDetectorConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public static FallDetectorConfigMap buildDefault() {
        FallDetectorConfigMap fallDetectorConfigMap = new FallDetectorConfigMap();
        fallDetectorConfigMap.setEnabled(true);
        fallDetectorConfigMap.setRange(6);
        fallDetectorConfigMap.setDepth(4);
        fallDetectorConfigMap.setPlayAlternateSound(true);
        fallDetectorConfigMap.setVolume(0.25f);
        fallDetectorConfigMap.setDelay(2500);

        setInstance(fallDetectorConfigMap);
        return fallDetectorConfigMap;
    }
}
