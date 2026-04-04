package org.mcaccess.minecraftaccess.test_utils;

import lombok.Setter;

import org.mcaccess.minecraftaccess.utils.condition.Interval;

public class MockInterval extends Interval {
    public static final MockInterval ALWAYS_READY = new MockInterval(0, 0);
    @Setter
    boolean ready = true;

    public MockInterval(long lastRunTime, long delayInNanoTime) {
        super(lastRunTime, delayInNanoTime);
    }

    @Override
    public boolean isReady() {
        return ready;
    }
}
