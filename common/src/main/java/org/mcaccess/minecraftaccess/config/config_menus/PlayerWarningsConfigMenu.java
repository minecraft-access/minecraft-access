package org.mcaccess.minecraftaccess.config.config_menus;

import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.config.config_maps.PlayerWarningConfigMap;
import org.mcaccess.minecraftaccess.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@SuppressWarnings("DataFlowIssue")
public class PlayerWarningsConfigMenu extends BaseScreen {
    public PlayerWarningsConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        PlayerWarningConfigMap initMap = PlayerWarningConfigMap.getInstance();

        ButtonWidget featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    PlayerWarningConfigMap map = PlayerWarningConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addDrawableChild(featureToggleButton);

        ButtonWidget playSoundButton = this.buildButtonWidget("minecraft_access.gui.common.button.play_sound_toggle_button." + (initMap.isPlaySound() ? "enabled" : "disabled"),
                (button) -> {
                    PlayerWarningConfigMap map = PlayerWarningConfigMap.getInstance();
                    map.setPlaySound(!map.isPlaySound());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.play_sound_toggle_button." + (map.isPlaySound() ? "enabled" : "disabled"))));
                });
        this.addDrawableChild(playSoundButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> PlayerWarningConfigMap.getInstance().getFirstHealthThreshold(),
                (v) -> PlayerWarningConfigMap.getInstance().setFirstHealthThreshold(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget firstHealthThresholdButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.translate("minecraft_access.gui.player_warnings_config_menu.button.first_health_threshold_button"),
                        initMap.getFirstHealthThreshold()
                ),
                (button) -> this.client.setScreen(new ValueEntryMenu(c1, this)));
        this.addDrawableChild(firstHealthThresholdButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> PlayerWarningConfigMap.getInstance().getSecondHealthThreshold(),
                (v) -> PlayerWarningConfigMap.getInstance().setSecondHealthThreshold(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget secondHealthThresholdButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.translate("minecraft_access.gui.player_warnings_config_menu.button.second_health_threshold_button"),
                        initMap.getSecondHealthThreshold()
                ),
                (button) -> this.client.setScreen(new ValueEntryMenu(c2, this)));
        this.addDrawableChild(secondHealthThresholdButton);

        ValueEntryMenu.ValueConfig c3 = new ValueEntryMenu.ValueConfig(() -> PlayerWarningConfigMap.getInstance().getHungerThreshold(),
                (v) -> PlayerWarningConfigMap.getInstance().setHungerThreshold(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget hungerThresholdButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.translate("minecraft_access.gui.player_warnings_config_menu.button.hunger_threshold_button"),
                        initMap.getHungerThreshold()
                ),
                (button) -> this.client.setScreen(new ValueEntryMenu(c3, this)));
        this.addDrawableChild(hungerThresholdButton);

        ValueEntryMenu.ValueConfig c4 = new ValueEntryMenu.ValueConfig(() -> PlayerWarningConfigMap.getInstance().getAirThreshold(),
                (v) -> PlayerWarningConfigMap.getInstance().setAirThreshold(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget airThresholdButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.translate("minecraft_access.gui.player_warnings_config_menu.button.air_threshold_button"),
                        initMap.getAirThreshold()
                ),
                (button) -> this.client.setScreen(new ValueEntryMenu(c4, this)));
        this.addDrawableChild(airThresholdButton);

        ValueEntryMenu.ValueConfig c5 = new ValueEntryMenu.ValueConfig(() -> PlayerWarningConfigMap.getInstance().getFrostThreshold(),
                (v) -> PlayerWarningConfigMap.getInstance().setFrostThreshold(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget frostThresholdButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_float_value", I18n.translate("minecraft_access.gui.player_warnings_config_menu.button.frost_threshold_button"), initMap.getFrostThreshold()
                ),
                (button) -> this.client.setScreen(new ValueEntryMenu(c5, this)));
        this.addDrawableChild(frostThresholdButton);
    }
}
