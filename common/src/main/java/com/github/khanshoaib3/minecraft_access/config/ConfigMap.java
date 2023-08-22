package com.github.khanshoaib3.minecraft_access.config;

import com.github.khanshoaib3.minecraft_access.config.config_maps.*;
import com.google.gson.annotations.SerializedName;

import java.util.Objects;

/**
 * Pure DTO for mapping structure of config json via Gson.
 */
public class ConfigMap {
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
        m.setDefaultPlayerWarningConfigMap();
        m.fallDetectorConfigMap = FallDetectorConfigMap.buildDefault();
        m.readCrosshairConfigMap = ReadCrosshairConfigMap.buildDefault();
        m.setOtherConfigsMap(OtherConfigsMap.getDefaultOtherConfigsMap());
        m.narratorMenuConfigMap = NarratorMenuConfigMap.buildDefault();
        return m;
    }

    public static void setInstance(ConfigMap map) {
        CameraControlsConfigMap.setInstance(map.cameraControlsConfigMap);
        FallDetectorConfigMap.setInstance(map.fallDetectorConfigMap);
        InventoryControlsConfigMap.setInstance(map.inventoryControlsConfigMap);
        MouseSimulationConfigMap.setInstance(map.mouseSimulationConfigMap);
        NarratorMenuConfigMap.setInstance(map.narratorMenuConfigMap);
        POIConfigMap.setInstance(map.poiConfigMap);
        ReadCrosshairConfigMap.setInstance(map.readCrosshairConfigMap);
    }

    public CameraControlsConfigMap getCameraControlsConfigMap() {
        return cameraControlsConfigMap;
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

    public void setPlayerWarningConfigMap(PlayerWarningConfigMap playerWarningConfigMap) {
        this.playerWarningConfigMap = playerWarningConfigMap;
    }

    public void setDefaultPlayerWarningConfigMap() {
        PlayerWarningConfigMap defaultPlayerWarningConfigMap = new PlayerWarningConfigMap();
        defaultPlayerWarningConfigMap.setEnabled(true);
        defaultPlayerWarningConfigMap.setPlaySound(true);
        defaultPlayerWarningConfigMap.setFirstHealthThreshold(6.0);
        defaultPlayerWarningConfigMap.setSecondHealthThreshold(3.0);
        defaultPlayerWarningConfigMap.setHungerThreshold(3.0);
        defaultPlayerWarningConfigMap.setAirThreshold(3.0);

        setPlayerWarningConfigMap(defaultPlayerWarningConfigMap);
    }

    public FallDetectorConfigMap getFallDetectorConfigMap() {
        return fallDetectorConfigMap;
    }

    public ReadCrosshairConfigMap getReadCrosshairConfigMap() {
        return readCrosshairConfigMap;
    }

    public OtherConfigsMap getOtherConfigsMap() {
        return otherConfigsMap;
    }

    public void setOtherConfigsMap(OtherConfigsMap otherConfigsMap) {
        this.otherConfigsMap = otherConfigsMap;
    }

    public NarratorMenuConfigMap getNarratorMenuConfigMap() {
        return narratorMenuConfigMap;
    }

    @SuppressWarnings("RedundantIfStatement")
    public boolean validate() {
        if (this.getCameraControlsConfigMap() == null) return false;
        if (this.getInventoryControlsConfigMap() == null) return false;
        if (this.getMouseSimulationConfigMap() == null) return false;
        if (this.getPlayerWarningConfigMap() == null) return false;
        if (this.getOtherConfigsMap() == null) return false;
        if (Objects.isNull(this.getReadCrosshairConfigMap()) || !this.getReadCrosshairConfigMap().validate()) return false;
        if (Objects.isNull(this.getPoiConfigMap()) || !this.getPoiConfigMap().validate()) return false;
        if (this.getFallDetectorConfigMap() == null) return false;
        if (this.getNarratorMenuConfigMap() == null) return false;

        return true;
    }
}