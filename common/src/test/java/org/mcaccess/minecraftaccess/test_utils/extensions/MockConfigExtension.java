package org.mcaccess.minecraftaccess.test_utils.extensions;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.DummyConfigSerializer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.mcaccess.minecraftaccess.Config;

/**
 * Use {@link DummyConfigSerializer} to prevent any real saving and loading
 */
public class MockConfigExtension implements BeforeAllCallback, AfterAllCallback {
    private MockedStatic<Config> ms;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        AutoConfig.register(Config.class, DummyConfigSerializer::new);
        ms = Mockito.mockStatic(Config.class);
        //noinspection ResultOfMethodCallIgnored
        ms.when(Config::getInstance).thenReturn(AutoConfig.getConfigHolder(Config.class).get());
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        ms.close();
    }
}
