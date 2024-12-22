package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class POIBlocksConfigMap {

    @Setter
    private static POIBlocksConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled;
    @SerializedName("Detect Fluid Blocks")
    private boolean detectFluidBlocks;
    @SerializedName("Range")
    private int range;
    @SerializedName("Play Sound")
    private boolean playSound;
    @SerializedName("Sound Volume")
    private float volume;
    @SerializedName("Play Sound for Other Blocks as well")
    private boolean playSoundForOtherBlocks;
    @SerializedName("Delay (in milliseconds)")
    private int delay;

    private POIBlocksConfigMap() {
    }

    public static POIBlocksConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public static POIBlocksConfigMap buildDefault() {
        POIBlocksConfigMap m = new POIBlocksConfigMap();
        m.setEnabled(true);
        m.setDetectFluidBlocks(true);
        m.setRange(6);
        m.setPlaySound(true);
        m.setVolume(0.25f);
        m.setPlaySoundForOtherBlocks(false);
        m.setDelay(3000);

        setInstance(m);
        return m;
    }
}
