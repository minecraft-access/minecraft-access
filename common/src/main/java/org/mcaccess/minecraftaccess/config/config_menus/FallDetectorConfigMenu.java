package org.mcaccess.minecraftaccess.config.config_menus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.config.config_maps.FallDetectorConfigMap;
import org.mcaccess.minecraftaccess.utils.BaseScreen;

@SuppressWarnings("DataFlowIssue")
public class FallDetectorConfigMenu extends BaseScreen {
    public FallDetectorConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        FallDetectorConfigMap initMap = FallDetectorConfigMap.getInstance();

        Button featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    FallDetectorConfigMap map = FallDetectorConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addRenderableWidget(featureToggleButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> FallDetectorConfigMap.getInstance().getRange(),
                (v) -> FallDetectorConfigMap.getInstance().setRange(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        Button rangeButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.range",
                        initMap.getRange()),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c1, this)));
        this.addRenderableWidget(rangeButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> FallDetectorConfigMap.getInstance().getDepth(),
                (v) -> FallDetectorConfigMap.getInstance().setDepth(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        Button depthButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.get("minecraft_access.gui.fall_detector_config_menu.button.depth_threshold_button"),
                        initMap.getDepth()
                ),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c2, this)));
        this.addRenderableWidget(depthButton);

        Button playAlternateSoundButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isPlayAlternateSound() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.fall_detector_config_menu.button.play_alternate_sound_button")
                ),
                (button) -> {
                    FallDetectorConfigMap map = FallDetectorConfigMap.getInstance();
                    map.setPlayAlternateSound(!map.isPlayAlternateSound());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isPlayAlternateSound() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.fall_detector_config_menu.button.play_alternate_sound_button")
                    )));
                });
        playAlternateSoundButton.active = false;
        this.addRenderableWidget(playAlternateSoundButton);

        ValueEntryMenu.ValueConfig c3 = new ValueEntryMenu.ValueConfig(() -> FallDetectorConfigMap.getInstance().getVolume(),
                (v) -> FallDetectorConfigMap.getInstance().setVolume(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        Button volumeButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.volume", initMap.getVolume()),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c3, this)));
        this.addRenderableWidget(volumeButton);

        ValueEntryMenu.ValueConfig c4 = new ValueEntryMenu.ValueConfig(() -> FallDetectorConfigMap.getInstance().getDelay(),
                (v) -> FallDetectorConfigMap.getInstance().setDelay(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        Button delayButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.delay", initMap.getDelay()),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c4, this)));
        this.addRenderableWidget(delayButton);
    }
}
