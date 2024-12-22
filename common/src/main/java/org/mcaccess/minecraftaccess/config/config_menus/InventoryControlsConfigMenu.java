package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.config_maps.InventoryControlsConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@SuppressWarnings("DataFlowIssue")
public class InventoryControlsConfigMenu extends BaseScreen {
    public InventoryControlsConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        InventoryControlsConfigMap initMap = InventoryControlsConfigMap.getInstance();

        ButtonWidget featureToggleButton = this.buildButtonWidget("minecraft_access.gui.common.button.feature_toggle_button." + (initMap.isEnabled() ? "enabled" : "disabled"),
                (button) -> {
                    InventoryControlsConfigMap map = InventoryControlsConfigMap.getInstance();
                    map.setEnabled(!map.isEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.feature_toggle_button." + (map.isEnabled() ? "enabled" : "disabled"))));
                });
        this.addDrawableChild(featureToggleButton);

        ButtonWidget autoOpenRecipeBookButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isAutoOpenRecipeBook() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.inventory_controls_config_menu.button.auto_open_recipe_book")
                ),
                (button) -> {
                    InventoryControlsConfigMap map = InventoryControlsConfigMap.getInstance();
                    map.setAutoOpenRecipeBook(!map.isAutoOpenRecipeBook());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isAutoOpenRecipeBook() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.inventory_controls_config_menu.button.auto_open_recipe_book")
                    )));
                });
        this.addDrawableChild(autoOpenRecipeBookButton);

        ValueEntryMenu.ValueConfig c1 = new ValueEntryMenu.ValueConfig(() -> InventoryControlsConfigMap.getInstance().getRowAndColumnFormat(),
                (v) -> InventoryControlsConfigMap.getInstance().setRowAndColumnFormat(v),
                ValueEntryMenu.ValueType.STRING);
        ButtonWidget rowNColumnButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_string_value",
                        I18n.translate("minecraft_access.gui.inventory_controls_config_menu.button.row_and_column_format"),
                        initMap.getRowAndColumnFormat()
                ),
                (button) -> this.client.setScreen(new ValueEntryMenu(c1, this)));
        rowNColumnButton.active = false;
        this.addDrawableChild(rowNColumnButton);

        ButtonWidget repeatSpeakingFocusedSlotBtn = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isSpeakFocusedSlotChanges() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.inventory_controls_config_menu.button.speak_focused_slot_changes")
                ),
                (button) -> {
                    InventoryControlsConfigMap map = InventoryControlsConfigMap.getInstance();
                    map.setSpeakFocusedSlotChanges(!map.isSpeakFocusedSlotChanges());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isSpeakFocusedSlotChanges() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.inventory_controls_config_menu.button.speak_focused_slot_changes")
                    )));
                });
        this.addDrawableChild(repeatSpeakingFocusedSlotBtn);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> InventoryControlsConfigMap.getInstance().getDelayInMilliseconds(),
                (v) -> InventoryControlsConfigMap.getInstance().setDelayInMilliseconds(Integer.parseInt(v)),
                ValueEntryMenu.ValueType.INT);
        ButtonWidget delayButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.delay",
                        initMap.getDelayInMilliseconds()),
                (button) -> this.client.setScreen(new ValueEntryMenu(c2, this)));
        this.addDrawableChild(delayButton);
    }
}
