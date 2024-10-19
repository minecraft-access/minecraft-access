package com.github.khanshoaib3.minecraft_access.utils.condition;

import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/**
 * For checking keystroke conditions that related to time,
 * like "interval/rate-limitation-on-feature-execution", "double/triple-click"
 */
public abstract class TimedKeystroke extends Keystroke {
    public Interval interval;
    public static final Supplier<Interval> DEFAULT_INTERVAL = () -> Interval.ms(OtherConfigsMap.getInstance().getMultipleClickSpeedInMilliseconds());

    /**
     * @param condition Expression that checking if the key (combination) is pressed now.
     */
    public TimedKeystroke(BooleanSupplier condition) {
        this(condition, TriggeredAt.PRESSING, DEFAULT_INTERVAL.get());
    }

    /**
     * @param condition Expression that checking if the key (combination) is pressed now.
     * @param timing    When the corresponding logic is triggered.
     */
    public TimedKeystroke(BooleanSupplier condition, TriggeredAt timing) {
        this(condition, timing, DEFAULT_INTERVAL.get());
    }

    /**
     * @param condition Expression that checking if the key (combination) is pressed now.
     * @param timing    When the corresponding logic is triggered.
     * @param interval  The interval setting, the meaning is to be determined.
     */
    public TimedKeystroke(BooleanSupplier condition, TriggeredAt timing, Interval interval) {
        super(condition, timing);
        this.interval = interval;
    }
}
