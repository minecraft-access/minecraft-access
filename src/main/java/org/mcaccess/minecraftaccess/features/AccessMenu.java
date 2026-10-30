package org.mcaccess.minecraftaccess.features;

import java.util.Map;
import java.util.Optional;

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

import org.mcaccess.minecraftaccess.ClientConfig;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.api.AddonRegistry;
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
                .handleWorldInput(_ -> {
                    Minecraft client = Minecraft.getInstance();
                    if (keyAccessMenu.getBinding().key().getValue() == InputConstants.KEY_F4
                            && InputConstants.isKeyDown(InputConstants.KEY_F3)) {
                        return false;
                    } else {
                        client.gui.setScreen(new GUI());
                        return true;
                    }
                })
                .build();

        getShortcuts().keySet().forEach(key -> Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(
                        MainClass.MOD_ID,
                        "access_menu.shortcuts_bar/" + InputConstants.Type.KEYBOARD.getOrCreate(key).getNumericKeyValue().orElse(-1)
                ))
                .withDefault(InputBinding.key(key, KeyModifiers.of(KeyModifier.ALT)))
                .skipRegistration()
                .handleWorldInput(_ -> {
                    AccessMenuFunction value = getShortcuts().get(key);
                    if (value.enabled()) {
                        value.execute();
                    }
                    return true;
                })
                .build());

        for (Map.Entry<Identifier, AccessMenuFunction> function : MainClass.registry(AccessMenuFunction.class).entrySet()) {
            ManagedKeyMapping.RegistrationBuilder mapping = Kuma.createKeyMapping(function.getKey())
                    .overrideName(identifier -> identifier.toLanguageKey("access_menu_function"))
                    .overrideCategory(KeyMappingCategories.ACCESS_MENU_FUNCTIONS)
                    .handleWorldInput(_ -> {
                        if (function.getValue().enabled()) {
                            function.getValue().execute();
                            return true;
                        }
                        return false;
                    });
            Optional.ofNullable(MainClass.registry(AddonRegistry.AccessMenuFunctionRegistration.DefaultKeybind.class).get(function.getKey()))
                    .ifPresent(defaultKeybind -> {
                        KeyModifiers modifiers = KeyModifiers.none();
                        for (int modifier : defaultKeybind.modifiers) {
                            switch (modifier) {
                                case InputConstants.MOD_SHIFT -> modifiers.addModifier(KeyModifier.SHIFT);
                                case InputConstants.MOD_CONTROL -> modifiers.addModifier(KeyModifier.CONTROL);
                                case InputConstants.MOD_ALT -> modifiers.addModifier(KeyModifier.ALT);
                                default -> modifiers.addCustomModifier(modifier);
                            }
                        }
                        mapping.withDefault(InputBinding.key(defaultKeybind.keycode, modifiers));
                    });
            mapping.build();
        }
    }

    private static Map<Integer, AccessMenuFunction> getShortcuts() {
        ClientConfig.AccessMenuShortcutBar config = ClientConfig.getInstance().accessMenuShortcutBar;
        Map<Identifier, AccessMenuFunction> registry = MainClass.registry(AccessMenuFunction.class);

        return Map.of(
                InputConstants.KEY_0, registry.get(config.key0),
                InputConstants.KEY_1, registry.get(config.key1),
                InputConstants.KEY_2, registry.get(config.key2),
                InputConstants.KEY_3, registry.get(config.key3),
                InputConstants.KEY_4, registry.get(config.key4),
                InputConstants.KEY_5, registry.get(config.key5),
                InputConstants.KEY_6, registry.get(config.key6),
                InputConstants.KEY_7, registry.get(config.key7),
                InputConstants.KEY_8, registry.get(config.key8),
                InputConstants.KEY_9, registry.get(config.key9)
        );
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

            for (Identifier identifier : ClientConfig.getInstance().accessMenu.functions) {
                AccessMenuFunction function = MainClass.registry(AccessMenuFunction.class).get(identifier);
                MutableComponent label = Component.translatable(identifier.toLanguageKey("access_menu_function"));
                getShortcuts().forEach((key, value) -> {
                    if (value == function) {
                        label.append(Component.literal(String.format(" [%d]",
                                InputConstants.Type.KEYBOARD.getOrCreate(key).getNumericKeyValue().orElse(-1))).withColor(0xbbbbbb));
                    }
                });
                Button button = Button.builder(label, _ -> {
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

            if (event.modifiers() == 0) {
                AccessMenuFunction function = getShortcuts().get(event.key());
                if (function != null && function.enabled()) {
                    onClose();
                    function.execute();
                    return true;
                }
            }

            return super.keyPressed(event);
        }
    }
}
