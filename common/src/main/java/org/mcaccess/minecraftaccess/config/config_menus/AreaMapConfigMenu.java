package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.config_maps.AreaMapConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

import java.util.Objects;

public class AreaMapConfigMenu extends BaseScreen {
    public AreaMapConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        AreaMapConfigMap initMap = AreaMapConfigMap.getInstance();

        ButtonWidget featureToggleButton = this.buildButtonWidget(featureToggleButtonMessage(initMap.isEnabled()),
                (button) -> {
                    AreaMapConfigMap map = AreaMapConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(Text.of(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addDrawableChild(featureToggleButton);

        ValueEntryMenu.ValueConfig delay = new ValueEntryMenu.ValueConfig(
                () -> AreaMapConfigMap.getInstance().getDelayInMilliseconds(),
                (v) -> AreaMapConfigMap.getInstance().setDelayInMilliseconds(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget delayButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.delay", initMap.getDelayInMilliseconds()),
                (button) -> Objects.requireNonNull(this.client).setScreen(new ValueEntryMenu(delay, this)));
        this.addDrawableChild(delayButton);

        ValueEntryMenu.ValueConfig verticalBound = new ValueEntryMenu.ValueConfig(
                () -> AreaMapConfigMap.getInstance().getVerticalBound(),
                (v) -> AreaMapConfigMap.getInstance().setVerticalBound(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget verticalBoundButton = this.buildButtonWidget(
                verticalBound.buildButtonText("minecraft_access.gui.area_map_config_menu.button.vertical_bound"),
                (button) -> Objects.requireNonNull(this.client).setScreen(new ValueEntryMenu(verticalBound, this)));
        this.addDrawableChild(verticalBoundButton);

        ValueEntryMenu.ValueConfig horizontalBound = new ValueEntryMenu.ValueConfig(
                () -> AreaMapConfigMap.getInstance().getHorizontalBound(),
                (v) -> AreaMapConfigMap.getInstance().setHorizontalBound(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget horizontalBoundButton = this.buildButtonWidget(
                horizontalBound.buildButtonText("minecraft_access.gui.area_map_config_menu.button.horizontal_bound"),
                (button) -> Objects.requireNonNull(this.client).setScreen(new ValueEntryMenu(horizontalBound, this)));
        this.addDrawableChild(horizontalBoundButton);
    }
}
