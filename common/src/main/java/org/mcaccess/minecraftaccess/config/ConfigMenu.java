package com.github.khanshoaib3.minecraft_access.config;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_menus.*;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import com.github.khanshoaib3.minecraft_access.utils.system.OsUtils;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;

@SuppressWarnings("DataFlowIssue")
public class ConfigMenu extends BaseScreen {
    public ConfigMenu(String title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        if (OsUtils.isMacOS()) {
            ButtonWidget speechSettingsButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.speech_settings_button",
                    (button) -> this.client.setScreen(new SpeechSettingsConfigMenu("speech_settings_config_menu", this)));
            this.addDrawableChild(speechSettingsButton);
        }

        ButtonWidget cameraControlsButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.camera_controls_button",
                (button) -> this.client.setScreen(new CameraControlsConfigMenu("camera_controls_config_menu", this)));
        this.addDrawableChild(cameraControlsButton);

        ButtonWidget inventoryControlsButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.inventory_controls_button",
                (button) -> this.client.setScreen(new InventoryControlsConfigMenu("inventory_controls_config_menu", this)));
        this.addDrawableChild(inventoryControlsButton);

        ButtonWidget mouseSimulationButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.mouse_simulation_button",
                (button) -> this.client.setScreen(new MouseSimulationMenu("mouse_simulation_config_menu", this)));
        this.addDrawableChild(mouseSimulationButton);

        ButtonWidget poiButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.poi_button",
                (button) -> this.client.setScreen(new POIConfigMenu("poi_config_menu", this)));
        this.addDrawableChild(poiButton);

        ButtonWidget playerWarningsButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.player_warnings_button",
                (button) -> this.client.setScreen(new PlayerWarningsConfigMenu("player_warnings_config_menu", this)));
        this.addDrawableChild(playerWarningsButton);

        ButtonWidget fallDetectorButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.fall_detector_button",
                (button) -> this.client.setScreen(new FallDetectorConfigMenu("fall_detector_config_menu", this)));
        this.addDrawableChild(fallDetectorButton);

        ButtonWidget readCrosshairButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.read_crosshair_button",
                (button) -> this.client.setScreen(new ReadCrosshairConfigMenu("read_crosshair_config_menu", this)));
        this.addDrawableChild(readCrosshairButton);

        ButtonWidget narratorMenuButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.access_menu_button",
                (button) -> this.client.setScreen(new NarratorMenuConfigMenu("access_menu_config_menu", this)));
        this.addDrawableChild(narratorMenuButton);

//        ButtonWidget areaMapButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.area_map_button",
//                (button) -> this.client.setScreen(new AreaMapConfigMenu("area_map_config_menu", this)));
//        this.addDrawableChild(areaMapButton);

        ButtonWidget otherButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.other_button",
                (button) -> this.client.setScreen(new OtherConfigMenu("other_config_menu", this)));
        this.addDrawableChild(otherButton);

        ButtonWidget resetConfigButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.reset_config_button",
                (button) -> {
                    Config.getInstance().resetConfigToDefault();
                    MainClass.speakWithNarrator(I18n.translate("minecraft_access.gui.config_menu.info.reset_config_text"), true);
                    this.client.setScreen(null);
                },
                true);
        this.addDrawableChild(resetConfigButton);
    }
}
