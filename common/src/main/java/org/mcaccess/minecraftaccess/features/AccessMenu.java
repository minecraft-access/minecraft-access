package org.mcaccess.minecraftaccess.features;

import java.util.Map;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.kuma.api.ManagedKeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;

public class AccessMenu implements BalmClientModule {
    private static ManagedKeyMapping keyAccessMenu;

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "access_menu");
    }

    @Override
    public void initialize() {
        keyAccessMenu = Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "other.access_menu"))
                .withDefault(InputBinding.key(InputConstants.KEY_F4))
                .overrideCategory(KeyMappingCategories.OTHER)
                .handleWorldInput(event -> {
                    Minecraft client = Minecraft.getInstance();
                    if (keyAccessMenu.getBinding().key().getValue() == InputConstants.KEY_F4 && InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_F3)) {
                        return false;
                    } else {
                        client.setScreen(new GUI());
                        return true;
                    }
                })
                .build();

        for (byte i = 0; i < 10; i++) {
            byte index = i;
            Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "access_menu.shortcuts_bar/" + index))
                    .withDefault(InputBinding.key(InputConstants.KEY_0 + i, KeyModifiers.of(KeyModifier.ALT)))
                    .skipRegistration()
                    .handleWorldInput(event -> {
                        AccessMenuFunction function = getShortcuts()[index];
                        if (function.enabled()) {
                            function.execute();
                        }
                        return true;
                    })
                    .build();
        }

        for (Map.Entry<Identifier, AccessMenuFunction> function : MainClass.registry(AccessMenuFunction.class).entrySet()) {
            Kuma.createKeyMapping(function.getKey())
                    .overrideName(identifier -> identifier.toLanguageKey("access_menu_function"))
                    .overrideCategory(KeyMappingCategories.ACCESS_MENU)
                    .handleWorldInput(event -> {
                        if (function.getValue().enabled()) {
                            function.getValue().execute();
                            return true;
                        }
                        return false;
                    })
                    .build();
        }
    }

    private static AccessMenuFunction[] getShortcuts() {
        Config.AccessMenu.ShortcutBar config = Config.getInstance().accessMenu.shortcutBar;
        Map<Identifier, AccessMenuFunction> registry = MainClass.registry(AccessMenuFunction.class);
        return new AccessMenuFunction[]{
                registry.get(config.key0),
                registry.get(config.key1),
                registry.get(config.key2),
                registry.get(config.key3),
                registry.get(config.key4),
                registry.get(config.key5),
                registry.get(config.key6),
                registry.get(config.key7),
                registry.get(config.key8),
                registry.get(config.key9),
        };
    }

    public static class GUI extends Screen {
        public GUI() {
            super(Component.translatable("minecraft_access.gui.screen.access_menu"));
        }

        @Override
        public void init() {
            HeaderAndFooterLayout layout = new HeaderAndFooterLayout(this);
            layout.addTitleHeader(title, minecraft.font);

            GridLayout grid = new GridLayout().spacing(10);
            GridLayout.RowHelper rowHelper = grid.createRowHelper(2);

            for (Identifier key : Config.getInstance().accessMenu.functions) {
                AccessMenuFunction function = MainClass.registry(AccessMenuFunction.class).get(key);
                MutableComponent label = Component.translatable(key.toLanguageKey("access_menu_function"));
                for (byte i = 0; i < 10; i++) {
                    if (getShortcuts()[i] == function) {
                        label.append(Component.literal(String.format(" [%d]", i)).withColor(0xbbbbbb));
                    }
                }
                Button button = Button.builder(label, b -> {
                    onClose();
                    function.execute();
                }).width(Math.min(Button.BIG_WIDTH, width / 2 - 15)).build();
                button.active = function.enabled();
                rowHelper.addChild(button);
            }

            ScrollableLayout scroll = layout.addToContents(new ScrollableLayout(minecraft, grid, layout.getContentHeight()));
            scroll.setMaxHeight(layout.getContentHeight());
            layout.visitWidgets(this::addRenderableWidget);
            layout.arrangeElements();
        }

        @Override
        public boolean keyPressed(@NotNull KeyEvent event) {
            if (keyAccessMenu.isActiveAndMatchesKey(event)) {
                onClose();
                return true;
            }
            if (event.getDigit() != -1) {
                AccessMenuFunction function = getShortcuts()[event.getDigit()];
                if (function.enabled()) {
                    onClose();
                    function.execute();
                    return true;
                }
            }
            return super.keyPressed(event);
        }
    }
}
