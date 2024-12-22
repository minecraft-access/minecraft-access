package com.github.khanshoaib3.minecraft_access.config.config_maps;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryControlsConfigMap {

    @Setter
    private static InventoryControlsConfigMap instance;

    @SerializedName("Enabled")
    private boolean enabled;
    @SerializedName("Auto Open Recipe Book (in creative/survival and crafting inventory)")
    private boolean autoOpenRecipeBook;
    @SerializedName("Row and Column Format in Crafting Input Slots")
    private String rowAndColumnFormat;
    @SerializedName("Speak Focused Slot Changes")
    private boolean speakFocusedSlotChanges = true;
    @SerializedName("Delay (in milliseconds)")
    private int delayInMilliseconds;

    private InventoryControlsConfigMap() {
    }

    public static InventoryControlsConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public static InventoryControlsConfigMap buildDefault() {
        InventoryControlsConfigMap defaultInventoryControlsConfigMap = new InventoryControlsConfigMap();
        defaultInventoryControlsConfigMap.setEnabled(true);
        defaultInventoryControlsConfigMap.setAutoOpenRecipeBook(true);
        defaultInventoryControlsConfigMap.setRowAndColumnFormat("%dx%d");
        defaultInventoryControlsConfigMap.setDelayInMilliseconds(150);
        defaultInventoryControlsConfigMap.speakFocusedSlotChanges = true;

        setInstance(defaultInventoryControlsConfigMap);
        return defaultInventoryControlsConfigMap;
    }
}
