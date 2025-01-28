package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.EditServerScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.options.*;
import net.minecraft.client.gui.screens.options.controls.ControlsScreen;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Moves the mouse to the top left of the screen and then performs left click.
 * This fixes the bug in which the mouse cursor interrupts when navigating through the screen elements
 * which results in infinite speaking of `Screen element x out of x` by the narrator
 */
@Slf4j
public class MenuFix {
    /**
     * Prevents executing this fix for the title screen the first time
     */
    @SuppressWarnings("rawtypes")
    static Class prevScreenClass = TitleScreen.class;
    /**
     * The list of screens for which this fix will execute when opened
     */
    @SuppressWarnings("rawtypes")
    private static final List<Class> menuList = new ArrayList<>() {{
        add(TitleScreen.class);
        add(OptionsScreen.class);
        add(ControlsScreen.class);
        add(OnlineOptionsScreen.class);
        add(SkinCustomizationScreen.class);
        add(SoundOptionsScreen.class);
        add(VideoSettingsScreen.class);
        add(LanguageSelectScreen.class);
        add(ChatOptionsScreen.class);
        add(PackSelectionScreen.class);
        add(AccessibilityOptionsScreen.class);
        add(MouseSettingsScreen.class);
        add(KeyBindsScreen.class);
        add(SelectWorldScreen.class);
        add(CreateWorldScreen.class);
        add(EditWorldScreen.class);
        add(JoinMultiplayerScreen.class);
        add(DirectJoinServerScreen.class);
        add(EditServerScreen.class);
    }};

    /**
     * This method gets called at the end of every tick.
     *
     * @param minecraftClient Current MinecraftClient instance
     */
    public static void update(Minecraft minecraftClient) {
        if (minecraftClient.screen == null)
            return;

        try {
            if (menuList.contains(minecraftClient.screen.getClass())) {
                if (!(prevScreenClass == minecraftClient.screen.getClass())) {
                   log.debug("%s opened, now moving the mouse cursor.".formatted(minecraftClient.screen.getTitle().getString()));
                    moveMouseCursor(minecraftClient);
                    prevScreenClass = minecraftClient.screen.getClass();
                }

                boolean isLeftAltPressed = KeyUtils.isLeftAltPressed();
                boolean isRPressed = KeyUtils.isAnyPressed(GLFW.GLFW_KEY_R);
                if (isLeftAltPressed && isRPressed)
                    moveMouseCursor(minecraftClient);
            }
        } catch (Exception e) {
            log.error("Error encountered while running the menu fix feature", e);
        }
    }

    /**
     * Moves the mouse cursor to x=1 y=1 pixel location relative the Minecraft window location
     *
     * @param minecraftClient Current MinecraftClient instance
     */
    private static void moveMouseCursor(Minecraft minecraftClient) {
        try {
            int movePosX = minecraftClient.getWindow().getX() + 10;
            int movePosY = minecraftClient.getWindow().getY() + 10;

            MouseUtils.moveAndLeftClick(movePosX, movePosY);
        } catch (Exception e) {
            log.error("Error encountered while moving the mouse for the menu fix feature", e);
        }
    }
}
