package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.config_maps.FallDetectorConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@SuppressWarnings("DataFlowIssue")
public class FallDetectorConfigMenu extends BaseScreen {
    public FallDetectorConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        FallDetectorConfigMap initMap = FallDetectorConfigMap.getInstance();

        ButtonWidget featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    FallDetectorConfigMap map = FallDetectorConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addDrawableChild(featureToggleButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> FallDetectorConfigMap.getInstance().getRange(),
                (v) -> FallDetectorConfigMap.getInstance().setRange(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget rangeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.range",
                        initMap.getRange()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c1, this)));
        this.addDrawableChild(rangeButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> FallDetectorConfigMap.getInstance().getDepth(),
                (v) -> FallDetectorConfigMap.getInstance().setDepth(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget depthButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.translate("minecraft_access.gui.fall_detector_config_menu.button.depth_threshold_button"),
                        initMap.getDepth()
                ),
                (button) -> this.client.setScreen(new ValueEntryMenu(c2, this)));
        this.addDrawableChild(depthButton);

        ButtonWidget playAlternateSoundButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isPlayAlternateSound() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.fall_detector_config_menu.button.play_alternate_sound_button")
                ),
                (button) -> {
                    FallDetectorConfigMap map = FallDetectorConfigMap.getInstance();
                    map.setPlayAlternateSound(!map.isPlayAlternateSound());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isPlayAlternateSound() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.fall_detector_config_menu.button.play_alternate_sound_button")
                    )));
                });
        playAlternateSoundButton.active = false;
        this.addDrawableChild(playAlternateSoundButton);

        ValueEntryMenu.ValueConfig c3 = new ValueEntryMenu.ValueConfig(() -> FallDetectorConfigMap.getInstance().getVolume(),
                (v) -> FallDetectorConfigMap.getInstance().setVolume(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget volumeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.volume", initMap.getVolume()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c3, this)));
        this.addDrawableChild(volumeButton);

        ValueEntryMenu.ValueConfig c4 = new ValueEntryMenu.ValueConfig(() -> FallDetectorConfigMap.getInstance().getDelay(),
                (v) -> FallDetectorConfigMap.getInstance().setDelay(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget delayButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.delay", initMap.getDelay()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c4, this)));
        this.addDrawableChild(delayButton);
    }
}
