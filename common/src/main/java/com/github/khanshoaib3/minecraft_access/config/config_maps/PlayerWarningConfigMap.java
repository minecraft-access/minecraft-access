package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerWarningConfigMap {

    @Setter
    private static PlayerWarningConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled;
    @SerializedName("Play Sound")
    private boolean playSound;
    @SerializedName("Health Threshold First")
    private double firstHealthThreshold;
    @SerializedName("Health Threshold Second")
    private double secondHealthThreshold;
    @SerializedName("Hunger Threshold")
    private double hungerThreshold;
    @SerializedName("Air Threshold")
    private double airThreshold;
    @SerializedName("Frost Threshold")
    private double frostThreshold;

    private PlayerWarningConfigMap() {
    }

    public static PlayerWarningConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public static PlayerWarningConfigMap buildDefault() {
        PlayerWarningConfigMap defaultPlayerWarningConfigMap = new PlayerWarningConfigMap();
        defaultPlayerWarningConfigMap.setEnabled(true);
        defaultPlayerWarningConfigMap.setPlaySound(true);
        defaultPlayerWarningConfigMap.setFirstHealthThreshold(6.0);
        defaultPlayerWarningConfigMap.setSecondHealthThreshold(3.0);
        defaultPlayerWarningConfigMap.setHungerThreshold(3.0);
        defaultPlayerWarningConfigMap.setAirThreshold(5.0);
        defaultPlayerWarningConfigMap.setFrostThreshold(30.0);

        setInstance(defaultPlayerWarningConfigMap);
        return defaultPlayerWarningConfigMap;
    }
}
