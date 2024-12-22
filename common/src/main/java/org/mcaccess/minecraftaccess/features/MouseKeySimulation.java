package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.config.config_maps.MouseSimulationConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.condition.Interval;
import com.github.khanshoaib3.minecraft_access.utils.condition.IntervalKeystroke;
import com.github.khanshoaib3.minecraft_access.utils.condition.Keystroke;
import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;
import com.github.khanshoaib3.minecraft_access.utils.system.MouseUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Arrays;
import java.util.Set;

/**
 * Bind four mouse operations with customizable keys:<br><br>
 * 1) left mouse key pressing<br>
 * 2) right mouse key pressing<br>
 * 3) middle mouse key pressing<br>
 * 4) mouse wheel scroll up<br>
 * 5) mouse wheel scroll down
 */
@Slf4j
public class MouseKeySimulation {
    private static final MouseKeySimulation instance;

    private boolean enabled;
    private static final Keystroke[] mouseClicks = new Keystroke[3];
    public static final Set<Triple<Keystroke, Runnable, Runnable>> MOUSE_CLICK_ACTIONS;
    private static final IntervalKeystroke[] mouseScrolls = new IntervalKeystroke[2];
    public static final Set<Pair<IntervalKeystroke, Runnable>> MOUSE_SCROLL_ACTIONS;

    static {
        try {
            instance = new MouseKeySimulation();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating AttackAndUseSimulation instance");
        }

        // config keystroke conditions
        mouseClicks[0] = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().mouseSimulationLeftMouseKey));
        mouseClicks[1] = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().mouseSimulationMiddleMouseKey));
        mouseClicks[2] = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().mouseSimulationRightMouseKey));
        mouseScrolls[0] = new IntervalKeystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().mouseSimulationScrollUpKey), Keystroke.TriggeredAt.PRESSING);
        mouseScrolls[1] = new IntervalKeystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().mouseSimulationScrollDownKey), Keystroke.TriggeredAt.PRESSING);

        MOUSE_SCROLL_ACTIONS = Set.of(
                new Pair<IntervalKeystroke, Runnable>(mouseScrolls[0], MouseUtils::scrollUp),
                new Pair<IntervalKeystroke, Runnable>(mouseScrolls[1], MouseUtils::scrollDown)
        );

        MOUSE_CLICK_ACTIONS = Set.of(
                Triple.of(mouseClicks[0], MouseUtils::leftDown, MouseUtils::leftUp),
                Triple.of(mouseClicks[1], MouseUtils::middleDown, MouseUtils::middleUp),
                Triple.of(mouseClicks[2], MouseUtils::rightDown, MouseUtils::rightUp)
        );
    }

    public static synchronized MouseKeySimulation getInstance() {
        return instance;
    }

    public void update() {
        try {
            loadConfigurations();

            if (!enabled) return;
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            execute();

        } catch (Exception e) {
            log.error("An error occurred while executing MouseKeySimulation", e);
        }
    }

    private void loadConfigurations() {
        MouseSimulationConfigMap map = MouseSimulationConfigMap.getInstance();
        this.enabled = map.isEnabled();
        mouseScrolls[0].setInterval(Interval.inMilliseconds(map.getScrollDelayInMilliseconds(), mouseScrolls[0].interval()));
        mouseScrolls[1].setInterval(Interval.inMilliseconds(map.getScrollDelayInMilliseconds(), mouseScrolls[1].interval()));
    }

    private void execute() {
        MOUSE_SCROLL_ACTIONS.forEach(t -> {
            if (t.getLeft().isCooledDownAndTriggered()) {
                t.getRight().run();
            }
        });

        MOUSE_CLICK_ACTIONS.forEach(t -> {
            if (t.getLeft().isPressed()) {
                t.getMiddle().run();
            } else if (t.getLeft().isReleased()) {
                t.getRight().run();
            }
        });

        Arrays.stream(mouseClicks).forEach(Keystroke::updateStateForNextTick);
        Arrays.stream(mouseScrolls).forEach(IntervalKeystroke::updateStateForNextTick);
    }
}
