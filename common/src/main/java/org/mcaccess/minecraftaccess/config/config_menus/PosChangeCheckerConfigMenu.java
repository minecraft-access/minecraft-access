package org.mcaccess.minecraftaccess.config.config_menus;

import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.config.config_maps.PosChangeCheckerConfigMap;
import org.mcaccess.minecraftaccess.utils.BaseScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

public class PosChangeCheckerConfigMenu extends BaseScreen {
        public PosChangeCheckerConfigMenu(String title, BaseScreen previousScreen) {
                super(title, previousScreen);
        }

        @Override
        protected void init() {
                super.init();

                PosChangeCheckerConfigMap initMap = PosChangeCheckerConfigMap.getInstance();
                ButtonWidget xToggleButton = this.buildButtonWidget(
                                "minecraft_access.gui.pos_change_checker_config_menu.button.x_changes_button."
                                                + (initMap.isPlaySoundForXChanges() ? "enabled" : "disabled"),
                                (button) -> {
                                        PosChangeCheckerConfigMap map = PosChangeCheckerConfigMap.getInstance();
                                        map.setPlaySoundForXChanges(!map.isPlaySoundForXChanges());
                                        Config.getInstance().writeJSON();
                                        button.setMessage(Text.of(I18n.translate(
                                                        "minecraft_access.gui.pos_change_checker_config_menu.button.x_changes_button."
                                                                        + (map.isPlaySoundForXChanges() ? "enabled"
                                                                                        : "disabled"))));
                                });
                this.addDrawableChild(xToggleButton);

                ButtonWidget yToggleButton = this.buildButtonWidget(
                                "minecraft_access.gui.pos_change_checker_config_menu.button.y_changes_button."
                                                + (initMap.isPlaySoundForYChanges() ? "enabled" : "disabled"),
                                (button) -> {
                                        PosChangeCheckerConfigMap map = PosChangeCheckerConfigMap.getInstance();
                                        map.setPlaySoundForYChanges(!map.isPlaySoundForYChanges());
                                        Config.getInstance().writeJSON();
                                        button.setMessage(Text.of(I18n.translate(
                                                        "minecraft_access.gui.pos_change_checker_config_menu.button.y_changes_button."
                                                                        + (map.isPlaySoundForYChanges() ? "enabled"
                                                                                        : "disabled"))));
                                });
                this.addDrawableChild(yToggleButton);

                ButtonWidget zToggleButton = this.buildButtonWidget(
                                "minecraft_access.gui.pos_change_checker_config_menu.button.z_changes_button."
                                                + (initMap.isPlaySoundForZChanges() ? "enabled" : "disabled"),
                                (button) -> {
                                        PosChangeCheckerConfigMap map = PosChangeCheckerConfigMap.getInstance();
                                        map.setPlaySoundForZChanges(!map.isPlaySoundForZChanges());
                                        Config.getInstance().writeJSON();
                                        button.setMessage(Text.of(I18n.translate(
                                                        "minecraft_access.gui.pos_change_checker_config_menu.button.z_changes_button."
                                                                        + (map.isPlaySoundForZChanges() ? "enabled"
                                                                                        : "disabled"))));
                                });
                this.addDrawableChild(zToggleButton);
        }
}
