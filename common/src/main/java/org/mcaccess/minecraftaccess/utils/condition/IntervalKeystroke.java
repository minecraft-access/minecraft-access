package org.mcaccess.minecraftaccess.utils.condition;

import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import net.minecraft.client.option.KeyBinding;

import java.util.function.BooleanSupplier;

/**
 * For keys that you can keep pressing and the function executes at intervals.
 */
public class IntervalKeystroke extends TimedKeystroke {
    /**
     * Single key, {@link TriggeredAt#PRESSING}
     */
    public IntervalKeystroke(KeyBinding singleKey) {
        this(() -> KeyUtils.isAnyPressed(singleKey), TriggeredAt.PRESSING);
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
    public IntervalKeystroke(BooleanSupplier condition, TriggeredAt timing, Interval interval) {
        super(condition, timing, interval);
    }

    @Override
    public boolean canBeTriggered() {
        return super.canBeTriggered() && interval.isReady();
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
