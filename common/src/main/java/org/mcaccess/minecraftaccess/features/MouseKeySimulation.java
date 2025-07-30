package org.mcaccess.minecraftaccess.features;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

/**
 * Simulate mouse key operations by programmatically invoking vanilla mouse key operation handlers.
 * Supports both press-and-hold and click functionality for mouse buttons.
 */
public class MouseKeySimulation {
    private static int scrollDelayMilliseconds;
    private static long lastScrollUpTime = 0;
    private static long lastScrollDownTime = 0;

    private static boolean leftMouseKeyWasDown = false;
    private static boolean middleMouseKeyWasDown = false;
    private static boolean rightMouseKeyWasDown = false;

    private static boolean scrollUpKeyWasDown = false;
    private static boolean scrollDownKeyWasDown = false;

    public static void tick() {
        loadConfig();
        checkMouseButtonKeys();
        checkScrollKeys();
    }

    /**
     * Load configuration settings
     */
    private static void loadConfig() {
        scrollDelayMilliseconds = Config.getInstance().mouseSimulation.scrollDelayMilliseconds;
    }

    /**
     * Check all mouse button keys (left, middle, right) and handle press/hold/release
     */
    private static void checkMouseButtonKeys() {
        boolean leftMouseKeyIsDown = KeyBindingsHandler.MOUSE_SIMULATION_LEFT_MOUSE_KEY.mapping.isDown();
        if (leftMouseKeyIsDown && !leftMouseKeyWasDown) {
            MouseUtils.Key.LEFT.press();
        } else if (!leftMouseKeyIsDown && leftMouseKeyWasDown) {
            MouseUtils.Key.LEFT.release();
        }
        leftMouseKeyWasDown = leftMouseKeyIsDown;

        boolean middleMouseKeyIsDown = KeyBindingsHandler.MOUSE_SIMULATION_MIDDLE_MOUSE_KEY.mapping.isDown();
        if (middleMouseKeyIsDown && !middleMouseKeyWasDown) {
            MouseUtils.Key.MIDDLE.press();
        } else if (!middleMouseKeyIsDown && middleMouseKeyWasDown) {
            MouseUtils.Key.MIDDLE.release();
        }
        middleMouseKeyWasDown = middleMouseKeyIsDown;

        boolean rightMouseKeyIsDown = KeyBindingsHandler.MOUSE_SIMULATION_RIGHT_MOUSE_KEY.mapping.isDown();
        if (rightMouseKeyIsDown && !rightMouseKeyWasDown) {
            MouseUtils.Key.RIGHT.press();
        } else if (!rightMouseKeyIsDown && rightMouseKeyWasDown) {
            MouseUtils.Key.RIGHT.release();
        }
        rightMouseKeyWasDown = rightMouseKeyIsDown;
    }

    /**
     * Check scroll keys with delay handling
     */
    private static void checkScrollKeys() {
        long currentTime = System.currentTimeMillis();

        boolean scrollUpKeyIsDown = KeyBindingsHandler.MOUSE_SIMULATION_SCROLL_UP_KEY.mapping.isDown();
        if (scrollUpKeyIsDown) {
            if (!scrollUpKeyWasDown || canScroll(lastScrollUpTime)) {
                MouseUtils.Wheel.UP.scroll();
                lastScrollUpTime = currentTime;
            }
        }
        scrollUpKeyWasDown = scrollUpKeyIsDown;

        boolean scrollDownKeyIsDown = KeyBindingsHandler.MOUSE_SIMULATION_SCROLL_DOWN_KEY.mapping.isDown();
        if (scrollDownKeyIsDown) {
            if (!scrollDownKeyWasDown || canScroll(lastScrollDownTime)) {
                MouseUtils.Wheel.DOWN.scroll();
                lastScrollDownTime = currentTime;
            }
        }
        scrollDownKeyWasDown = scrollDownKeyIsDown;
    }

    /**
     * Check if enough time has passed for scrolling
     */
    private static boolean canScroll(long lastScrollTime) {
        return System.currentTimeMillis() - lastScrollTime >= scrollDelayMilliseconds;
    }
}
