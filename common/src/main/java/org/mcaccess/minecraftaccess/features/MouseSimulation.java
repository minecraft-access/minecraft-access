package org.mcaccess.minecraftaccess.features;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyConflictContext;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.kuma.api.ManagedKeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;

/**
 * Simulate mouse key operations by programmatically invoking vanilla mouse key operation handlers.
 * Supports both press-and-hold and click functionality for mouse buttons.
 */
public class MouseSimulation implements BalmClientModule {
    private static ManagedKeyMapping keyLeftMouseButton;
    private static ManagedKeyMapping keyMiddleMouseButton;
    private static ManagedKeyMapping keyRightMouseButton;

    private Config.MouseSimulation config = Config.getInstance().mouseSimulation;
    private long lastScrollUpTime = 0;
    private long lastScrollDownTime = 0;

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_simulation");
    }

    @Override
    public void initialize() {
        ClientTickCallback.AFTER.register(this::tick);

        // Mouse Button Keys
        keyLeftMouseButton = Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_simulation.button/left"))
                .withDefault(InputBinding.key(InputConstants.KEY_LBRACKET))
                .enableKeyRepeat()
                .overrideCategory(KeyMappingCategories.MOUSE_SIMULATION)
                .withContext(KeyConflictContext.UNIVERSAL)
                .build();

        keyMiddleMouseButton = Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_simulation.button/middle"))
                .withDefault(InputBinding.key(InputConstants.KEY_BACKSLASH))
                .enableKeyRepeat()
                .overrideCategory(KeyMappingCategories.MOUSE_SIMULATION)
                .withContext(KeyConflictContext.UNIVERSAL)
                .build();

        keyRightMouseButton = Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_simulation.button/right"))
                .withDefault(InputBinding.key(InputConstants.KEY_RBRACKET))
                .enableKeyRepeat()
                .overrideCategory(KeyMappingCategories.MOUSE_SIMULATION)
                .withContext(KeyConflictContext.UNIVERSAL)
                .build();

        // Mouse Scrolling Keys
        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_simulation.scroll/up"))
                .withDefault(InputBinding.key(InputConstants.KEY_SEMICOLON))
                .enableKeyRepeat()
                .overrideCategory(KeyMappingCategories.MOUSE_SIMULATION)
                .handleWorldInput(event -> {
                    if (canScroll(lastScrollUpTime)) {
                        MouseUtils.Wheel.UP.scroll();
                        lastScrollUpTime = System.currentTimeMillis();
                    }
                    return true;
                })
                .handleScreenInput(event -> {
                    if (canScroll(lastScrollUpTime)) {
                        MouseUtils.Wheel.UP.scroll();
                        lastScrollUpTime = System.currentTimeMillis();
                    }
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_simulation.scroll/down"))
                .withDefault(InputBinding.key(InputConstants.KEY_APOSTROPHE))
                .enableKeyRepeat()
                .overrideCategory(KeyMappingCategories.MOUSE_SIMULATION)
                .handleWorldInput(event -> {
                    if (canScroll(lastScrollDownTime)) {
                        MouseUtils.Wheel.DOWN.scroll();
                        lastScrollDownTime = System.currentTimeMillis();
                    }
                    return true;
                })
                .handleScreenInput(event -> {
                    if (canScroll(lastScrollDownTime)) {
                        MouseUtils.Wheel.DOWN.scroll();
                        lastScrollDownTime = System.currentTimeMillis();
                    }
                    return true;
                })
                .build();
    }

    /**
     * Check all mouse button keys (left, middle, right) and handle press/hold/release
     */
    private void tick(Minecraft client) {
        if (keyLeftMouseButton.isDown() && !keyLeftMouseButton.wasDown()) {
            MouseUtils.Key.LEFT.press();
        } else if (!keyLeftMouseButton.isDown() && keyLeftMouseButton.wasDown()) {
            MouseUtils.Key.LEFT.release();
        }

        if (keyMiddleMouseButton.isDown() && !keyMiddleMouseButton.wasDown()) {
            MouseUtils.Key.MIDDLE.press();
        } else if (!keyMiddleMouseButton.isDown() && keyMiddleMouseButton.wasDown()) {
            MouseUtils.Key.MIDDLE.release();
        }

        if (keyRightMouseButton.isDown() && !keyRightMouseButton.wasDown()) {
            MouseUtils.Key.RIGHT.press();
        } else if (!keyRightMouseButton.isDown() && keyRightMouseButton.wasDown()) {
            MouseUtils.Key.RIGHT.release();
        }
    }

    /**
     * Check if enough time has passed for scrolling
     */
    private boolean canScroll(long lastScrollTime) {
        return System.currentTimeMillis() - lastScrollTime >= config.scrollDelayMilliseconds;
    }
}
