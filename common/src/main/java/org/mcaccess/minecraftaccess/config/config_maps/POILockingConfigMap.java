package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class POILockingConfigMap {

    @Setter
    private static POILockingConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled;
    @SerializedName("Lock on Blocks")
    private boolean lockOnBlocks;
    @SerializedName("Speak Relative Distance to Entity/Block")
    private boolean speakDistance;
    @SerializedName("Play Sound Instead Of Speak")
    private boolean unlockingSound = false;
    @SerializedName("Auto Lock on to Eye of Ender when Used")
    private boolean autoLockEyeOfEnderEntity;
    @SerializedName("Delay (in milliseconds)")
    private int delay;
    @SerializedName("Bow aim assist")
    private boolean aimAssistEnabled;
    @SerializedName("Aim assist audio cues")
    private boolean aimAssistAudioCuesEnabled;
    @SerializedName("Aim assist audio cues volume")
    private float aimAssistAudioCuesVolume;

    private POILockingConfigMap() {
    }

    public static POILockingConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public static POILockingConfigMap buildDefault() {
        POILockingConfigMap m = new POILockingConfigMap();
        m.setEnabled(true);
        m.setLockOnBlocks(true);
        m.setSpeakDistance(false);
        m.setUnlockingSound(false);
        m.setAutoLockEyeOfEnderEntity(true);
        m.setDelay(100);
        m.setAimAssistEnabled(true);
        m.setAimAssistAudioCuesEnabled(true);
        m.setAimAssistAudioCuesVolume(0.5f);

        setInstance(m);
        return m;
    }
}
