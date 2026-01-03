package org.mcaccess.minecraftaccess.features;

import java.util.Map;
import java.util.stream.IntStream;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.Kuma;
import net.blay09.mods.kuma.api.ManagedKeyMapping;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ScrollableLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.AccessMenuFunction;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.events.ClientPlayingTick;

public class AccessMenu implements BalmClientModule {
    private static ManagedKeyMapping keyAccessMenu;
    private final Interval[] intervals = IntStream.range(0, 10)
            .mapToObj(i -> Interval.sec(1))
            .toArray(Interval[]::new);

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "access_menu");
    }

    @Override
    public void initialize() {
        ClientPlayingTick.AFTER.register(this::tick);

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
                .handleScreenInput(event -> {
                    if (event.screen() instanceof GUI) {
                        Minecraft.getInstance().setScreen(null);
                        return true;
                    } else {
                        return false;
                    }
                })
                .build();
    }

    public void tick(Minecraft client, LocalPlayer player, Level level) {
        if (client.screen == null) {
            if (client.hasAltDown()) {
                for (byte i = 0; i < 10; i++) {
                    RegisteredFunction function = getShortcuts()[i];
                    if (InputConstants.isKeyDown(client.getWindow(), InputConstants.KEY_0 + i) && function.function().enabled() && intervals[i].isReady()) {
                        player.clientSideCloseContainer();
                        function.function().execute();
                    }
                }
                return;
            }

            for (RegisteredFunction function : MainClass.registry(RegisteredFunction.class).values()) {
                while (function.key().consumeClick()) {
                    if (function.function().enabled()) {
                        function.function().execute();
                    }
                }
            }
        }
    }

    private static RegisteredFunction[] getShortcuts() {
        Config.AccessMenu.ShortcutBar config = Config.getInstance().accessMenu.shortcutBar;
        Map<Identifier, RegisteredFunction> registry = MainClass.registry(RegisteredFunction.class);
        return new RegisteredFunction[]{
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

    public record RegisteredFunction(AccessMenuFunction function, KeyMapping key) {
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
                RegisteredFunction function = MainClass.registry(RegisteredFunction.class).get(key);
                MutableComponent label = Component.translatable(key.toLanguageKey("access_menu_function"));
                for (byte i = 0; i < 10; i++) {
                    if (getShortcuts()[i] == function) {
                        label.append(Component.literal(String.format(" [%d]", i)).withColor(0xbbbbbb));
                    }
                }
                Button button = Button.builder(label, b -> {
                    onClose();
                    function.function().execute();
                }).width(Math.min(Button.BIG_WIDTH, width / 2 - 15)).build();
                button.active = function.function().enabled();
                rowHelper.addChild(button);
            }

            ScrollableLayout scroll = layout.addToContents(new ScrollableLayout(minecraft, grid, layout.getContentHeight()));
            scroll.setMaxHeight(layout.getContentHeight());
            layout.visitWidgets(this::addRenderableWidget);
            layout.arrangeElements();
        }

        @Override
        public boolean keyPressed(KeyEvent event) {
            if (event.getDigit() != -1) {
                RegisteredFunction function = getShortcuts()[event.getDigit()];
                if (function.function().enabled()) {
                    onClose();
                    function.function().execute();
                    return true;
                }
            }
            return super.keyPressed(event);
        }
    }
}
