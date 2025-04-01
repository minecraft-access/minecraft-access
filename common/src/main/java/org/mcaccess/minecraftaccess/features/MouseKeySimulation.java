package org.mcaccess.minecraftaccess.features;

import net.minecraft.util.Tuple;
import org.apache.commons.lang3.tuple.Triple;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.condition.IntervalKeystroke;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

import java.util.Set;

/**
 * Simulate mouse key operations by programmatically invoking vanilla mouse key operation handlers.
 */
public class MouseKeySimulation {
    private static final Keystroke[] mouseClicks = new Keystroke[3];
    public static final Set<Triple<Keystroke, Runnable, Runnable>> MOUSE_CLICK_ACTIONS;
    private static final IntervalKeystroke[] mouseScrolls = new IntervalKeystroke[2];
    public static final Set<Tuple<IntervalKeystroke, Runnable>> MOUSE_SCROLL_ACTIONS;

    static {
        // config keystroke conditions
        mouseClicks[0] = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.mouseSimulationLeftMouseKey));
        mouseClicks[1] = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.mouseSimulationMiddleMouseKey));
        mouseClicks[2] = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.mouseSimulationRightMouseKey));
        mouseScrolls[0] = new IntervalKeystroke(KeyBindingsHandler.mouseSimulationScrollUpKey);
        mouseScrolls[1] = new IntervalKeystroke(KeyBindingsHandler.mouseSimulationScrollDownKey);

        MOUSE_SCROLL_ACTIONS = Set.of(
            new Tuple<IntervalKeystroke, Runnable>(mouseScrolls[0], () -> MouseUtils.scroll(MouseUtils.WheelDirection.UP)),
            new Tuple<IntervalKeystroke, Runnable>(mouseScrolls[1], () -> MouseUtils.scroll(MouseUtils.WheelDirection.DOWN))
        );

        MOUSE_CLICK_ACTIONS = Set.of(
            Triple.of(mouseClicks[0],
                () -> MouseUtils.press(MouseUtils.Key.LEFT),
                () -> MouseUtils.release(MouseUtils.Key.LEFT)),
            Triple.of(mouseClicks[1],
                () -> MouseUtils.press(MouseUtils.Key.MIDDLE),
                () -> MouseUtils.release(MouseUtils.Key.MIDDLE)),
            Triple.of(mouseClicks[2],
                () -> MouseUtils.press(MouseUtils.Key.RIGHT),
                () -> MouseUtils.release(MouseUtils.Key.RIGHT))
        );
    }

    private static void loadConfig() {
        Config.MouseSimulation config = Config.getInstance().mouseSimulation;
        mouseScrolls[0].interval.setDelay(config.scrollDelayMilliseconds, Interval.Unit.Millisecond);
        mouseScrolls[1].interval.setDelay(config.scrollDelayMilliseconds, Interval.Unit.Millisecond);
    }

    public static void runOnTick() {
        loadConfig();
        MOUSE_SCROLL_ACTIONS.forEach(t -> {
            if (t.getA().canBeTriggered()) {
                t.getB().run();
            }
        });

        MOUSE_CLICK_ACTIONS.forEach(t -> {
            if (t.getLeft().isPressed()) {
                t.getMiddle().run();
            } else if (t.getLeft().isReleased()) {
                t.getRight().run();
            }
        });
    }
}
