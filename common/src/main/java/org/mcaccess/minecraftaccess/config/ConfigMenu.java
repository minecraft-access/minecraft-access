package org.mcaccess.minecraftaccess.config;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.config.config_menus.*;
import org.mcaccess.minecraftaccess.utils.BaseScreen;
import org.mcaccess.minecraftaccess.utils.system.OsUtils;

@SuppressWarnings("DataFlowIssue")
public class ConfigMenu extends BaseScreen {
    public ConfigMenu(String title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();

        if (OsUtils.isMacOS()) {
            Button speechSettingsButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.speech_settings_button",
                    (button) -> this.minecraft.setScreen(new SpeechSettingsConfigMenu("speech_settings_config_menu", this)));
            this.addRenderableWidget(speechSettingsButton);
        }

        Button cameraControlsButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.camera_controls_button",
                (button) -> this.minecraft.setScreen(new CameraControlsConfigMenu("camera_controls_config_menu", this)));
        this.addRenderableWidget(cameraControlsButton);

        Button inventoryControlsButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.inventory_controls_button",
                (button) -> this.minecraft.setScreen(new InventoryControlsConfigMenu("inventory_controls_config_menu", this)));
        this.addRenderableWidget(inventoryControlsButton);

        Button mouseSimulationButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.mouse_simulation_button",
                (button) -> this.minecraft.setScreen(new MouseSimulationMenu("mouse_simulation_config_menu", this)));
        this.addRenderableWidget(mouseSimulationButton);

        Button poiButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.poi_button",
                (button) -> this.minecraft.setScreen(new POIConfigMenu("poi_config_menu", this)));
        this.addRenderableWidget(poiButton);

        Button playerWarningsButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.player_warnings_button",
                (button) -> this.minecraft.setScreen(new PlayerWarningsConfigMenu("player_warnings_config_menu", this)));
        this.addRenderableWidget(playerWarningsButton);

        Button fallDetectorButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.fall_detector_button",
                (button) -> this.minecraft.setScreen(new FallDetectorConfigMenu("fall_detector_config_menu", this)));
        this.addRenderableWidget(fallDetectorButton);

        Button readCrosshairButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.read_crosshair_button",
                (button) -> this.minecraft.setScreen(new ReadCrosshairConfigMenu("read_crosshair_config_menu", this)));
        this.addRenderableWidget(readCrosshairButton);

        Button narratorMenuButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.access_menu_button",
                (button) -> this.minecraft.setScreen(new NarratorMenuConfigMenu("access_menu_config_menu", this)));
        this.addRenderableWidget(narratorMenuButton);

        Button otherButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.other_button",
                (button) -> this.minecraft.setScreen(new OtherConfigMenu("other_config_menu", this)));
        this.addRenderableWidget(otherButton);

        Button resetConfigButton = this.buildButtonWidget("minecraft_access.gui.config_menu.button.reset_config_button",
                (button) -> {
                    Config.getInstance().resetConfigToDefault();
                    MainClass.speakWithNarrator(I18n.get("minecraft_access.gui.config_menu.info.reset_config_text"), true);
                    this.minecraft.setScreen(null);
                },
                true);
        this.addRenderableWidget(resetConfigButton);
    }
}
