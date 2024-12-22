package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AreaMapConfigMap {
    @Setter
    private static AreaMapConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled = true;
    @SerializedName("Delay (in milliseconds)")
    private int delayInMilliseconds = 150;
    /**
     * player y (lower-body) +-2
     */
    @SerializedName("Vertical Distance Bound")
    private int verticalBound = 2;
    /**
     * the default Java Edition render distance is 12 chunks = 192 x 192 blocks
     * 192/2 = 96
     */
    @SerializedName("Horizontal Distance Bound")
    private int horizontalBound = 96;

    private AreaMapConfigMap() {
    }

    public static AreaMapConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public static AreaMapConfigMap buildDefault() {
        AreaMapConfigMap m = new AreaMapConfigMap();
        m.enabled = true;
        m.delayInMilliseconds = 150;
        m.verticalBound = 2;
        m.horizontalBound = 96;

        setInstance(m);
        return m;
    }
}
