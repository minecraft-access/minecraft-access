package org.mcaccess.minecraftaccess.test_utils.extensions;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mcaccess.minecraftaccess.config.Config;
import org.mcaccess.minecraftaccess.test_utils.MockConfig;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Mock logic "Config.getInstance()" in "getInstance" method in every "*ConfigMap" class.
 */
public class MockConfigExtension implements BeforeAllCallback, AfterAllCallback {
    private MockedStatic<Config> ms;

    @Override
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void beforeAll(ExtensionContext extensionContext) {
        this.ms = Mockito.mockStatic(Config.class);
        this.ms.when(Config::getInstance).thenReturn(new MockConfig());
    }

    @Override public void afterAll(ExtensionContext extensionContext) {
        this.ms.close();
    }
}
