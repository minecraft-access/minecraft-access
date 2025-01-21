package org.mcaccess.minecraftaccess.features;

import net.minecraft.util.Tuple;
import org.apache.commons.lang3.tuple.Triple;
import org.mcaccess.minecraftaccess.config.config_maps.MouseSimulationConfigMap;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.condition.IntervalKeystroke;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

import java.util.Set;

/**
 * Bind four mouse operations with customizable keys:<br><br>
 * 1) left mouse key pressing<br>
 * 2) right mouse key pressing<br>
 * 3) middle mouse key pressing<br>
 * 4) mouse wheel scroll up<br>
 * 5) mouse wheel scroll down
 */
public class MouseKeySimulation {
    private static boolean enabled;
    private static final Keystroke[] mouseClicks = new Keystroke[3];
    public static final Set<Triple<Keystroke, Runnable, Runnable>> MOUSE_CLICK_ACTIONS;
    private static final IntervalKeystroke[] mouseScrolls = new IntervalKeystroke[2];
    public static final Set<Tuple<IntervalKeystroke, Runnable>> MOUSE_SCROLL_ACTIONS;

    static {
        // config keystroke conditions
        mouseClicks[0] = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().mouseSimulationLeftMouseKey));
        mouseClicks[1] = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().mouseSimulationMiddleMouseKey));
        mouseClicks[2] = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().mouseSimulationRightMouseKey));
        mouseScrolls[0] = new IntervalKeystroke(KeyBindingsHandler.getInstance().mouseSimulationScrollUpKey);
        mouseScrolls[1] = new IntervalKeystroke(KeyBindingsHandler.getInstance().mouseSimulationScrollDownKey);

        MOUSE_SCROLL_ACTIONS = Set.of(
                new Tuple<IntervalKeystroke, Runnable>(mouseScrolls[0], MouseUtils::scrollUp),
                new Tuple<IntervalKeystroke, Runnable>(mouseScrolls[1], MouseUtils::scrollDown)
        );

        MOUSE_CLICK_ACTIONS = Set.of(
                Triple.of(mouseClicks[0], MouseUtils::leftDown, MouseUtils::leftUp),
                Triple.of(mouseClicks[1], MouseUtils::middleDown, MouseUtils::middleUp),
                Triple.of(mouseClicks[2], MouseUtils::rightDown, MouseUtils::rightUp)
        );
    }

    public static void runOnTick() {
        loadConfigurations();
        if (!enabled) return;
        execute();
    }

    private static void loadConfigurations() {
        MouseSimulationConfigMap map = MouseSimulationConfigMap.getInstance();
        enabled = map.isEnabled();
        mouseScrolls[0].interval.setDelay(map.getScrollDelayInMilliseconds(), Interval.Unit.Millisecond);
        mouseScrolls[1].interval.setDelay(map.getScrollDelayInMilliseconds(), Interval.Unit.Millisecond);
    }

    private static void execute() {
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
