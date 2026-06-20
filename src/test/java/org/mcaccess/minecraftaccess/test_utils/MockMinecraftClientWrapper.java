package org.mcaccess.minecraftaccess.test_utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

/**
 * Reusable mock Minecraft Client for testing.
 * Based on Mockito, we can make use of Mockito's techniques,
 * but with this additional layer of wrap, we can easily write and reuse our own custom logic.
 */
public class MockMinecraftClientWrapper {
    private final Minecraft mockitoClient;

    public MockMinecraftClientWrapper() {
        mockitoClient = mock(Minecraft.class);

        lenient().doAnswer((i) -> {
            // assign screen param to desired field to simulate real behavior
            mockitoClient.gui.setScreen(i.getArgument(0));
            return null;
        }).when(mockitoClient).gui.setScreen(any());
    }

    public Minecraft mockito() {
        return mockitoClient;
    }

    public void setScreen(Class<? extends Screen> screenClass) {
        Screen screen = mock(screenClass);
        lenient().doAnswer((ignored) -> {
            mockitoClient.gui.setScreen(null);
            return null;
        }).when(screen).onClose();

        mockitoClient.gui.setScreen(screen);
    }

    public void verifyOpeningMenuOf(Class<? extends Screen> screenClass) {
        assertThat(mockitoClient.gui.screen()).as("the menu should be opened").isOfAnyClassIn(screenClass);
    }

    public void verifyClosingMenu() {
        assertThat(mockitoClient.gui.screen()).as("the menu should be closed").isNull();
    }
}
