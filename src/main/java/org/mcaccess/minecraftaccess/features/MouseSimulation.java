package org.mcaccess.minecraftaccess.features;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.kuma.api.ManagedKeyMapping;
import net.blay09.mods.kuma.api.ScreenInputEvent;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.ClientConfig;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;

/**
 * Replicate mouse button and scroll wheel functionality on the keyboard
 * without rebinding vanilla controls off of the mouse.
 */
public class MouseSimulation implements BalmClientModule {
    private ManagedKeyMapping keyLeftMouseButton;
    private ManagedKeyMapping keyRightMouseButton;
    private ManagedKeyMapping keyScrollUp;
    private ManagedKeyMapping keyScrollDown;

    private boolean leftDown = false;
    private boolean rightDown = false;

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
                .overrideCategory(KeyMappingCategories.MOUSE_SIMULATION)
                .handleWorldInput(event -> {
                    if (Minecraft.getInstance().gui.overlay() != null) {
                        return false;
                    }
                    if (event.keyMapping().isDown() && !leftDown) {
                        pressMouseButton(InputConstants.MOUSE_BUTTON_LEFT);
                        leftDown = true;
                    } else if (!event.keyMapping().isDown() && leftDown) {
                        releaseMouseButton(InputConstants.MOUSE_BUTTON_LEFT);
                        leftDown = false;
                    }
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_simulation.button/middle"))
                .withDefault(InputBinding.key(InputConstants.KEY_BACKSLASH))
                .overrideCategory(KeyMappingCategories.MOUSE_SIMULATION)
                .handleWorldInput(event -> {
                    if (Minecraft.getInstance().gui.overlay() != null) {
                        return false;
                    }
                    if (event.keyMapping().isDown()) {
                        KeyMapping.click(InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_MIDDLE));
                    }
                    return true;
                })
                .build();

        keyRightMouseButton = Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_simulation.button/right"))
                .withDefault(InputBinding.key(InputConstants.KEY_RBRACKET))
                .overrideCategory(KeyMappingCategories.MOUSE_SIMULATION)
                .handleWorldInput(event -> {
                    if (Minecraft.getInstance().gui.overlay() != null) {
                        return false;
                    }
                    if (event.keyMapping().isDown() && !rightDown) {
                        pressMouseButton(InputConstants.MOUSE_BUTTON_RIGHT);
                        rightDown = true;
                    } else if (!event.keyMapping().isDown() && rightDown) {
                        releaseMouseButton(InputConstants.MOUSE_BUTTON_RIGHT);
                        rightDown = false;
                    }
                    return true;
                })
                .build();

        // Mouse Scrolling Keys
        keyScrollUp = Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_simulation.scroll/up"))
                .withDefault(InputBinding.key(InputConstants.KEY_SEMICOLON))
                .enableKeyRepeat()
                .ignoreScreenFocus()
                .overrideCategory(KeyMappingCategories.MOUSE_SIMULATION)
                .handleWorldInput(event -> {
                    if (event.keyMapping().isDown() && canScroll(lastScrollUpTime)) {
                        scrollWorld(1.0);
                        lastScrollUpTime = System.currentTimeMillis();
                    }
                    return true;
                })
                .handleScreenInput(event -> handleScreenScroll(event, 1.0, true))
                .build();

        keyScrollDown = Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "mouse_simulation.scroll/down"))
                .withDefault(InputBinding.key(InputConstants.KEY_APOSTROPHE))
                .enableKeyRepeat()
                .ignoreScreenFocus()
                .overrideCategory(KeyMappingCategories.MOUSE_SIMULATION)
                .handleWorldInput(event -> {
                    if (event.keyMapping().isDown() && canScroll(lastScrollDownTime)) {
                        scrollWorld(-1.0);
                        lastScrollDownTime = System.currentTimeMillis();
                    }
                    return true;
                })
                .handleScreenInput(event -> handleScreenScroll(event, -1.0, false))
                .build();
    }

    /**
     * Handle release for held mouse buttons and repeat for held scroll keys in-world.
     */
    private void tick(Minecraft client) {
        boolean inWorld = client.gui.screen() == null && client.gui.overlay() == null;
        if (leftDown && !(inWorld && keyLeftMouseButton.isActiveAndDown())) {
            releaseMouseButton(InputConstants.MOUSE_BUTTON_LEFT);
            leftDown = false;
        }
        if (rightDown && !(inWorld && keyRightMouseButton.isActiveAndDown())) {
            releaseMouseButton(InputConstants.MOUSE_BUTTON_RIGHT);
            rightDown = false;
        }

        if (inWorld) {
            if (keyScrollUp.isActiveAndDown() && canScroll(lastScrollUpTime)) {
                scrollWorld(1.0);
                lastScrollUpTime = System.currentTimeMillis();
            }
            if (keyScrollDown.isActiveAndDown() && canScroll(lastScrollDownTime)) {
                scrollWorld(-1.0);
                lastScrollDownTime = System.currentTimeMillis();
            }
        }
    }

    private static void pressMouseButton(int mouseButton) {
        InputConstants.Key mouseKey = InputConstants.Type.MOUSE.getOrCreate(mouseButton);
        KeyMapping.set(mouseKey, true);
        KeyMapping.click(mouseKey);
    }

    private static void releaseMouseButton(int mouseButton) {
        KeyMapping.set(InputConstants.Type.MOUSE.getOrCreate(mouseButton), false);
    }

    private static void scrollWorld(double yOffset) {
        Minecraft client = Minecraft.getInstance();
        client.mouseHandler.onScroll(client.getWindow().handle(), 0.0, yOffset);
    }

    private boolean handleScreenScroll(ScreenInputEvent event, double offset, boolean isUp) {
        GuiEventListener focused = event.getFocusedElement();
        if (focused instanceof EditBox || focused instanceof MultiLineEditBox) {
            return false;
        }

        long lastTime = isUp ? lastScrollUpTime : lastScrollDownTime;
        if (!canScroll(lastTime)) {
            return true;
        }

        double x = event.mouseX();
        double y = event.mouseY();
        if (focused != null) {
            ScreenRectangle rect = focused.getRectangle();
            x = rect.left() + rect.width() / 2.0;
            y = rect.top() + rect.height() / 2.0;
        }

        boolean handled = event.screen().mouseScrolled(x, y, 0.0, offset);
        if (handled) {
            long now = System.currentTimeMillis();
            if (isUp) {
                lastScrollUpTime = now;
            } else {
                lastScrollDownTime = now;
            }
        }
        return handled;
    }

    /**
     * Check if enough time has passed for scrolling.
     */
    private boolean canScroll(long lastScrollTime) {
        return System.currentTimeMillis() - lastScrollTime >= ClientConfig.getInstance().mouseSimulation.scrollDelayMilliseconds;
    }
}
