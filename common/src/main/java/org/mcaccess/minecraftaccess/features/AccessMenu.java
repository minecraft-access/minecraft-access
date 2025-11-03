package org.mcaccess.minecraftaccess.features;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.debug.GameModeSwitcherScreen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.condition.MenuKeystroke;

public class AccessMenu {
    private static final Minecraft CLIENT = Minecraft.getInstance();
    private static final MenuKeystroke MENU_KEY = new MenuKeystroke(KeyBindingsHandler.Keys.ACCESS_MENU_KEY.mapping);
    private boolean gameModeSwitcherActive = false;

    public void tick() {
        if (CLIENT.player == null) return;

        if (CLIENT.screen == null) {
            if (CLIENT.hasAltDown()) {
                assert CLIENT.player != null;
                Queue<RegisteredFunction> functions = new ArrayDeque<>(MainClass.ACCESS_MENU_REGISTRY.values());
                for (byte i = 1; i < 11; i++) {
                    RegisteredFunction function = functions.poll();
                    if (function == null) {
                        break;
                    }
                    if (function.function().enabled() && InputConstants.isKeyDown(CLIENT.getWindow(), InputConstants.KEY_0 + i % 10)) {
                        CLIENT.player.clientSideCloseContainer();
                        function.function().execute();
                    }
                }
                return;
            }

            for (RegisteredFunction function : MainClass.ACCESS_MENU_REGISTRY.values()) {
                while (function.key().consumeClick()) {
                    if (function.function().enabled()) {
                        function.function().execute();
                    }
                }
            }

            if (MENU_KEY.canOpenMenu() && !gameModeSwitcherActive) {
                CLIENT.setScreen(new GUI());
            }
        }

        if (CLIENT.screen instanceof GameModeSwitcherScreen) {
            gameModeSwitcherActive = true;
        } else if (!InputConstants.isKeyDown(CLIENT.getWindow(), InputConstants.KEY_F4)) {
            gameModeSwitcherActive = false;
        }
    }

    public record RegisteredFunction(AccessMenuFunction function, KeyMapping key) {
    }

    public static class GUI extends Screen {
        public GUI() {
            super(Component.translatable("minecraft_access.gui.screen.access_menu"));
        }

        @Override
        public void init() {
            assert minecraft != null;
            HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
            layout.addTitleHeader(title, minecraft.font);

            GridLayout grid = new GridLayout().spacing(10);
            GridLayout.RowHelper rowHelper = grid.createRowHelper(2);

            for (Map.Entry<ResourceLocation, RegisteredFunction> function : MainClass.ACCESS_MENU_REGISTRY.entrySet()) {
                Component label = Component.translatable(function.getKey().toLanguageKey("access_menu_function"));
                // TODO: Shortcut in label
                Button button = Button.builder(label, b -> {
                    onClose();
                    function.getValue().function().execute();
                }).width(Math.min(Button.BIG_WIDTH, width / 2 - 15)).build();
                button.active = function.getValue().function().enabled();
                rowHelper.addChild(button);
            }

            ScrollableLayout scroll = layout.addToContents(new ScrollableLayout(minecraft, grid, layout.getContentHeight()));
            scroll.setMaxHeight(layout.getContentHeight());
            layout.visitWidgets(this::addRenderableWidget);
            layout.arrangeElements();
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (KeyBindingsHandler.Keys.ACCESS_MENU_KEY.mapping.matches(event)) {
                onClose();
                return true;
            }
            if (event.key() >= InputConstants.KEY_0 && event.key() <= InputConstants.KEY_9) {
                Optional<RegisteredFunction> function = MainClass.ACCESS_MENU_REGISTRY.values().stream()
                        .skip((event.key() - InputConstants.KEY_0 + 9) % 10)
                        .findFirst();
                if (function.isPresent()) {
                    if (function.get().function().enabled()) {
                        onClose();
                        function.get().function().execute();
                        return true;
                    }
                }
            }
            return super.keyPressed(event);
        }
    }
}
