package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;
import com.github.khanshoaib3.minecraft_access.utils.system.MouseUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.AddServerScreen;
import net.minecraft.client.gui.screen.multiplayer.DirectConnectScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.*;
import net.minecraft.client.gui.screen.pack.PackScreen;
import net.minecraft.client.gui.screen.world.CreateWorldScreen;
import net.minecraft.client.gui.screen.world.EditWorldScreen;
import net.minecraft.client.gui.screen.world.SelectWorldScreen;
import org.lwjgl.glfw.GLFW;

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
        add(ControlsOptionsScreen.class);
        add(OnlineOptionsScreen.class);
        add(SkinOptionsScreen.class);
        add(SoundOptionsScreen.class);
        add(VideoOptionsScreen.class);
        add(LanguageOptionsScreen.class);
        add(ChatOptionsScreen.class);
        add(PackScreen.class);
        add(AccessibilityOptionsScreen.class);
        add(MouseOptionsScreen.class);
        add(KeybindsScreen.class);
        add(SelectWorldScreen.class);
        add(CreateWorldScreen.class);
        add(EditWorldScreen.class);
        add(MultiplayerScreen.class);
        add(DirectConnectScreen.class);
        add(AddServerScreen.class);
    }};

    /**
     * This method gets called at the end of every tick.
     *
     * @param minecraftClient Current MinecraftClient instance
     */
    public static void update(MinecraftClient minecraftClient) {
        if (minecraftClient.currentScreen == null)
            return;

        try {
            if (menuList.contains(minecraftClient.currentScreen.getClass())) {
                if (!(prevScreenClass == minecraftClient.currentScreen.getClass())) {
                   log.debug("%s opened, now moving the mouse cursor.".formatted(minecraftClient.currentScreen.getTitle().getString()));
                    moveMouseCursor(minecraftClient);
                    prevScreenClass = minecraftClient.currentScreen.getClass();
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
    private static void moveMouseCursor(MinecraftClient minecraftClient) {
        try {
            int movePosX = minecraftClient.getWindow().getX() + 10;
            int movePosY = minecraftClient.getWindow().getY() + 10;

            MouseUtils.moveAndLeftClick(movePosX, movePosY);
        } catch (Exception e) {
            log.error("Error encountered while moving the mouse for the menu fix feature", e);
        }
    }
}
