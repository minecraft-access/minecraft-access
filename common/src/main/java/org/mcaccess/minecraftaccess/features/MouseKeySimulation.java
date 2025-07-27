package org.mcaccess.minecraftaccess.features;

import java.util.Set;

import net.minecraft.util.Tuple;
import org.apache.commons.lang3.tuple.Triple;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.condition.IntervalKeystroke;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

/**
 * Simulate mouse key operations by programmatically invoking vanilla mouse key operation handlers.
 */
public final class MouseKeySimulation {
    private static final Keystroke[] MOUSE_CLICKS = new Keystroke[]{
            new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.MOUSE_SIMULATION_LEFT_MOUSE_KEY.mapping)),
            new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.MOUSE_SIMULATION_MIDDLE_MOUSE_KEY.mapping)),
            new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.MOUSE_SIMULATION_RIGHT_MOUSE_KEY.mapping)),
    };
    public static final Set<Triple<Keystroke, Runnable, Runnable>> MOUSE_CLICK_ACTIONS = Set.of(
            Triple.of(MOUSE_CLICKS[0], MouseUtils.Key.LEFT::press, MouseUtils.Key.LEFT::release),
            Triple.of(MOUSE_CLICKS[1], MouseUtils.Key.MIDDLE::press, MouseUtils.Key.MIDDLE::release),
            Triple.of(MOUSE_CLICKS[2], MouseUtils.Key.RIGHT::press, MouseUtils.Key.RIGHT::release)
    );
    private static final IntervalKeystroke[] MOUSE_SCROLLS = new IntervalKeystroke[]{
            new IntervalKeystroke(KeyBindingsHandler.MOUSE_SIMULATION_SCROLL_UP_KEY.mapping),
            new IntervalKeystroke(KeyBindingsHandler.MOUSE_SIMULATION_SCROLL_DOWN_KEY.mapping),
    };
    public static final Set<Tuple<IntervalKeystroke, Runnable>> MOUSE_SCROLL_ACTIONS = Set.of(
            new Tuple<IntervalKeystroke, Runnable>(MOUSE_SCROLLS[0], MouseUtils.Wheel.UP::scroll),
            new Tuple<IntervalKeystroke, Runnable>(MOUSE_SCROLLS[1], MouseUtils.Wheel.DOWN::scroll)
    );

    private MouseKeySimulation() {
    }

    private static void loadConfig() {
        Config.MouseSimulation config = Config.getInstance().mouseSimulation;
        MOUSE_SCROLLS[0].interval.setDelay(config.scrollDelayMilliseconds, Interval.Unit.MILLISECOND);
        MOUSE_SCROLLS[1].interval.setDelay(config.scrollDelayMilliseconds, Interval.Unit.MILLISECOND);
    }

    public static void tick() {
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
