package org.mcaccess.minecraftaccess.config.config_menus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.config.config_maps.InventoryControlsConfigMap;
import org.mcaccess.minecraftaccess.utils.BaseScreen;

@SuppressWarnings("DataFlowIssue")
public class InventoryControlsConfigMenu extends BaseScreen {
    public InventoryControlsConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        InventoryControlsConfigMap initMap = InventoryControlsConfigMap.getInstance();

        Button featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    InventoryControlsConfigMap map = InventoryControlsConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addRenderableWidget(featureToggleButton);

        Button autoOpenRecipeBookButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isAutoOpenRecipeBook() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.inventory_controls_config_menu.button.auto_open_recipe_book")
                ),
                (button) -> {
                    InventoryControlsConfigMap map = InventoryControlsConfigMap.getInstance();
                    map.setAutoOpenRecipeBook(!map.isAutoOpenRecipeBook());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isAutoOpenRecipeBook() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.inventory_controls_config_menu.button.auto_open_recipe_book")
                    )));
                });
        this.addRenderableWidget(autoOpenRecipeBookButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> InventoryControlsConfigMap.getInstance().getRowAndColumnFormat(),
                (v) -> InventoryControlsConfigMap.getInstance().setRowAndColumnFormat(v),
                ValueEntryMenu.ValueType.STRING);
        Button rowNColumnButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.button_with_string_value",
                        I18n.get("minecraft_access.gui.inventory_controls_config_menu.button.row_and_column_format"),
                        initMap.getRowAndColumnFormat()
                ),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c1, this)));
        rowNColumnButton.active = false;
        this.addRenderableWidget(rowNColumnButton);

        Button repeatSpeakingFocusedSlotBtn = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isSpeakFocusedSlotChanges() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.inventory_controls_config_menu.button.speak_focused_slot_changes")
                ),
                (button) -> {
                    InventoryControlsConfigMap map = InventoryControlsConfigMap.getInstance();
                    map.setSpeakFocusedSlotChanges(!map.isSpeakFocusedSlotChanges());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isSpeakFocusedSlotChanges() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.inventory_controls_config_menu.button.speak_focused_slot_changes")
                    )));
                });
        this.addRenderableWidget(repeatSpeakingFocusedSlotBtn);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> InventoryControlsConfigMap.getInstance().getDelayInMilliseconds(),
                (v) -> InventoryControlsConfigMap.getInstance().setDelayInMilliseconds(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        Button delayButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.delay",
                        initMap.getDelayInMilliseconds()),
                (button) -> minecraft.setScreen(new ValueEntryMenu(c2, this)));
        this.addRenderableWidget(delayButton);
    }
}
