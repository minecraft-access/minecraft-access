package com.github.khanshoaib3.minecraft_access.utils.condition;

import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;
import com.github.khanshoaib3.minecraft_access.features.CameraControls;

/**
 * An auto-refresh countdown timer for controlling interval execution of features.
 */
public class Interval {
    public long lastRunTime;
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
        // There is configuration that set to 0 to disable the timer
        // ref: Read Crosshair - Repeat Speaking Interval (in milliseconds) (0 to disable)
        if (delay == 0) return false;
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

    /**
     * When the interval is used at the scope of whole feature, e.g. {@link CameraControls},
     * in which the feature is manually triggered by player's keystrokes while having a rate limitation,
     * this method is called at the end of feature logic to adjust the next feature execution time.
     *
     * @param anyFunctionTriggered whether any function of the feature has been triggered in this tick
     */
    public void adjustNextReadyTimeBy(boolean anyFunctionTriggered) {
        if (anyFunctionTriggered) {
            // make the next action to be executed after one complete interval
            reset();
        } else {
            // immediately ready for next tick
            beReady();
        }
    }


    /**
     * Make the interval ready immediately
     */
    public void beReady() {
        lastRunTime = 0;
    }
}
