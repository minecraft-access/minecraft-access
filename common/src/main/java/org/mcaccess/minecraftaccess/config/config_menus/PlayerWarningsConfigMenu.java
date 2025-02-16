package org.mcaccess.minecraftaccess.config.config_menus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.config.config_maps.PlayerWarningConfigMap;
import org.mcaccess.minecraftaccess.utils.BaseScreen;

@SuppressWarnings("DataFlowIssue")
public class PlayerWarningsConfigMenu extends BaseScreen {
    public PlayerWarningsConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        PlayerWarningConfigMap initMap = PlayerWarningConfigMap.getInstance();

        Button featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    PlayerWarningConfigMap map = PlayerWarningConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addRenderableWidget(featureToggleButton);

        Button playSoundButton = this.buildButtonWidget("minecraft_access.gui.common.button.play_sound_toggle_button." + (initMap.isPlaySound() ? "enabled" : "disabled"),
                (button) -> {
                    PlayerWarningConfigMap map = PlayerWarningConfigMap.getInstance();
                    map.setPlaySound(!map.isPlaySound());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.play_sound_toggle_button." + (map.isPlaySound() ? "enabled" : "disabled"))));
                });
        this.addRenderableWidget(playSoundButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> PlayerWarningConfigMap.getInstance().getFirstHealthThreshold(),
                (v) -> PlayerWarningConfigMap.getInstance().setFirstHealthThreshold(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        Button firstHealthThresholdButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.get("minecraft_access.gui.player_warnings_config_menu.button.first_health_threshold_button"),
                        initMap.getFirstHealthThreshold()
                ),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c1, this)));
        this.addRenderableWidget(firstHealthThresholdButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> PlayerWarningConfigMap.getInstance().getSecondHealthThreshold(),
                (v) -> PlayerWarningConfigMap.getInstance().setSecondHealthThreshold(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        Button secondHealthThresholdButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.get("minecraft_access.gui.player_warnings_config_menu.button.second_health_threshold_button"),
                        initMap.getSecondHealthThreshold()
                ),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c2, this)));
        this.addRenderableWidget(secondHealthThresholdButton);

        ValueEntryMenu.ValueConfig c3 = new ValueEntryMenu.ValueConfig(() -> PlayerWarningConfigMap.getInstance().getHungerThreshold(),
                (v) -> PlayerWarningConfigMap.getInstance().setHungerThreshold(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        Button hungerThresholdButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.get("minecraft_access.gui.player_warnings_config_menu.button.hunger_threshold_button"),
                        initMap.getHungerThreshold()
                ),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c3, this)));
        this.addRenderableWidget(hungerThresholdButton);

        ValueEntryMenu.ValueConfig c4 = new ValueEntryMenu.ValueConfig(() -> PlayerWarningConfigMap.getInstance().getAirThreshold(),
                (v) -> PlayerWarningConfigMap.getInstance().setAirThreshold(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        Button airThresholdButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.button_with_float_value",
                        I18n.get("minecraft_access.gui.player_warnings_config_menu.button.air_threshold_button"),
                        initMap.getAirThreshold()
                ),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c4, this)));
        this.addRenderableWidget(airThresholdButton);

        ValueEntryMenu.ValueConfig c5 = new ValueEntryMenu.ValueConfig(() -> PlayerWarningConfigMap.getInstance().getFrostThreshold(),
                (v) -> PlayerWarningConfigMap.getInstance().setFrostThreshold(Float.parseFloat(v)),
                ValueEntryMenu.ValueType.FLOAT);
        Button frostThresholdButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.button_with_float_value", I18n.get("minecraft_access.gui.player_warnings_config_menu.button.frost_threshold_button"), initMap.getFrostThreshold()
                ),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c5, this)));
        this.addRenderableWidget(frostThresholdButton);
    }
}
