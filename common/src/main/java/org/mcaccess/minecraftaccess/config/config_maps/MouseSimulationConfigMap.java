package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MouseSimulationConfigMap {
    @Setter
    private static MouseSimulationConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled;
    @SerializedName("Scroll Delay (in milliseconds)")
    private int scrollDelayInMilliseconds;

    private MouseSimulationConfigMap() {
    }

    public static MouseSimulationConfigMap buildDefault() {
        MouseSimulationConfigMap m = new MouseSimulationConfigMap();
        m.enabled = true;
        m.scrollDelayInMilliseconds = 150;

        setInstance(m);
        return m;
    }

    public static MouseSimulationConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }
}
