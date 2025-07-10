package org.mcaccess.minecraftaccess.features;

import com.mojang.blaze3d.platform.InputConstants;
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
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

import java.util.List;

/**
 * Moves the mouse to the top left of the screen and then performs left click.
 * This fixes the bug in which the mouse cursor interrupts when navigating through the screen elements
 * which results in infinite narrating of `Screen element x out of x` by the narrator
 */
@Slf4j
public class MenuFix {
    private static Class<? extends Screen> prevScreenClass = TitleScreen.class;
    private static final List<Class<? extends Screen>> menusNeedFix = List.of(
        TitleScreen.class,
        OptionsScreen.class,
        ControlsScreen.class,
        OnlineOptionsScreen.class,
        SkinCustomizationScreen.class,
        SoundOptionsScreen.class,
        VideoSettingsScreen.class,
        LanguageSelectScreen.class,
        ChatOptionsScreen.class,
        PackSelectionScreen.class,
        AccessibilityOptionsScreen.class,
        MouseSettingsScreen.class,
        KeyBindsScreen.class,
        SelectWorldScreen.class,
        CreateWorldScreen.class,
        EditWorldScreen.class,
        JoinMultiplayerScreen.class,
        DirectJoinServerScreen.class,
        EditServerScreen.class
    );

    public static void tick(Minecraft minecraftClient) {
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
            boolean isRPressed = KeyUtils.isAnyPressed(InputConstants.KEY_R);
            if (isLeftAltPressed && isRPressed)
                moveMouseCursor();
        }
    }

    /**
     * Moves the mouse cursor to x=10 y=10 relative to the Minecraft window location
     */
    private static void moveMouseCursor() {
        MouseUtils.moveAndLeftClick(10, 10);
    }
}
