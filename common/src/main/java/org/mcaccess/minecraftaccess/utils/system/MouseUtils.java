package org.mcaccess.minecraftaccess.utils.system;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.MouseButtonInfo;
import org.lwjgl.glfw.GLFW;

import org.mcaccess.minecraftaccess.mixin.MouseHandlerAccessor;

/**
 * Contains functions to simulate mouse events.
 */
@Slf4j
public final class MouseUtils {
    /**
     * The {@link Minecraft} is singleton and {@link Minecraft#window} only be initialized once in constructor,
     * so this is safe to cache it.
     */
    private static long windowPointer = 0;

    private MouseUtils() {
    }

    public static void moveAndLeftClick(int x, int y) {
        move(x, y);
        // fix the https://github.com/minecraft-access/minecraft-access/issues/65
        if (Util.getPlatform() == Util.OS.WINDOWS) {
            try {
                // with a little bit of waiting, everything is ok now.
                // I've tried to set the value to 10, and it doesn't always work, 20 is fine.
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (InterruptedException ignored) {
            }
        }
        Key.LEFT.click();
    }

    public static void move(int x, int y) {
        log.debug("Move mouse to x:{} y:{}", x, y);
        GLFW.glfwSetCursorPos(getWindowPointer(), x, y);
        getMouseHandler().invokeOnMove(getWindowPointer(), x, y);
    }

    public static void move(Coordinates coordinates) {
        move(coordinates.x(), coordinates.y());
    }

    public static void moveAfterDelay(int x, int y, int delayInMillSecs) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                move(x, y);
            }
        }, delayInMillSecs);
    }

    public static Coordinates calcRealPositionOfWidget(int x, int y) {
        double scale = Minecraft.getInstance().getWindow().getGuiScale();
        int realX = (int) (x * scale);
        int realY = (int) (y * scale);
        return new Coordinates(realX, realY);
    }

    private static MouseHandlerAccessor getMouseHandler() {
        return (MouseHandlerAccessor) Minecraft.getInstance().mouseHandler;
    }

    private static long getWindowPointer() {
        if (windowPointer == 0) {
            windowPointer = Minecraft.getInstance().getWindow().handle();
        }
        return windowPointer;
    }

    public record Coordinates(int x, int y) {
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
            // basing on MouseHandler.onPress():
            // if Minecraft.ON_OSX && button == 0
            // run macOS related logic
            int modifiers = Minecraft.getInstance().hasShiftDown() ? 1 : 0;
            MouseButtonInfo mouseButtonInfo = new MouseButtonInfo(key.id, modifiers);
            getMouseHandler().invokeOnButton(getWindowPointer(), mouseButtonInfo, action);
        }
    }

    public enum Wheel {
        UP,
        DOWN;

        public void scroll() {
            log.debug("Mouse {} scrolled", this);
            // captured real mouse scrolling always results in x=0, y=1/-1
            int offset = this == UP ? 1 : -1;
            getMouseHandler().invokeOnScroll(getWindowPointer(), 0, offset);
        }
    }
}
