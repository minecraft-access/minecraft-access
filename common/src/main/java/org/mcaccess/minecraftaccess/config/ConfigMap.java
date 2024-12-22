package com.github.khanshoaib3.minecraft_access.config;

import com.github.khanshoaib3.minecraft_access.config.config_maps.*;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Pure DTO for mapping structure of config json via Gson.
 */
public class ConfigMap {

    private static ConfigMap instance;

    @SerializedName("Speech Settings")
    private SpeechSettingsConfigMap speechSettingsConfigMap;
    @SerializedName("Camera Controls")
    private CameraControlsConfigMap cameraControlsConfigMap;
    @SerializedName("Inventory Controls")
    private InventoryControlsConfigMap inventoryControlsConfigMap;
    @SerializedName("Mouse Simulation")
    private MouseSimulationConfigMap mouseSimulationConfigMap;
    @SerializedName("Points of Interest")
    private POIConfigMap poiConfigMap;
    @SerializedName("Player Warnings")
    private PlayerWarningConfigMap playerWarningConfigMap;
    @SerializedName("Fall Detector")
    private FallDetectorConfigMap fallDetectorConfigMap;
    @SerializedName("Read Crosshair")
    private ReadCrosshairConfigMap readCrosshairConfigMap;
    @SerializedName("Narrator Menu")
    private NarratorMenuConfigMap narratorMenuConfigMap;
    @SerializedName("Area Map")
    private AreaMapConfigMap areaMapConfigMap;
    @SerializedName("Other Configurations")
    private OtherConfigsMap otherConfigsMap;

    public static ConfigMap buildDefault() {
        ConfigMap m = new ConfigMap();
        m.speechSettingsConfigMap = SpeechSettingsConfigMap.buildDefault();
        m.cameraControlsConfigMap = CameraControlsConfigMap.buildDefault();
        m.inventoryControlsConfigMap = InventoryControlsConfigMap.buildDefault();
        m.mouseSimulationConfigMap = MouseSimulationConfigMap.buildDefault();
        m.poiConfigMap = POIConfigMap.buildDefault();
        m.playerWarningConfigMap = PlayerWarningConfigMap.buildDefault();
        m.fallDetectorConfigMap = FallDetectorConfigMap.buildDefault();
        m.readCrosshairConfigMap = ReadCrosshairConfigMap.buildDefault();
        m.otherConfigsMap = OtherConfigsMap.buildDefault();
        m.narratorMenuConfigMap = NarratorMenuConfigMap.buildDefault();
        m.areaMapConfigMap = AreaMapConfigMap.buildDefault();
        return m;
    }

    public static void setInstance(ConfigMap map) {
        SpeechSettingsConfigMap.setInstance(map.speechSettingsConfigMap);
        CameraControlsConfigMap.setInstance(map.cameraControlsConfigMap);
        FallDetectorConfigMap.setInstance(map.fallDetectorConfigMap);
        InventoryControlsConfigMap.setInstance(map.inventoryControlsConfigMap);
        MouseSimulationConfigMap.setInstance(map.mouseSimulationConfigMap);
        NarratorMenuConfigMap.setInstance(map.narratorMenuConfigMap);
        OtherConfigsMap.setInstance(map.otherConfigsMap);
        PlayerWarningConfigMap.setInstance(map.playerWarningConfigMap);
        POIConfigMap.setInstance(map.poiConfigMap);
        ReadCrosshairConfigMap.setInstance(map.readCrosshairConfigMap);
        AreaMapConfigMap.setInstance(map.areaMapConfigMap);
        instance = map;
    }

    public static ConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    /**
     * Some XxxConfigMap class variable may be missing duo to new features released with new config section or so.
     * Only reset these missing sections to reduce the burden on users whenever new config section is introduced.
     */
    public void resetMissingSectionsToDefault() {
        if (Objects.isNull(this.speechSettingsConfigMap)) {
            this.speechSettingsConfigMap = SpeechSettingsConfigMap.buildDefault();
        }
        if (Objects.isNull(this.cameraControlsConfigMap)) {
            this.cameraControlsConfigMap = CameraControlsConfigMap.buildDefault();
        }
        if (Objects.isNull(this.inventoryControlsConfigMap)) {
            this.inventoryControlsConfigMap = InventoryControlsConfigMap.buildDefault();
        }
        if (Objects.isNull(this.mouseSimulationConfigMap)) {
            this.mouseSimulationConfigMap = MouseSimulationConfigMap.buildDefault();
        }
        if (Objects.isNull(this.playerWarningConfigMap)) {
            this.playerWarningConfigMap = PlayerWarningConfigMap.buildDefault();
        }
        if (Objects.isNull(this.otherConfigsMap)) {
            this.otherConfigsMap = OtherConfigsMap.buildDefault();
        }
        if (Objects.isNull(this.readCrosshairConfigMap)) {
            this.readCrosshairConfigMap = ReadCrosshairConfigMap.buildDefault();
        } else {
            this.readCrosshairConfigMap.resetMissingSectionsToDefault();
        }
        if (Objects.isNull(this.poiConfigMap)) {
            this.poiConfigMap = POIConfigMap.buildDefault();
        } else {
            this.poiConfigMap.resetMissingSectionsToDefault();
        }
        if (Objects.isNull(this.fallDetectorConfigMap)) {
            this.fallDetectorConfigMap = FallDetectorConfigMap.buildDefault();
        }
        if (Objects.isNull(this.narratorMenuConfigMap)) {
            this.narratorMenuConfigMap = NarratorMenuConfigMap.buildDefault();
        } else {
            this.narratorMenuConfigMap.resetMissingSectionsToDefault();
        }
//        if (Objects.isNull(this.areaMapConfigMap)) {
//            this.areaMapConfigMap = AreaMapConfigMap.buildDefault();
//        }
    }
}