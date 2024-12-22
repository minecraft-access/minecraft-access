package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class POIEntitiesConfigMap {

    @Setter
    private static POIEntitiesConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled;
    @SerializedName("Range")
    private int range;
    @SerializedName("Play Sound")
    private boolean playSound;
    @SerializedName("Volume")
    private float volume;
    @SerializedName("Delay (in milliseconds)")
    private int delay;

    private POIEntitiesConfigMap() {
    }

    public static POIEntitiesConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public static POIEntitiesConfigMap buildDefault() {
        POIEntitiesConfigMap m = new POIEntitiesConfigMap();
        m.setEnabled(true);
        m.setRange(6);
        m.setPlaySound(true);
        m.setVolume(0.25f);
        m.setDelay(3000);

        setInstance(m);
        return m;
    }
}
