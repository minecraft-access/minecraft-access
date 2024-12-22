package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RCPartialSpeakingConfigMap {
    @Setter
    private static RCPartialSpeakingConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled;
    @SerializedName("White List Mode")
    private boolean partialSpeakingWhitelistMode;
    @SerializedName("Fuzzy Matching Mode")
    private boolean partialSpeakingFuzzyMode;
    @SerializedName("Target Mode")
    private PartialSpeakingTargetMode partialSpeakingTargetMode;
    @SerializedName("Targets")
    private List<String> partialSpeakingTargets;

    private RCPartialSpeakingConfigMap() {
    }

    public static RCPartialSpeakingConfigMap buildDefault() {
        RCPartialSpeakingConfigMap m = new RCPartialSpeakingConfigMap();
        m.enabled = false;
        m.partialSpeakingWhitelistMode = true;
        m.partialSpeakingFuzzyMode = true;
        m.partialSpeakingTargets = List.of("slab", "planks", "block", "stone", "sign");
        m.partialSpeakingTargetMode = PartialSpeakingTargetMode.BLOCK;

        setInstance(m);
        return m;
    }

    public static RCPartialSpeakingConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public enum PartialSpeakingTargetMode {
        @SerializedName("all")
        ALL,
        @SerializedName("entity")
        ENTITY,
        @SerializedName("block")
        BLOCK,
    }
}
