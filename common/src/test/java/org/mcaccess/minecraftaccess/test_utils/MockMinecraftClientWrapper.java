package org.mcaccess.minecraftaccess.test_utils;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 * Reusable mock Minecraft Client for testing.
 * Based on Mockito, we can make use of Mockito's techniques,
 * but with this additional layer of wrap, we can easily write and reuse our own custom logic.
 */
public class MockMinecraftClientWrapper {

    private final MinecraftClient mockitoClient;

    public MockMinecraftClientWrapper() {
        mockitoClient = mock(MinecraftClient.class);

        lenient().doAnswer((i) -> {
            // assign screen param to desired field to simulate real behavior
            mockitoClient.currentScreen = i.getArgument(0);
            return null;
        }).when(mockitoClient).setScreen(any());
    }

    public MinecraftClient mockito() {
        return mockitoClient;
    }

    public void setScreen(Class<? extends Screen> screenClass) {
        Screen screen = Mockito.mock(screenClass);
        lenient().doAnswer((ignored) -> {
            mockitoClient.currentScreen = null;
            return null;
        }).when(screen).close();

        mockitoClient.currentScreen = screen;
    }

    public void verifyOpeningMenuOf(Class<? extends Screen> screenClass) {
        assertThat(mockitoClient.currentScreen).as("the menu should be opened").isOfAnyClassIn(screenClass);
    }

    public void verifyClosingMenu() {
        assertThat(mockitoClient.currentScreen).as("the menu should be closed").isNull();
    }
}
