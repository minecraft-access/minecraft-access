package com.github.khanshoaib3.minecraft_access.features.access_menu;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.ConfigMenu;
import com.github.khanshoaib3.minecraft_access.screen_reader.ScreenReaderController;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.Text;

/**
 * GUI screen for the access menu.
 */
public class AccessMenuGUI extends BaseScreen {
    public AccessMenuGUI(String title) {
        super(title);
    }

    /**
     * The order of buttons initialization should be the same as {@link AccessMenu#MENU_FUNCTIONS}
     */
    @Override
    protected void init() {
        super.init();

        ButtonWidget blockAndFluidTargetInformationButton = this.buildButtonWidget("1", "minecraft_access.access_menu.gui.button.block_and_fluid_target_info",
                (button) -> AccessMenu.getBlockAndFluidTargetInformation());
        this.addDrawableChild(blockAndFluidTargetInformationButton);

        ButtonWidget blockAndFluidTargetPositionButton = this.buildButtonWidget("2", "minecraft_access.access_menu.gui.button.block_and_fluid_target_position",
                (button) -> AccessMenu.getBlockAndFluidTargetPosition());
        this.addDrawableChild(blockAndFluidTargetPositionButton);

        ButtonWidget lightLevelButton = this.buildButtonWidget("3", "minecraft_access.access_menu.gui.button.light_level",
                (button) -> AccessMenu.getLightLevel());
        this.addDrawableChild(lightLevelButton);

        ButtonWidget findWaterButton = this.buildButtonWidget("4", "minecraft_access.access_menu.gui.button.find_water",
                (button) -> MainClass.fluidDetector.findClosestWaterSource(true));
        this.addDrawableChild(findWaterButton);

        ButtonWidget findLavaButton = this.buildButtonWidget("5", "minecraft_access.access_menu.gui.button.find_lava",
                (button) -> MainClass.fluidDetector.findClosestLavaSource(true));
        this.addDrawableChild(findLavaButton);

        ButtonWidget biomeButton = this.buildButtonWidget("6", "minecraft_access.access_menu.gui.button.biome",
                (button) -> AccessMenu.getBiome());
        this.addDrawableChild(biomeButton);

        ButtonWidget timeOfDayButton = this.buildButtonWidget("7", "minecraft_access.access_menu.gui.button.time_of_day",
                (button) -> AccessMenu.getTimeOfDay());
        this.addDrawableChild(timeOfDayButton);

            ButtonWidget xpButton = this.buildButtonWidget("8", "minecraft_access.access_menu.gui.button.xp",
                (button) -> AccessMenu.getXP());
        this.addDrawableChild(xpButton);

        ButtonWidget refreshScreenReaderButton = this.buildButtonWidget("9", "minecraft_access.access_menu.gui.button.refresh_screen_reader",
                (button) -> ScreenReaderController.refreshScreenReader(true));
        this.addDrawableChild(refreshScreenReaderButton);

        ButtonWidget openConfigMenuButton = this.buildButtonWidget("0", "minecraft_access.access_menu.gui.button.open_config_menu",
                (button) -> MinecraftClient.getInstance().setScreen(new ConfigMenu("config_menu")));
        this.addDrawableChild(openConfigMenuButton);
    }

    private ButtonWidget buildButtonWidget(String shortcut, String translationKey, ButtonWidget.PressAction pressAction) {
        Text label = Text.literal(shortcut)
                .append(". ")
                .append(I18n.translate(translationKey));
        return super.buildButtonWidget(label.getString(), pressAction);
    }
}
