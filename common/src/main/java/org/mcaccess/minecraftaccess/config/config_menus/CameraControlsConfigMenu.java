package org.mcaccess.minecraftaccess.config.config_menus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.config.config_maps.CameraControlsConfigMap;
import org.mcaccess.minecraftaccess.utils.BaseScreen;

@SuppressWarnings("DataFlowIssue")
public class CameraControlsConfigMenu extends BaseScreen {
    public CameraControlsConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        CameraControlsConfigMap initMap = CameraControlsConfigMap.getInstance();

        Button featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    CameraControlsConfigMap map = CameraControlsConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addRenderableWidget(featureToggleButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> CameraControlsConfigMap.getInstance().getNormalRotatingAngle(),
                (v) -> CameraControlsConfigMap.getInstance().setNormalRotatingAngle(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        Button normalRotatingAngleButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.get("minecraft_access.gui.camera_controls_config_menu.button.normal_rotating_angle"),
                        initMap.getNormalRotatingAngle()),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c1, this)));
        this.addRenderableWidget(normalRotatingAngleButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> CameraControlsConfigMap.getInstance().getModifiedRotatingAngle(),
                (v) -> CameraControlsConfigMap.getInstance().setModifiedRotatingAngle(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        Button modifiedRotatingAngleButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.get("minecraft_access.gui.camera_controls_config_menu.button.modified_rotating_angle"),
                        initMap.getModifiedRotatingAngle()
                ),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c2, this)));
        this.addRenderableWidget(modifiedRotatingAngleButton);

        ValueEntryMenu.ValueConfig c3 = new ValueEntryMenu.ValueConfig(() -> CameraControlsConfigMap.getInstance().getDelayInMilliseconds(),
                (v) -> CameraControlsConfigMap.getInstance().setDelayInMilliseconds(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        Button delayButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.delay",
                        initMap.getDelayInMilliseconds()),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c3, this)));
        this.addRenderableWidget(delayButton);
    }
}
