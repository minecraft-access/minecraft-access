package org.mcaccess.minecraftaccess.config.config_maps;

import org.mcaccess.minecraftaccess.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class AccessMenuConfigMap {

    private static AccessMenuConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled;
    @SerializedName("Fluid Detector")
    private FluidDetectorConfigMap fluidDetectorConfigMap;

    private AccessMenuConfigMap() {
    }

    public static AccessMenuConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public static void setInstance(AccessMenuConfigMap map) {
        FluidDetectorConfigMap.setInstance(map.fluidDetectorConfigMap);
        instance = map;
    }

    public static AccessMenuConfigMap buildDefault() {
        AccessMenuConfigMap defaultAccessMenuConfigMap = new AccessMenuConfigMap();
        defaultAccessMenuConfigMap.setEnabled(true);
        defaultAccessMenuConfigMap.fluidDetectorConfigMap = FluidDetectorConfigMap.buildDefault();

        setInstance(defaultAccessMenuConfigMap);
        return defaultAccessMenuConfigMap;
    }

    public void resetMissingSectionsToDefault() {
        if (Objects.isNull(this.fluidDetectorConfigMap)) {
            this.fluidDetectorConfigMap = FluidDetectorConfigMap.buildDefault();
        }
    }
}
