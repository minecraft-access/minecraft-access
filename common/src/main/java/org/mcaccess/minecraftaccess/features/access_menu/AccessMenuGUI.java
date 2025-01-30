package org.mcaccess.minecraftaccess.features.access_menu;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.config.ConfigMenu;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderController;
import org.mcaccess.minecraftaccess.utils.BaseScreen;

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
    public void init() {
        super.init();

        Button blockAndFluidTargetInformationButton = this.buildButtonWidget("1", "minecraft_access.access_menu.gui.button.block_and_fluid_target_info",
                (button) -> AccessMenu.getBlockAndFluidTargetInformation());
        this.addRenderableWidget(blockAndFluidTargetInformationButton);

        Button blockAndFluidTargetPositionButton = this.buildButtonWidget("2", "minecraft_access.access_menu.gui.button.block_and_fluid_target_position",
                (button) -> AccessMenu.getBlockAndFluidTargetPosition());
        this.addRenderableWidget(blockAndFluidTargetPositionButton);

        Button lightLevelButton = this.buildButtonWidget("3", "minecraft_access.access_menu.gui.button.light_level",
                (button) -> AccessMenu.getLightLevel());
        this.addRenderableWidget(lightLevelButton);

        Button findWaterButton = this.buildButtonWidget("4", "minecraft_access.access_menu.gui.button.find_water",
                (button) -> MainClass.fluidDetector.findClosestWaterSource(true));
        this.addRenderableWidget(findWaterButton);

        Button findLavaButton = this.buildButtonWidget("5", "minecraft_access.access_menu.gui.button.find_lava",
                (button) -> MainClass.fluidDetector.findClosestLavaSource(true));
        this.addRenderableWidget(findLavaButton);

        Button biomeButton = this.buildButtonWidget("6", "minecraft_access.access_menu.gui.button.biome",
                (button) -> AccessMenu.getBiome());
        this.addRenderableWidget(biomeButton);

        Button timeOfDayButton = this.buildButtonWidget("7", "minecraft_access.access_menu.gui.button.time_of_day",
                (button) -> AccessMenu.getTimeOfDay());
        this.addRenderableWidget(timeOfDayButton);

        Button xpButton = this.buildButtonWidget("8", "minecraft_access.access_menu.gui.button.xp",
                (button) -> AccessMenu.getXP());
        this.addRenderableWidget(xpButton);

        Button refreshScreenReaderButton = this.buildButtonWidget("9", "minecraft_access.access_menu.gui.button.refresh_screen_reader",
                (button) -> ScreenReaderController.refreshScreenReader(true));
        this.addRenderableWidget(refreshScreenReaderButton);

        Button openConfigMenuButton = this.buildButtonWidget("0", "minecraft_access.access_menu.gui.button.open_config_menu",
                (button) -> Minecraft.getInstance().setScreen(new ConfigMenu("config_menu")));
        this.addRenderableWidget(openConfigMenuButton);
    }

    private Button buildButtonWidget(String shortcut, String translationKey, Button.OnPress pressAction) {
        Component label = Component.literal(shortcut)
                .append(". ")
                .append(I18n.get(translationKey));
        return super.buildButtonWidget(label.getString(), pressAction);
    }
}
