package org.mcaccess.minecraftaccess.config.config_menus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.config.config_maps.AccessMenuConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.FluidDetectorConfigMap;
import org.mcaccess.minecraftaccess.utils.BaseScreen;

@SuppressWarnings("DataFlowIssue")
public class NarratorMenuConfigMenu extends BaseScreen {
    public NarratorMenuConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        AccessMenuConfigMap initMap = AccessMenuConfigMap.getInstance();

        Button featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    AccessMenuConfigMap map = AccessMenuConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addRenderableWidget(featureToggleButton);

        Button fluidDetectorButton = this.buildButtonWidget("minecraft_access.gui.access_menu_config_menu.button.fluid_detector_button",
                (button) -> this.minecraft.setScreen(new FluidDetectorConfigMenu("fluid_detector_config_menu", this)));
        this.addRenderableWidget(fluidDetectorButton);
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
        Button volumeButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.volume", initMap.getVolume()),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c1, this)));
        this.addRenderableWidget(volumeButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> FluidDetectorConfigMap.getInstance().getRange(),
                (v) -> FluidDetectorConfigMap.getInstance().setRange(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        Button rangeButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.range", initMap.getRange()),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c2, this)));
        this.addRenderableWidget(rangeButton);
    }
}