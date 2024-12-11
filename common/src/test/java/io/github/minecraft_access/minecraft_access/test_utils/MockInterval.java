package io.github.minecraft_access.minecraft_access.test_utils;

import io.github.minecraft_access.minecraft_access.utils.condition.Interval;

public class MockInterval extends Interval {
    public static final MockInterval ALWAYS_READY = new MockInterval(0, 0);
    boolean ready = true;

    public MockInterval(long lastRunTime, long delayInNanoTime) {
        super(lastRunTime, delayInNanoTime);
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    @Override
    public boolean isReady() {
        return ready;
    }
}