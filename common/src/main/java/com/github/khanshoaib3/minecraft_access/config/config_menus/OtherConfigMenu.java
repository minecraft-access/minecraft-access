package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

@SuppressWarnings("DataFlowIssue")
public class OtherConfigMenu extends BaseScreen {
    public OtherConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        OtherConfigsMap initMap = OtherConfigsMap.getInstance();

        ButtonWidget biomeIndicatorButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isBiomeIndicatorEnabled() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.biome_indicator_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setBiomeIndicatorEnabled(!map.isBiomeIndicatorEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isBiomeIndicatorEnabled() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.biome_indicator_button")
                    )));
                });
        this.addDrawableChild(biomeIndicatorButton);

        ButtonWidget xpIndicatorButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isXpIndicatorEnabled() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.xp_indicator_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setXpIndicatorEnabled(!map.isXpIndicatorEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isXpIndicatorEnabled() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.xp_indicator_button")
                    )));
                });
        this.addDrawableChild(xpIndicatorButton);

        ButtonWidget speakFacingDirectionButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isFacingDirectionEnabled() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.speak_facing_direction_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setFacingDirectionEnabled(!map.isFacingDirectionEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isFacingDirectionEnabled() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.speak_facing_direction_button")
                    )));
                },
                true);
        this.addDrawableChild(speakFacingDirectionButton);

        ButtonWidget healthNHungerButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isHealthNHungerEnabled() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.health_n_hunger_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setHealthNHungerEnabled(!map.isHealthNHungerEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isHealthNHungerEnabled() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.health_n_hunger_button")
                    )));
                },
                true);
        this.addDrawableChild(healthNHungerButton);

        ButtonWidget positionNarratorButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isPositionNarratorEnabled() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.position_narrator_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setPositionNarratorEnabled(!map.isPositionNarratorEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isPositionNarratorEnabled() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.position_narrator_button")
                    )));
                });
        this.addDrawableChild(positionNarratorButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> OtherConfigsMap.getInstance().getCommandSuggestionNarratorFormat(),
                (v) -> OtherConfigsMap.getInstance().setCommandSuggestionNarratorFormat(v),
                ValueEntryMenu.ValueType.STRING);
        ButtonWidget suggestionFormatButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.button_with_string_value",
                        I18n.translate("minecraft_access.gui.other_config_menu.button.command_suggestion_narrator_format_button"),
                        initMap.getCommandSuggestionNarratorFormat()
                ),
                (button) -> this.client.setScreen(new ValueEntryMenu(c2, this)));
        this.addDrawableChild(suggestionFormatButton);

        ButtonWidget use12HourFormatButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isUse12HourTimeFormat() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.use_12_hour_format_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setUse12HourTimeFormat(!map.isUse12HourTimeFormat());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isUse12HourTimeFormat() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.use_12_hour_format_button")
                    )));
                });
        this.addDrawableChild(use12HourFormatButton);

        ButtonWidget speakActionBarButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isActionBarEnabled() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.speak_action_bar_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setActionBarEnabled(!map.isActionBarEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isActionBarEnabled() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.speak_action_bar_button")
                    )));
                },
                true);
        this.addDrawableChild(speakActionBarButton);

        ButtonWidget onlySpeakChangedContentOfActionBarButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isOnlySpeakActionBarUpdates() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.only_speak_action_bar_updates_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setOnlySpeakActionBarUpdates(!map.isOnlySpeakActionBarUpdates());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isOnlySpeakActionBarUpdates() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.only_speak_action_bar_updates_button")
                    )));
                },
                true);
        this.addDrawableChild(onlySpeakChangedContentOfActionBarButton);

        ButtonWidget speakFishingHarvestButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isFishingHarvestEnabled() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.speak_fishing_harvest_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setFishingHarvestEnabled(!map.isFishingHarvestEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isFishingHarvestEnabled() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.speak_fishing_harvest_button")
                    )));
                },
                true);
        this.addDrawableChild(speakFishingHarvestButton);

        ButtonWidget reportHeldItemsCountWhenChangedButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isReportHeldItemsCountWhenChanged() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.report_held_items_count_when_changed_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setReportHeldItemsCountWhenChanged(!map.isReportHeldItemsCountWhenChanged());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isReportHeldItemsCountWhenChanged() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.report_held_items_count_when_changed_button")
                    )));
                },
                true);
        this.addDrawableChild(reportHeldItemsCountWhenChangedButton);

        ButtonWidget menuFixButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isMenuFixEnabled() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.menu_fix_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setMenuFixEnabled(!map.isMenuFixEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isMenuFixEnabled() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.menu_fix_button")
                    )));
                });
        this.addDrawableChild(menuFixButton);

        ButtonWidget debugModeButton = this.buildButtonWidget(
                I18n.translate("minecraft_access.gui.common.button.toggle_button." + (initMap.isDebugMode() ? "enabled" : "disabled"),
                        I18n.translate("minecraft_access.gui.other_config_menu.button.debug_mode_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setDebugMode(!map.isDebugMode());
                    Config.getInstance().writeJSON();
                    button.setMessage(Text.of(I18n.translate("minecraft_access.gui.common.button.toggle_button." + (map.isDebugMode() ? "enabled" : "disabled"),
                            I18n.translate("minecraft_access.gui.other_config_menu.button.debug_mode_button")
                    )));
                });
        this.addDrawableChild(debugModeButton);
    }
}
