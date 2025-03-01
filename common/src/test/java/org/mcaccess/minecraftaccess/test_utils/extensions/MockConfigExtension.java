package org.mcaccess.minecraftaccess.test_utils.extensions;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.DummyConfigSerializer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mcaccess.minecraftaccess.Config;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Replace {@link Config#getSerializer()} with {@link DummyConfigSerializer} to prevent any real saving and loading
 */
public class MockConfigExtension implements BeforeAllCallback, AfterAllCallback {
    private MockedStatic<Config> ms;

    static {
        AutoConfig.register(Config.class, (ConfigSerializer.Factory<Config>) DummyConfigSerializer::new);
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        this.ms = Mockito.mockStatic(Config.class);
        //noinspection ResultOfMethodCallIgnored
        this.ms.when(Config::getInstance).thenReturn(AutoConfig.getConfigHolder(Config.class).get());
    }

    @Override public void afterAll(ExtensionContext extensionContext) {
        this.ms.close();
    }
}
