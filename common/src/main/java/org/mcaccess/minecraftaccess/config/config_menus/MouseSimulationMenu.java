package org.mcaccess.minecraftaccess.config.config_menus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.config.config_maps.MouseSimulationConfigMap;
import org.mcaccess.minecraftaccess.utils.BaseScreen;

import java.util.function.Function;

@SuppressWarnings("DataFlowIssue")
public class MouseSimulationMenu extends BaseScreen {
    public MouseSimulationMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        MouseSimulationConfigMap initMap = MouseSimulationConfigMap.getInstance();

        Button featureToggleButton = this.buildButtonWidget(featureToggleButtonMessage(initMap.isEnabled()),
                (button) -> {
                    MouseSimulationConfigMap map = MouseSimulationConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    button.setMessage(Component.nullToEmpty(featureToggleButtonMessage(map.isEnabled())));
                    Config.getInstance().writeJSON();
                });
        this.addRenderableWidget(featureToggleButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> MouseSimulationConfigMap.getInstance().getScrollDelayInMilliseconds(),
                (v) -> MouseSimulationConfigMap.getInstance().setScrollDelayInMilliseconds(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        Button delayButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.delay",
                        initMap.getScrollDelayInMilliseconds()),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c1, this)));
        this.addRenderableWidget(delayButton);

        Function<Boolean, String> useMacMouseFix = featureToggleButtonMessageWith("minecraft_access.gui.mouse_simulation.button.toggle_mac_mouse_fix_button");
        Button macMouseFixToggleButton = this.buildButtonWidget(useMacMouseFix.apply(initMap.getMacMouseFix()),
                (button) -> {
                    MouseSimulationConfigMap map = MouseSimulationConfigMap.getInstance();
                    map.setMacMouseFix(!map.getMacMouseFix());
                    button.setMessage(Component.nullToEmpty(I18n.get(useMacMouseFix.apply(map.getMacMouseFix()))));
                    Config.getInstance().writeJSON();
                });
        this.addRenderableWidget(macMouseFixToggleButton);
    }
}
