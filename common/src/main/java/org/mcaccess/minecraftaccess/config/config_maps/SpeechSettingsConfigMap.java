package org.mcaccess.minecraftaccess.config.config_maps;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;
import org.mcaccess.minecraftaccess.config.Config;

@Getter
@Setter
public class SpeechSettingsConfigMap {
    @Setter
    private static SpeechSettingsConfigMap instance;

    @SerializedName("Speech Rate")
    private float speechRate;

    private SpeechSettingsConfigMap() {
    }

    public static SpeechSettingsConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public static SpeechSettingsConfigMap buildDefault() {
        SpeechSettingsConfigMap m = new SpeechSettingsConfigMap();
        m.setSpeechRate(50.0f);

        setInstance(m);
        return m;
    }
}
