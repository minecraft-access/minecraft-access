package com.github.khanshoaib3.minecraft_access.utils.condition;

/**
 * An auto-refresh countdown timer for controlling interval execution of features.
 */
public class Interval {
    protected long lastRunTime;
    private final long delay;
    private boolean isRunning;
    private final boolean disabled;

    protected Interval(long lastRunTime, long delayInNanoTime) {
        this.lastRunTime = lastRunTime;
        this.delay = delayInNanoTime;
        this.disabled = delayInNanoTime == 0;
        this.isRunning = false;
    }

    /**
     * Build or update instance according to delay config.
     *
     * @param delay    config value
     * @param previous the interval class variable
     */
    public static Interval inMilliseconds(long delay, Interval... previous) {
        if (previous == null || previous.length == 0 || previous[0] == null) {
            // 1 milliseconds = 1*10^6 nanoseconds
            return new Interval(System.nanoTime(), delay * 1000_000);
        } else {
            Interval interval = previous[0];
            boolean configChanged = delay * 1000_000 != interval.delay;
            return configChanged ? Interval.inMilliseconds(delay) : interval;
        }
    }

    public void reset() {
        lastRunTime = System.nanoTime();
    }

    /**
     * Check if the delay has cooled down. (Will auto-reset the timer if true)
     */
    public boolean isReady() {
        if (disabled) return false;

        if (System.nanoTime() - lastRunTime > delay) {
            reset();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Check if the delay has cooled down. (This will not auto-reset the timer, that will have to be done by calling the start() method)
     *
     * @return true if the delay timer has stopped or cooled down.
     */
    public boolean hasEnded() {
        if (!this.isRunning) return true;
        if (!this.isReady()) return false;

        this.isRunning = false;
        return true;
    }

    /**
     * Starts or resets the delay timer. This is recommended to be used when there is a key input.
     */
    public void start() {
        this.isRunning = true;
        reset();
    }
}
