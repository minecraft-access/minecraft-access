package com.github.khanshoaib3.minecraft_access.config;

import com.github.khanshoaib3.minecraft_access.config.config_maps.*;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Pure DTO for mapping structure of config json via Gson.
 */
public class ConfigMap {

    private static ConfigMap instance;

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
    @SerializedName("Other Configurations")
    private OtherConfigsMap otherConfigsMap;

    public static ConfigMap buildDefault() {
        ConfigMap m = new ConfigMap();
        m.cameraControlsConfigMap = CameraControlsConfigMap.buildDefault();
        m.inventoryControlsConfigMap = InventoryControlsConfigMap.buildDefault();
        m.mouseSimulationConfigMap = MouseSimulationConfigMap.buildDefault();
        m.poiConfigMap = POIConfigMap.buildDefault();
        m.playerWarningConfigMap = PlayerWarningConfigMap.buildDefault();
        m.fallDetectorConfigMap = FallDetectorConfigMap.buildDefault();
        m.readCrosshairConfigMap = ReadCrosshairConfigMap.buildDefault();
        m.otherConfigsMap = OtherConfigsMap.buildDefault();
        m.narratorMenuConfigMap = NarratorMenuConfigMap.buildDefault();
        return m;
    }

    public static void setInstance(ConfigMap map) {
        CameraControlsConfigMap.setInstance(map.cameraControlsConfigMap);
        FallDetectorConfigMap.setInstance(map.fallDetectorConfigMap);
        InventoryControlsConfigMap.setInstance(map.inventoryControlsConfigMap);
        MouseSimulationConfigMap.setInstance(map.mouseSimulationConfigMap);
        NarratorMenuConfigMap.setInstance(map.narratorMenuConfigMap);
        OtherConfigsMap.setInstance(map.otherConfigsMap);
        PlayerWarningConfigMap.setInstance(map.playerWarningConfigMap);
        POIConfigMap.setInstance(map.poiConfigMap);
        ReadCrosshairConfigMap.setInstance(map.readCrosshairConfigMap);
        instance = map;
    }

    public static ConfigMap getInstance() {
        if (instance == null) Config.getInstance().loadConfig();
        return instance;
    }

    public InventoryControlsConfigMap getInventoryControlsConfigMap() {
        return inventoryControlsConfigMap;
    }

    public MouseSimulationConfigMap getMouseSimulationConfigMap() {
        return mouseSimulationConfigMap;
    }

    public POIConfigMap getPoiConfigMap() {
        return poiConfigMap;
    }

    public PlayerWarningConfigMap getPlayerWarningConfigMap() {
        return playerWarningConfigMap;
    }

    public OtherConfigsMap getOtherConfigsMap() {
        return otherConfigsMap;
    }

    public NarratorMenuConfigMap getNarratorMenuConfigMap() {
        return narratorMenuConfigMap;
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean validate() {
        if (this.cameraControlsConfigMap == null) return false;
        if (this.getInventoryControlsConfigMap() == null) return false;
        if (this.getMouseSimulationConfigMap() == null) return false;
        if (this.getPlayerWarningConfigMap() == null) return false;
        if (this.getOtherConfigsMap() == null) return false;
        if (Objects.isNull(this.readCrosshairConfigMap) || !this.readCrosshairConfigMap.validate()) return false;
        if (Objects.isNull(this.getPoiConfigMap()) || !this.getPoiConfigMap().validate()) return false;
        if (this.fallDetectorConfigMap == null) return false;
        if (this.getNarratorMenuConfigMap() == null) return false;

        return true;
    }
}