package org.mcaccess.minecraftaccess.features.access_menu;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
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
     * The order of buttons initialization should be the same as {@link AccessMenu#FUNCTIONS}
     */
    @Override
    public void init() {
        super.init();

        Button blockAndFluidTargetInformationButton = buildButtonWidget("1", "minecraft_access.access_menu.gui.button.block_and_fluid_target_info",
                (button) -> AccessMenu.getBlockAndFluidTargetInformation());
        addRenderableWidget(blockAndFluidTargetInformationButton);

        Button blockAndFluidTargetPositionButton = buildButtonWidget("2", "minecraft_access.access_menu.gui.button.block_and_fluid_target_position",
                (button) -> AccessMenu.getBlockAndFluidTargetPosition());
        addRenderableWidget(blockAndFluidTargetPositionButton);

        Button lightLevelButton = buildButtonWidget("3", "minecraft_access.access_menu.gui.button.light_level",
                (button) -> AccessMenu.getLightLevel());
        addRenderableWidget(lightLevelButton);

        Button findWaterButton = buildButtonWidget("4", "minecraft_access.access_menu.gui.button.find_water",
                (button) -> MainClass.fluidDetector.findClosestWaterSource(true));
        addRenderableWidget(findWaterButton);

        Button findLavaButton = buildButtonWidget("5", "minecraft_access.access_menu.gui.button.find_lava",
                (button) -> MainClass.fluidDetector.findClosestLavaSource(true));
        addRenderableWidget(findLavaButton);

        Button biomeButton = buildButtonWidget("6", "minecraft_access.access_menu.gui.button.biome",
                (button) -> AccessMenu.getBiome());
        addRenderableWidget(biomeButton);

        Button timeOfDayButton = buildButtonWidget("7", "minecraft_access.access_menu.gui.button.time_of_day",
                (button) -> AccessMenu.getTimeOfDay());
        addRenderableWidget(timeOfDayButton);

        Button xpButton = buildButtonWidget("8", "minecraft_access.access_menu.gui.button.xp",
                (button) -> AccessMenu.getXP());
        addRenderableWidget(xpButton);

        Button refreshScreenReaderButton = buildButtonWidget("9", "minecraft_access.access_menu.gui.button.refresh_screen_reader",
                (button) -> ScreenReaderController.refreshScreenReader(true));
        addRenderableWidget(refreshScreenReaderButton);

        Button openConfigMenuButton = buildButtonWidget("0", "minecraft_access.access_menu.gui.button.open_config_menu",
                (button) -> Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, this).get()));
        addRenderableWidget(openConfigMenuButton);
    }

    private Button buildButtonWidget(String shortcut, String translationKey, Button.OnPress pressAction) {
        Component label = Component.literal(shortcut)
                .append(". ")
                .append(I18n.get(translationKey));
        return buildButtonWidget(label.getString(), pressAction);
    }
}
