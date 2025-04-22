package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import net.minecraft.client.gui.screens.EditServerScreen;
import net.minecraft.client.gui.screens.Screen;
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
import org.mcaccess.minecraftaccess.Config;
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
    private static Class<?> prevScreenClass = TitleScreen.class;
    private static final List<Class<?>> menusNeedFix = new ArrayList<>() {{
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

    public static void update(Minecraft minecraftClient) {
        if (!Config.getInstance().menuFixEnabled || minecraftClient.screen == null) {
            return;
        }

        Class<? extends Screen> currentScreen = minecraftClient.screen.getClass();
        if (menusNeedFix.contains(currentScreen)) {
            if (prevScreenClass != currentScreen) {
                log.debug("Performing menu fix on {}", minecraftClient.screen.getTitle().getString());
                moveMouseCursor();
                prevScreenClass = currentScreen;
            }

            boolean isLeftAltPressed = KeyUtils.isLeftAltPressed();
            boolean isRPressed = KeyUtils.isAnyPressed(GLFW.GLFW_KEY_R);
            if (isLeftAltPressed && isRPressed) {
                moveMouseCursor();
            }
        }
    }

    /**
     * Moves the mouse cursor to x=10 y=10 relative to the Minecraft window location
     */
    private static void moveMouseCursor() {
        MouseUtils.moveAndLeftClick(10, 10);
    }
}
