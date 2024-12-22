package org.mcaccess.minecraftaccess.utils.condition;

import net.minecraft.client.option.KeyBinding;
import org.jetbrains.annotations.Contract;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Function;

/**
 * A state machine that helps with complex keystroke condition checking.
 * For keys that only execute once until released and pressed again.
 */
public class Keystroke {

    protected static final List<Keystroke> instances = new ArrayList<>();

    /**
     * Save the state of keystroke at the previous tick.
     */
    protected boolean hasKeyPressed = false;

    /**
     * Expression that checking if the key (combination) is pressed now.
     */
    protected BooleanSupplier condition;

    /**
     * For checking feature triggering condition,
     * like "only-trigger-once-before-release".
     */
    protected final TriggeredAt timing;

    /**
     * Times that logic has been triggered before reset (when opposite state appears).
     */
    protected int triggeredCount;

    /**
     * Single key, {@link TriggeredAt#PRESSING}
     */
    public Keystroke(KeyBinding singleKey) {
        this(() -> KeyUtils.isAnyPressed(singleKey), TriggeredAt.PRESSING);
    }

    /**
     * Use this class as a condition checker.
     * Suitable for complex cases that there are other conditions that determine the logic is triggered or not.
     *
     * @param condition Expression that checking if the key (combination) is pressed now.
     */
    public Keystroke(BooleanSupplier condition) {
        this(condition, TriggeredAt.PRESSING);
    }

    /**
     * Let this class handles feature triggering controls.
     * Suitable for simple cases that the keystroke is the only condition that triggers the logic.
     *
     * @param condition Expression that checking if the key (combination) is pressed now.
     * @param timing    When the corresponding logic is triggered.
     */
    public Keystroke(BooleanSupplier condition, TriggeredAt timing) {
        this.condition = condition;
        this.timing = Optional.ofNullable(timing).orElse(TriggeredAt.PRESSING);
        this.triggeredCount = 0;
        instances.add(this);
    }

    /**
     * Update state according to the condition result.
     * Invoke this method at the end of feature logic.
     */
    public void updateStateForNextTick() {
        hasKeyPressed = isPressing();
        if (this.timing.happen(this)) this.triggeredCount += 1;
        // reset triggeredCount
        if (this.timing.aboutToHappen(this)) this.triggeredCount = 0;
    }

    public boolean isPressing() {
        return condition.getAsBoolean();
    }

    public boolean isNotPressing() {
        return !isPressing();
    }

    protected boolean hasPressedPreviousTick() {
        return hasKeyPressed;
    }

    public boolean isReleased() {
        return !isPressing() && hasPressedPreviousTick();
    }

    public boolean isPressed() {
        return isPressing() && !hasPressedPreviousTick();
    }

    /**
     * Check if the timing condition is met.
     */
    @Contract(pure = true)
    public boolean canBeTriggered() {
        boolean correctKeystrokeState = this.timing.happen(this);
        return correctKeystrokeState && otherTriggerConditions();
    }

    /**
     * Additional conditions for subclasses to extend.
     */
    protected boolean otherTriggerConditions() {
        return this.triggeredCount == 0;
    }

    /**
     * When the corresponding logic is triggered.<br>
     * --- released (short) ---><br>
     * pressing (long)               not-pressing (long)<br>
     * <--- pressed (short) ---
     */
    public enum TriggeredAt {
        PRESSING(Keystroke::isPressing, Keystroke::isNotPressing),
        NOT_PRESSING(Keystroke::isNotPressing, Keystroke::isPressing),
        PRESSED(Keystroke::isPressed, Keystroke::isNotPressing),
        RELEASED(Keystroke::isReleased, Keystroke::isPressing),
        ;

        private final Function<Keystroke, Boolean> triggerCondition;
        /**
         * the state ahead of triggering state
         */
        private final Function<Keystroke, Boolean> preCondition;

        TriggeredAt(Function<Keystroke, Boolean> triggerCondition, Function<Keystroke, Boolean> preCondition) {
            this.triggerCondition = triggerCondition;
            this.preCondition = preCondition;
        }

        public boolean happen(Keystroke keystroke) {
            return this.triggerCondition.apply(keystroke);
        }

        public boolean aboutToHappen(Keystroke keystroke) {
            return this.preCondition.apply(keystroke);
        }
    }

    /**
     * Let this method handle state updates,
     * no need to manually call the update method in every feature.
     */
    public static void updateInstances() {
        instances.forEach(Keystroke::updateStateForNextTick);
    }
}
