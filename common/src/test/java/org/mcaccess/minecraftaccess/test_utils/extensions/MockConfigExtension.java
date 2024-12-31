package org.mcaccess.minecraftaccess.test_utils.extensions;

import org.mcaccess.minecraftaccess.Config;
import me.shedaniel.autoconfig.serializer.ConfigSerializer;
import me.shedaniel.autoconfig.serializer.DummyConfigSerializer;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * Replace {@link Config#getSerialiser()} with {@link DummyConfigSerializer} to prevent any real saving and loading
 */
public class MockConfigExtension implements BeforeAllCallback, AfterAllCallback {
    private MockedStatic<Config> ms;

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        this.ms = Mockito.mockStatic(Config.class);
        this.ms.when(Config::getSerialiser).thenReturn((ConfigSerializer.Factory<Config>) DummyConfigSerializer::new);
    }

    @Override public void afterAll(ExtensionContext extensionContext) {
        this.ms.close();
    }
}
