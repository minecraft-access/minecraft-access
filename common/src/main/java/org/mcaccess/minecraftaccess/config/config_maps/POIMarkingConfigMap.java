package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class POIMarkingConfigMap {
    @Setter
    private static POIMarkingConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled;

    @SerializedName("Suppress Other POI When Marking On")
    private boolean suppressOtherWhenEnabled;

    private POIMarkingConfigMap() {
    }

    public static POIMarkingConfigMap buildDefault() {
        POIMarkingConfigMap m = new POIMarkingConfigMap();
        m.enabled = true;
        m.suppressOtherWhenEnabled = true;

        // leaf config maps should maintain their singletons during config-reset
        setInstance(m);
        return m;
    }

    public static POIMarkingConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }
}
