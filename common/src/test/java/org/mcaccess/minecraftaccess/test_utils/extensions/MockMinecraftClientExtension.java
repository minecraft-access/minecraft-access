package org.mcaccess.minecraftaccess.test_utils.extensions;

import net.minecraft.client.Minecraft;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mcaccess.minecraftaccess.test_utils.MockMinecraftClientWrapper;
import org.mcaccess.minecraftaccess.test_utils.annotations.MockMinecraftClient;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Objects;

/**
 * At {@link BeforeTestExecutionCallback} phase, assign new {@link MockMinecraftClientWrapper} instances to first field that tagged with {@link MockMinecraftClient}.
 * Close the mocked static instance at {@link AfterTestExecutionCallback} phase.
 */
public class MockMinecraftClientExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private MockedStatic<Minecraft> ms;

    @Override public void beforeTestExecution(ExtensionContext extensionContext) {
        this.ms = Mockito.mockStatic(Minecraft.class);
        MockMinecraftClientWrapper wrapper = new MockMinecraftClientWrapper();

        // Mock "MinecraftClient.getInstance()" that commonly used to get current MinecraftClient singleton instance.
        this.ms.when(Minecraft::getInstance).thenReturn(wrapper.mockito());

        enableMCBootstrapFlag();

        Object testInstance = extensionContext.getRequiredTestInstance();
        Arrays.stream(testInstance.getClass().getDeclaredFields())
                .filter(f -> f.isAnnotationPresent(org.mcaccess.minecraftaccess.test_utils.annotations.MockMinecraftClient.class))
                .forEach(f -> {
                    try {
                        f.trySetAccessible();
                        f.set(testInstance, wrapper);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * To avoid some Minecraft classes' static init failure.
     */
    private static void enableMCBootstrapFlag() {
        try {
            var b = Bootstrap.class.getDeclaredField("isBootstrapped");
            b.trySetAccessible();
            b.set(null, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void afterTestExecution(ExtensionContext extensionContext) {
        if (Objects.nonNull(this.ms)) this.ms.close();
    }
}
