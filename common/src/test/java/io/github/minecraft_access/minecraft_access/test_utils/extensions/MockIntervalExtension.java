package io.github.minecraft_access.minecraft_access.test_utils.extensions;

import io.github.minecraft_access.minecraft_access.test_utils.MockInterval;
import io.github.minecraft_access.minecraft_access.utils.condition.Interval;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

/**
 * Make all {@link Interval} always be ready while testing, to avoid unnecessary waiting.
 */
public class MockIntervalExtension implements BeforeAllCallback, AfterAllCallback {
    private MockedStatic<Interval> ms;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        this.ms = Mockito.mockStatic(Interval.class);
        this.ms.when(() -> Interval.inMilliseconds(anyLong(), any())).thenReturn(MockInterval.ALWAYS_READY);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        this.ms.close();
    }
}
