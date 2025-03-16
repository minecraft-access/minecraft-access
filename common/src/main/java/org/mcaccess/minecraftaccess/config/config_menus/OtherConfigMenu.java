package org.mcaccess.minecraftaccess.config.config_menus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.config.config_maps.OtherConfigsMap;
import org.mcaccess.minecraftaccess.utils.BaseScreen;

@SuppressWarnings("DataFlowIssue")
public class OtherConfigMenu extends BaseScreen {
    public OtherConfigMenu(String title, BaseScreen previousScreen) {
        super(title, previousScreen);
    }

    @Override
    protected void init() {
        super.init();

        OtherConfigsMap initMap = OtherConfigsMap.getInstance();

        Button biomeIndicatorButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isBiomeIndicatorEnabled() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.biome_indicator_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setBiomeIndicatorEnabled(!map.isBiomeIndicatorEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isBiomeIndicatorEnabled() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.biome_indicator_button")
                    )));
                });
        this.addRenderableWidget(biomeIndicatorButton);

        Button xpIndicatorButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isXpIndicatorEnabled() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.xp_indicator_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setXpIndicatorEnabled(!map.isXpIndicatorEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isXpIndicatorEnabled() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.xp_indicator_button")
                    )));
                });
        this.addRenderableWidget(xpIndicatorButton);

        Button speakFacingDirectionButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isFacingDirectionEnabled() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.speak_facing_direction_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setFacingDirectionEnabled(!map.isFacingDirectionEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isFacingDirectionEnabled() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.speak_facing_direction_button")
                    )));
                },
                true);
        this.addRenderableWidget(speakFacingDirectionButton);

        Button playerStatusButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isPlayerStatusEnabled() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.player_status_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setPlayerStatusEnabled(!map.isPlayerStatusEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isPlayerStatusEnabled() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.player_status_button")
                    )));
                },
                true);
        this.addRenderableWidget(playerStatusButton);

        Button positionNarratorButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isPositionNarratorEnabled() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.position_narrator_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setPositionNarratorEnabled(!map.isPositionNarratorEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isPositionNarratorEnabled() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.position_narrator_button")
                    )));
                });
        this.addRenderableWidget(positionNarratorButton);

        ValueEntryMenu.ValueConfig c2 = new ValueEntryMenu.ValueConfig(() -> OtherConfigsMap.getInstance().getCommandSuggestionNarratorFormat(),
                (v) -> OtherConfigsMap.getInstance().setCommandSuggestionNarratorFormat(v),
                ValueEntryMenu.ValueType.STRING);
        Button suggestionFormatButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.button_with_string_value",
                        I18n.get("minecraft_access.gui.other_config_menu.button.command_suggestion_narrator_format_button"),
                        initMap.getCommandSuggestionNarratorFormat()
                ),
                (button) -> this.minecraft.setScreen(new ValueEntryMenu(c2, this)));
        this.addRenderableWidget(suggestionFormatButton);

        Button use12HourFormatButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isUse12HourTimeFormat() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.use_12_hour_format_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setUse12HourTimeFormat(!map.isUse12HourTimeFormat());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isUse12HourTimeFormat() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.use_12_hour_format_button")
                    )));
                });
        this.addRenderableWidget(use12HourFormatButton);

        Button speakActionBarButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isActionBarEnabled() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.speak_action_bar_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setActionBarEnabled(!map.isActionBarEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isActionBarEnabled() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.speak_action_bar_button")
                    )));
                },
                true);
        this.addRenderableWidget(speakActionBarButton);

        Button onlySpeakChangedContentOfActionBarButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isOnlySpeakActionBarUpdates() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.only_speak_action_bar_updates_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setOnlySpeakActionBarUpdates(!map.isOnlySpeakActionBarUpdates());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isOnlySpeakActionBarUpdates() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.only_speak_action_bar_updates_button")
                    )));
                },
                true);
        this.addRenderableWidget(onlySpeakChangedContentOfActionBarButton);

        Button speakFishingHarvestButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isFishingHarvestEnabled() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.speak_fishing_harvest_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setFishingHarvestEnabled(!map.isFishingHarvestEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isFishingHarvestEnabled() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.speak_fishing_harvest_button")
                    )));
                },
                true);
        this.addRenderableWidget(speakFishingHarvestButton);

        Button alwaysSpeakPickedUpItemsButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isAlwaysSpeakPickedUpItemsEnabled() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.always_speak_picked_up_items_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setAlwaysSpeakPickedUpItemsEnabled(!map.isAlwaysSpeakPickedUpItemsEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isAlwaysSpeakPickedUpItemsEnabled() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.always_speak_picked_up_items_button")
                    )));
                },
                true);
        this.addRenderableWidget(alwaysSpeakPickedUpItemsButton);

        Button reportHeldItemsCountWhenChangedButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isReportHeldItemsCountWhenChanged() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.report_held_items_count_when_changed_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setReportHeldItemsCountWhenChanged(!map.isReportHeldItemsCountWhenChanged());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isReportHeldItemsCountWhenChanged() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.report_held_items_count_when_changed_button")
                    )));
                },
                true);
        this.addRenderableWidget(reportHeldItemsCountWhenChangedButton);

        Button menuFixButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isMenuFixEnabled() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.menu_fix_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setMenuFixEnabled(!map.isMenuFixEnabled());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isMenuFixEnabled() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.menu_fix_button")
                    )));
                });
        this.addRenderableWidget(menuFixButton);

        Button debugModeButton = this.buildButtonWidget(
                I18n.get("minecraft_access.gui.common.button.toggle_button." + (initMap.isDebugMode() ? "enabled" : "disabled"),
                        I18n.get("minecraft_access.gui.other_config_menu.button.debug_mode_button")
                ),
                (button) -> {
                    OtherConfigsMap map = OtherConfigsMap.getInstance();
                    map.setDebugMode(!map.isDebugMode());
                    Config.getInstance().writeJSON();
                    button.setMessage(Component.nullToEmpty(I18n.get("minecraft_access.gui.common.button.toggle_button." + (map.isDebugMode() ? "enabled" : "disabled"),
                            I18n.get("minecraft_access.gui.other_config_menu.button.debug_mode_button")
                    )));
                });
        this.addRenderableWidget(debugModeButton);
    }
}
