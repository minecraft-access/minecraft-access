package org.mcaccess.minecraftaccess.utils.config;

import java.lang.reflect.Field;
import java.util.Comparator;
import java.util.Objects;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;

public class RegistrySingleSelect extends BaseEntry<Identifier, Button> {
    private final Class<?> registry;
    private final String registryI18n;

    protected RegistrySingleSelect(Class<?> registry, String registryI18n, String i18n, Field field, Object config, Object defaults) {
        super(i18n, field, config, defaults);
        this.registry = registry;
        this.registryI18n = registryI18n;
    }

    @Override
    protected Button initWidget() {
        return Button.builder(Component.empty(), _ -> Minecraft.getInstance().gui.setScreen(new SelectionScreen(Minecraft.getInstance().gui.screen()))).build();
    }

    @Override
    protected void updateWidget(Button widget) {
        widget.setMessage(Component.translatable(getValue().toLanguageKey(registryI18n)));
    }

    public final class SelectionScreen extends Screen {
        private final Screen previous;
        private HeaderAndFooterLayout layout;
        private SelectionList selectionList;
        private Button doneButton;

        private SelectionScreen(Screen previous) {
            super(getDisplayedFieldName());
            this.previous = previous;
        }

        @Override
        protected void init() {
            layout = new HeaderAndFooterLayout(this);
            layout.addTitleHeader(title, minecraft.font);
            selectionList = layout.addToContents(new SelectionList());
            LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
            footer.addChild(Button.builder(Component.translatable("gui.cancel"), _ -> onClose()).build());
            doneButton = footer.addChild(Button.builder(Component.translatable("gui.done"), _ -> {
                if (selectionList.getSelected() != null) {
                    value = selectionList.getSelected().value;
                }
                onClose();
            }).build());
            layout.visitWidgets(this::addRenderableWidget);
            layout.arrangeElements();
        }

        @Override
        public void onClose() {
            Minecraft.getInstance().gui.setScreen(previous);
        }

        @Override
        public void extractRenderState(@NotNull GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
            doneButton.active = selectionList.getSelected() != null;
            super.extractRenderState(guiGraphicsExtractor, mouseX, mouseY, partialTick);
        }

        private final class SelectionList extends ObjectSelectionList<SelectionList.RegistryEntry> {
            private SelectionList() {
                super(SelectionScreen.this.minecraft, layout.getWidth(), layout.getContentHeight(), layout.getHeaderHeight(), 16);
                MainClass.registry(registry).keySet().stream()
                        .sorted(Comparator.comparing(key -> I18n.get(key.toLanguageKey(registryI18n))))
                        .map(RegistryEntry::new)
                        .forEachOrdered(this::addEntry);
                children().stream()
                        .filter(entry -> Objects.equals(entry.value, getValue()))
                        .findAny()
                        .ifPresent(this::setSelected);
            }

            private final class RegistryEntry extends ObjectSelectionList.Entry<RegistryEntry> {
                private final Identifier value;

                private RegistryEntry(Identifier value) {
                    this.value = value;
                }

                @Override
                public @NotNull Component getNarration() {
                    return Component.translatable(value.toLanguageKey(registryI18n));
                }

                @Override
                public void extractContent(GuiGraphicsExtractor guiGraphicsExtractor, int mouseX, int mouseY, boolean isHovering, float partialTick) {
                    guiGraphicsExtractor.text(minecraft.font, getNarration(), getContentX() + 5, getContentYMiddle() - minecraft.font.lineHeight / 2, -1);
                }

                @Override
                public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
                    if (event.buttonInfo().button() == InputConstants.MOUSE_BUTTON_LEFT && isFocused()) {
                        RegistrySingleSelect.this.value = value;
                        onClose();
                        return true;
                    }
                    return super.mouseClicked(event, isDoubleClick);
                }

                @Override
                public boolean keyPressed(KeyEvent event) {
                    if (event.isSelection()) {
                        RegistrySingleSelect.this.value = value;
                        onClose();
                        return true;
                    }
                    return super.keyPressed(event);
                }
            }
        }
    }
}
