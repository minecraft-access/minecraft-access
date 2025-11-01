package org.mcaccess.minecraftaccess.features.access_menu;

import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.screen_reader.ScreenReaderController;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;

/**
 * GUI screen for the access menu.
 */
public class AccessMenuGUI extends Screen {
    private GridLayout.RowHelper rowHelper;

    public AccessMenuGUI() {
        super(Component.translatable("minecraft_access.gui.screen.access_menu"));
    }

    @Override
    public void init() {
        assert minecraft != null;
        HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
        layout.addTitleHeader(title, minecraft.font);

        GridLayout grid = layout.addToContents(new GridLayout().spacing(10));
        rowHelper = grid.createRowHelper(2);

        // The order of buttons initialisation should be the same as AccessMenu.FUNCTIONS
        button("1", "minecraft_access.access_menu.gui.button.block_and_fluid_target_info",
                (button) -> AccessMenu.getBlockAndFluidTargetInformation());

        button("2", "minecraft_access.access_menu.gui.button.block_and_fluid_target_position",
                (button) -> AccessMenu.getBlockAndFluidTargetPosition());

        button("3", "minecraft_access.access_menu.gui.button.light_level",
                (button) -> AccessMenu.getLightLevel());

        button("4", "minecraft_access.access_menu.gui.button.find_water",
                (button) -> MainClass.fluidDetector.findClosestWaterSource(true));

        button("5", "minecraft_access.access_menu.gui.button.find_lava",
                (button) -> MainClass.fluidDetector.findClosestLavaSource(true));

        button("6", "minecraft_access.access_menu.gui.button.biome",
                (button) -> AccessMenu.getBiome());

        button("7", "minecraft_access.access_menu.gui.button.time_of_day",
                (button) -> AccessMenu.getTimeOfDay());

        button("8", "minecraft_access.access_menu.gui.button.xp",
                (button) -> AccessMenu.getXP());

        button("9", "minecraft_access.access_menu.gui.button.refresh_screen_reader",
                (button) -> ScreenReaderController.refreshScreenReader(true));

        button("0", "minecraft_access.access_menu.gui.button.open_config_menu",
                (button) -> Minecraft.getInstance().setScreen(AutoConfig.getConfigScreen(Config.class, this).get()));

        button("", "minecraft_access.access_menu.gui.button.weather",
                (button) -> AccessMenu.getWeatherStatus());

        layout.visitWidgets(this::addRenderableWidget);
        layout.arrangeElements();
    }

    private void button(String shortcut, String translationKey, Button.OnPress pressAction) {
        Component label = Component.literal(shortcut)
                .append(". ")
                .append(I18n.get(translationKey));
        rowHelper.addChild(Button.builder(label, pressAction)
                .width(Math.min(250, width / 2 - 15))
                .build());
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (KeyBindingsHandler.Keys.ACCESS_MENU_KEY.mapping.matches(event)) {
            Minecraft.getInstance().player.clientSideCloseContainer();
            return true;
        }
        return super.keyPressed(event);
    }
}
