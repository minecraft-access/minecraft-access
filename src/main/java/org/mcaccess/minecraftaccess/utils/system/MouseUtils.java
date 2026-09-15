package org.mcaccess.minecraftaccess.utils.system;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.util.Util;
import org.lwjgl.sdl.SDLMouse;

/**
 * Contains functions to simulate mouse events.
 */
@Slf4j
public final class MouseUtils {
    private static long windowPointer = 0;

    private MouseUtils() {
    }

    public static void moveAndLeftClick(double xpos, double ypos) {
        move(xpos, ypos);
        // fix the https://github.com/minecraft-access/minecraft-access/issues/65
        if (Util.getPlatform() == Util.OS.WINDOWS) {
            try {
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException ignored) {
            }
        }
        Key.LEFT.click();
    }

    public static void move(double xpos, double ypos) {
        log.debug("Move mouse to x:{} y:{}", xpos, ypos);
        SDLMouse.SDL_WarpMouseInWindow(getWindowPointer(), (float) xpos, (float) ypos);
        Minecraft.getInstance().mouseHandler.onMove(getWindowPointer(), xpos, ypos, 0.0, 0.0);
    }

    public static void moveAfterDelay(double xpos, double ypos, int delayInMillSecs) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                move(xpos, ypos);
            }
        }, delayInMillSecs);
    }

    public static Coordinates calcRealPositionOfWidget(double x, double y) {
        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        double realX = x * scale;
        double realY = y * scale;
        return new Coordinates(realX, realY);
    }

    private static long getWindowPointer() {
        if (windowPointer == 0) {
            windowPointer = Minecraft.getInstance().getWindow().handle();
        }
        return windowPointer;
    }

    public record Coordinates(double x, double y) {
    }

    public enum Key {
        LEFT(InputConstants.MOUSE_BUTTON_LEFT),
        RIGHT(InputConstants.MOUSE_BUTTON_RIGHT),
        MIDDLE(InputConstants.MOUSE_BUTTON_MIDDLE);

        public final int id;

        Key(int buttonId) {
            id = buttonId;
        }

        public void click() {
            log.debug("Mouse {} clicked", this);
            press();
            release();
        }

        public void press() {
            operate(this, InputConstants.PRESS);
        }

        public void release() {
            operate(this, InputConstants.RELEASE);
        }

        private static void operate(Key key, int action) {
            int modifiers = Minecraft.getInstance().hasShiftDown() ? 1 : 0;
            MouseButtonInfo mouseButtonInfo = new MouseButtonInfo(key.id, modifiers);
            Minecraft.getInstance().mouseHandler.onButton(getWindowPointer(), mouseButtonInfo, action);
        }
    }

    public enum Wheel {
        UP,
        DOWN;

        public void scroll() {
            log.debug("Mouse {} scrolled", this);
            double offset = this == UP ? 1.0 : -1.0;
            Minecraft.getInstance().mouseHandler.onScroll(getWindowPointer(), 0.0, offset);
        }
    }
}
