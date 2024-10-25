package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.config_maps.FluidDetectorConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.NarratorMenuConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@SuppressWarnings("DataFlowIssue")
public class NarratorMenuConfigMenu extends BaseScreen {
    public NarratorMenuConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        NarratorMenuConfigMap initMap = NarratorMenuConfigMap.getInstance();

        ButtonWidget featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    NarratorMenuConfigMap map = NarratorMenuConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addDrawableChild(featureToggleButton);

        ButtonWidget fluidDetectorButton = this.buildButtonWidget("minecraft_access.gui.access_menu_config_menu.button.fluid_detector_button",
                (button) -> this.client.setScreen(new FluidDetectorConfigMenu("fluid_detector_config_menu", this)));
        this.addDrawableChild(fluidDetectorButton);
    }
}

@SuppressWarnings("DataFlowIssue")
class FluidDetectorConfigMenu extends BaseScreen {
    public FluidDetectorConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        FluidDetectorConfigMap initMap = FluidDetectorConfigMap.getInstance();

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> FluidDetectorConfigMap.getInstance().getVolume(),
                (v) -> FluidDetectorConfigMap.getInstance().setVolume(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.FLOAT);
        ButtonWidget volumeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.volume", initMap.getVolume()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c1, this)));
        this.addDrawableChild(volumeButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> FluidDetectorConfigMap.getInstance().getRange(),
                (v) -> FluidDetectorConfigMap.getInstance().setRange(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget rangeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.range", initMap.getRange()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c2, this)));
        this.addDrawableChild(rangeButton);
    }
}