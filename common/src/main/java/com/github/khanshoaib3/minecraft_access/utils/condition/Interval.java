package com.github.khanshoaib3.minecraft_access.utils.condition;

import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;

/**
 * An auto-refresh countdown timer for controlling interval execution of features.
 */
public class Interval {
    protected long lastRunTime;
    private long delay;

    protected Interval(long lastRunTime, long delayInNanoTime) {
        this.lastRunTime = lastRunTime;
        this.delay = delayInNanoTime;
    }

    /**
     * In milliseconds
     */
    public static Interval ms(long delay) {
        // 1 milliseconds = 1*10^6 nanoseconds
        return new Interval(System.nanoTime(), Unit.Millisecond.toNano(delay));
    }

    /**
     * In seconds
     */
    public static Interval sec(long delay) {
        // 1 seconds = 1*10^9 nanoseconds
        return new Interval(System.nanoTime(), Unit.Second.toNano(delay));
    }

    /**
     * Use the value of {@link OtherConfigsMap#getMultipleClickSpeedInMilliseconds()} as delay
     */
    public static Interval defaultDelay() {
        return Interval.ms(OtherConfigsMap.getInstance().getMultipleClickSpeedInMilliseconds());
    }

    public void reset() {
        lastRunTime = System.nanoTime();
    }

    /**
     * Check if the delay has cooled down. (Will auto-reset the timer if true)
     */
    public boolean isReady() {
        if (System.nanoTime() - lastRunTime > delay) {
            reset();
            return true;
        } else {
            return false;
        }
    }

    public void setDelay(long delay, Unit unit) {
        this.delay = unit.toNano(delay);
    }

    public enum Unit {
        Millisecond(1000_000),
        Second(1000_000_000);

        private final long factor;

        Unit(long factor) {
            this.factor = factor;
        }

        public long toNano(long value) {
            return value * this.factor;
        }
    }
}
