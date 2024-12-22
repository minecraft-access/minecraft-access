package com.github.khanshoaib3.minecraft_access.utils.condition;

import java.util.function.BooleanSupplier;

/**
 * For keys that you can keep pressing and the function executes at intervals.
 */
public class IntervalKeystroke extends KeystrokeTiming {
    /**
     * @param condition Expression that checking if the key (combination) is pressed now.
     */
    public IntervalKeystroke(BooleanSupplier condition) {
        super(condition);
    }

    /**
     * @param condition Expression that checking if the key (combination) is pressed now.
     * @param timing    When the corresponding logic is triggered.
     */
    public IntervalKeystroke(BooleanSupplier condition, TriggeredAt timing) {
        super(condition, timing);
    }

    /**
     * @param condition Expression that checking if the key (combination) is pressed now.
     * @param timing    When the corresponding logic is triggered.
     * @param interval  The maximum interval between first and second keystroke, default is 750ms.
     */
    @SuppressWarnings("unused")
    public IntervalKeystroke(BooleanSupplier condition, TriggeredAt timing, Interval interval) {
        super(condition, timing, interval);
    }

    public boolean isCooledDownAndTriggered() {
        return this.canBeTriggered() && interval.isReady();
    }

    /**
     * Always return true so the key can be triggered multiple times,
     * as long as inner interval is ready, under continuous pressing.
     */
    @Override
    protected boolean otherTriggerConditions() {
        return true;
    }
}
