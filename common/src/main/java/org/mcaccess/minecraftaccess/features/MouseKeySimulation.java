package org.mcaccess.minecraftaccess.features;

import java.util.Set;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.options.controls.KeyBindsScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Tuple;
import org.apache.commons.lang3.tuple.Triple;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.mixin.KeyMappingAccessor;
import org.mcaccess.minecraftaccess.utils.KeyMappingsHandler;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.condition.IntervalKeystroke;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

/**
 * Simulate mouse key operations by programmatically invoking vanilla mouse key operation handlers.
 */
public class MouseKeySimulation implements BalmClientModule {
    private static final Keystroke[] MOUSE_CLICKS = new Keystroke[]{
            new Keystroke(() -> isKeyPressed(KeyMappingsHandler.Keys.MOUSE_SIMULATION_LEFT_MOUSE_KEY.mapping)),
            new Keystroke(() -> isKeyPressed(KeyMappingsHandler.Keys.MOUSE_SIMULATION_MIDDLE_MOUSE_KEY.mapping)),
            new Keystroke(() -> isKeyPressed(KeyMappingsHandler.Keys.MOUSE_SIMULATION_RIGHT_MOUSE_KEY.mapping)),
    };
    public static final Set<Triple<Keystroke, Runnable, Runnable>> MOUSE_CLICK_ACTIONS = Set.of(
            Triple.of(MOUSE_CLICKS[0], MouseUtils.Key.LEFT::press, MouseUtils.Key.LEFT::release),
            Triple.of(MOUSE_CLICKS[1], MouseUtils.Key.MIDDLE::press, MouseUtils.Key.MIDDLE::release),
            Triple.of(MOUSE_CLICKS[2], MouseUtils.Key.RIGHT::press, MouseUtils.Key.RIGHT::release)
    );
    private static final IntervalKeystroke[] MOUSE_SCROLLS = new IntervalKeystroke[]{
            new IntervalKeystroke(KeyMappingsHandler.Keys.MOUSE_SIMULATION_SCROLL_UP_KEY.mapping),
            new IntervalKeystroke(KeyMappingsHandler.Keys.MOUSE_SIMULATION_SCROLL_DOWN_KEY.mapping),
    };
    public static final Set<Tuple<IntervalKeystroke, Runnable>> MOUSE_SCROLL_ACTIONS = Set.of(
            new Tuple<IntervalKeystroke, Runnable>(MOUSE_SCROLLS[0], MouseUtils.Wheel.UP::scroll),
            new Tuple<IntervalKeystroke, Runnable>(MOUSE_SCROLLS[1], MouseUtils.Wheel.DOWN::scroll)
    );

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_key_simulation");
    }

    @Override
    public void initialize() {
        ClientTickCallback.AFTER.register(this::tick);
    }

    private static void loadConfig() {
        Config.MouseSimulation config = Config.getInstance().mouseSimulation;
        MOUSE_SCROLLS[0].interval.setDelay(config.scrollDelayMilliseconds, Interval.Unit.MILLISECOND);
        MOUSE_SCROLLS[1].interval.setDelay(config.scrollDelayMilliseconds, Interval.Unit.MILLISECOND);
    }

    private void tick(Minecraft client) {
        if (client.screen != null && (client.screen.getFocused() instanceof EditBox || client.screen instanceof KeyBindsScreen)) {
            return;
        }

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

    private static boolean isKeyPressed(KeyMapping keyMapping) {
        if (keyMapping.isUnbound()) return false;

        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), ((KeyMappingAccessor) keyMapping).getKey().getValue());
    }
}
