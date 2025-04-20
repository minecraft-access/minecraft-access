package org.mcaccess.minecraftaccess.utils.system;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.mixin.MouseHandlerAccessor;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Contains functions to simulate mouse events.
 */
@Slf4j
public class MouseUtils {
    /**
     * The {@link Minecraft} is singleton and {@link Minecraft#window} only be initialized once in constructor,
     * so this is safe to cache it.
     */
    private static long windowPointer = 0;

    /**
     * Move the mouse to the given pixel location and then perform left click.
     *
     * @param x the x position of the pixel location
     * @param y the y position of the pixel location
     */
    public static void moveAndLeftClick(int x, int y) {
        move(x, y);
        // fix the https://github.com/khanshoaib3/minecraft-access/issues/65
        if (OsUtils.isWindows()) {
            try {
                // with a little bit of waiting, everything is ok now.
                // I've tried to set the value to 10, and it doesn't always work, 20 is fine.
                TimeUnit.MILLISECONDS.sleep(20);
            } catch (Exception ignored) {
            }
        }
        Key.LEFT.click();
    }

    /**
     * Move the mouse to the given pixel location.
     *
     * @param x the x position of the pixel location
     * @param y the y position of the pixel location
     */
    public static void move(int x, int y) {
        log.debug("Moving mouse to x:%d y:%d".formatted(x, y));
        GLFW.glfwSetCursorPos(getWindowPointer(), x, y);
        getMouseHandler().move(getWindowPointer(), x, y);
    }

    /**
     * Move the mouse to the given pixel location after a delay.
     *
     * @param x     the x position of the pixel location
     * @param y     the y position of the pixel location
     * @param delay delay amount in milliseconds
     */
    public static void moveAfterDelay(int x, int y, int delay) {
        try {
            log.debug("Moving mouse to x:%d y:%d after %d milliseconds".formatted(x, y, delay));
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    move(x, y);
                }
            };
            new Timer().schedule(timerTask, delay);
        } catch (Exception e) {
            log.error("Error encountered on moving mouse.", e);
        }
    }

    public record Coordinates(int x, int y) {
    }

    public static Coordinates calcRealPositionOfWidget(int x, int y) {
        Minecraft client = Minecraft.getInstance();
        Window window = client.getWindow();

        int realX, realY;
        realX = (int) ((x * window.getGuiScale()));
        realY = (int) ((y * window.getGuiScale()));
        return new Coordinates(realX, realY);
    }

    public static void move(Coordinates coordinates) {
        move(coordinates.x(), coordinates.y());
    }

    /**
     * Preform a mouse event at the given location
     *
     * @param x        x coordinate
     * @param y        y coordinate
     * @param consumer event
     */
    public static void performAt(int x, int y, Consumer<Coordinates> consumer) {
        consumer.accept(calcRealPositionOfWidget(x, y));
    }

    private static MouseHandlerAccessor getMouseHandler() {
        return (MouseHandlerAccessor) Minecraft.getInstance().mouseHandler;
    }

    private static long getWindowPointer() {
        if (windowPointer == 0) {
            windowPointer = Minecraft.getInstance().getWindow().getWindow();
        }
        return windowPointer;
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
            // if (Minecraft.ON_OSX && button == 0)
            // run macOS related logic
            int modifiers = Minecraft.ON_OSX ? 0 : 1;
            getMouseHandler().press(getWindowPointer(), key.id, action, modifiers);
        }
    }

    public enum Wheel {
        UP,
        DOWN;

        public void scroll() {
            // captured real mouse scrolling always results in x=0, y=1/-1
            int offset = this == Wheel.UP ? 1 : -1;
            getMouseHandler().scroll(getWindowPointer(), 0, offset);
        }
    }
}
