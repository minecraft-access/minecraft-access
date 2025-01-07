package org.mcaccess.minecraftaccess.config.config_maps;

import org.mcaccess.minecraftaccess.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PosChangeCheckerConfigMap {
    @Setter
    private static PosChangeCheckerConfigMap instance;

    @SerializedName("Play a sound when the X coordinate changes")
    private boolean playSoundForXChanges;
    @SerializedName("Play a sound when the Y coordinate changes")
    private boolean playSoundForYChanges;
    @SerializedName("Play a sound when the Z coordinate changes")
    private boolean playSoundForZChanges;

    private PosChangeCheckerConfigMap() {
    }

    public static PosChangeCheckerConfigMap getInstance() {
        if (instance == null)
            Config.getInstance().loadConfig();
        return instance;
    }

    public static PosChangeCheckerConfigMap buildDefault() {
        PosChangeCheckerConfigMap config = new PosChangeCheckerConfigMap();
        config.playSoundForXChanges = false;
        config.playSoundForYChanges = false;
        config.playSoundForZChanges = false;
        return config;
    }
}
