package com.github.khanshoaib3.minecraft_access.utils.condition;

import java.util.function.BooleanSupplier;

public class DoubleClick extends KeystrokeTiming {
    /**
     * @param condition Expression that checking if the key (combination) is pressed now.
     */
    public DoubleClick(BooleanSupplier condition) {
        super(condition);
    }

    /**
     * @param condition Expression that checking if the key (combination) is pressed now.
     * @param timing    When the corresponding logic is triggered.
     */
    public DoubleClick(BooleanSupplier condition, TriggeredAt timing) {
        super(condition, timing);
    }

    /**
     * @param condition Expression that checking if the key (combination) is pressed now.
     * @param timing    When the corresponding logic is triggered.
     * @param interval  The maximum interval between first and second keystroke, default is 750ms.
     */
    public DoubleClick(BooleanSupplier condition, TriggeredAt timing, Interval interval) {
        super(condition, timing, interval);
    }

    @Override
    public void updateStateForNextTick() {
        hasKeyPressed = isPressing();

        boolean waitingTooLong = this.interval.isReady();
        if (waitingTooLong || canBeTriggered()) {
            this.triggeredCount = 0;
            return;
        }

        // count as one valid keystroke
        if (this.timing.happen(this)) {
            this.triggeredCount += 1;
            this.interval.reset();
        }
    }

    @Override
    protected boolean otherTriggerConditions() {
        // triggered at the second keystroke
        return this.triggeredCount == 1;
    }
}
