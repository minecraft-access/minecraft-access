package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class ReadCrosshairConfigMap {
    private static ReadCrosshairConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled;
    @SerializedName("Use Jade")
    private boolean useJade;
    @SerializedName("Speak Block Sides")
    private boolean speakSide;
    @SerializedName("Disable Speaking Consecutive Blocks With Same Name")
    private boolean disableSpeakingConsecutiveBlocks;
    @SerializedName("Repeat Speaking Interval (in milliseconds) (0 to disable)")
    private long repeatSpeakingInterval;
    @SerializedName("Relative Position Sound Cue")
    private RCRelativePositionSoundCueConfigMap relativePositionSoundCueConfigMap;
    @SerializedName("Partial Speaking")
    private RCPartialSpeakingConfigMap partialSpeakingConfigMap;

    private ReadCrosshairConfigMap() {
    }

    public static ReadCrosshairConfigMap buildDefault() {
        ReadCrosshairConfigMap m = new ReadCrosshairConfigMap();
        m.setEnabled(true);
        m.setUseJade(true);
        m.setSpeakSide(true);
        m.setDisableSpeakingConsecutiveBlocks(false);
        m.setRepeatSpeakingInterval(0L);
        m.relativePositionSoundCueConfigMap = RCRelativePositionSoundCueConfigMap.buildDefault();
        m.partialSpeakingConfigMap = RCPartialSpeakingConfigMap.buildDefault();

        setInstance(m);
        return m;
    }

    public static ReadCrosshairConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public static void setInstance(ReadCrosshairConfigMap map) {
        RCRelativePositionSoundCueConfigMap.setInstance(map.relativePositionSoundCueConfigMap);
        RCPartialSpeakingConfigMap.setInstance(map.partialSpeakingConfigMap);
        instance = map;
    }

    public void resetMissingSectionsToDefault() {
        if (Objects.isNull(this.partialSpeakingConfigMap)) {
            this.partialSpeakingConfigMap = RCPartialSpeakingConfigMap.buildDefault();
        }
        if (Objects.isNull(this.relativePositionSoundCueConfigMap)) {
            this.relativePositionSoundCueConfigMap = RCRelativePositionSoundCueConfigMap.buildDefault();
        }
    }
}
