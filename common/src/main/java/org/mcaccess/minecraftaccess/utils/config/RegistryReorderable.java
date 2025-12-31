package org.mcaccess.minecraftaccess.utils.config;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;

class RegistryReorderable extends BaseEntry<Identifier[], Button> {
    private final Class<?> registry;
    private final String registryI18n;

    protected RegistryReorderable(Class<?> registry, String registryI18n, String i18n, Field field, Object config, Object defaults) {
        super(i18n, field, config, defaults);
        this.registry = registry;
        this.registryI18n = registryI18n;
    }

    @Override
    protected Button initWidget() {
        return Button.builder(Component.empty(), b -> {
            Minecraft.getInstance().setScreen(new SelectionScreen(Minecraft.getInstance().screen));
        }).build();
    }

    @Override
    protected void updateWidget(Button widget) {
        widget.setMessage(Component.translatable("minecraft_access.other.selected", getValue().length));
    }

    private final class SelectionScreen extends Screen {
        private final Screen previous;
        private HeaderAndFooterLayout layout;
        private SelectionList availableList;
        private SelectionList selectedList;
        private final List<Identifier> selection;

        private SelectionScreen(Screen previous) {
            super(getDisplayedFieldName());
            this.previous = previous;
            selection = new ArrayList<>(Arrays.asList(value));
        }

        @Override
        protected void init() {
            assert minecraft != null;
            layout = new HeaderAndFooterLayout(this);
            layout.addTitleHeader(title, minecraft.font);
            availableList = addRenderableWidget(new SelectionList(false));
            selectedList = addRenderableWidget(new SelectionList(true));
            LinearLayout footer = layout.addToFooter(LinearLayout.horizontal().spacing(8));
            footer.addChild(Button.builder(Component.translatable("gui.cancel"), b -> onClose()).build());
            footer.addChild(Button.builder(Component.translatable("gui.done"), b -> {
                value = selection.toArray(Identifier[]::new);
                onClose();
            }).build());
            layout.visitWidgets(this::addRenderableWidget);
            repositionElements();
        }

        @Override
        protected void repositionElements() {
            layout.arrangeElements();
            availableList.updateSizeAndPosition(200, layout.getContentHeight(), width / 2 - 215, layout.getHeaderHeight());
            selectedList.updateSizeAndPosition(200, layout.getContentHeight(), width / 2 + 15, layout.getHeaderHeight());
        }

        @Override
        public void onClose() {
            Minecraft.getInstance().screen = previous;
        }

        @Override
        public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
            super.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        private final class SelectionList extends ObjectSelectionList<SelectionList.BaseEntry> {
            private final boolean isSelectedList;

            private SelectionList(boolean isSelectedList) {
                super(SelectionScreen.this.minecraft, 200, layout.getContentHeight(), layout.getHeaderHeight(), 36);
                this.isSelectedList = isSelectedList;
                update();
            }

            private void update() {
                Identifier focussed = getFocused() instanceof RegistryEntry registryEntry ? registryEntry.value : null;
                clearEntries();
                addEntry(new HeadingEntry(
                                Component.translatable(isSelectedList ? "pack.selected.title" : "pack.available.title")
                                        .withStyle(ChatFormatting.BOLD, ChatFormatting.UNDERLINE)
                        ),
                        16
                );
                if (isSelectedList) {
                    selection.stream()
                            .map(RegistryEntry::new)
                            .forEachOrdered(this::addEntry);
                } else {
                    MainClass.registry(registry).keySet().stream()
                            .filter(key -> !selection.contains(key))
                            .sorted(Comparator.comparing(key -> I18n.get(key.toLanguageKey(registryI18n))))
                            .map(RegistryEntry::new)
                            .forEachOrdered(this::addEntry);
                }
                if (focussed != null) {
                    children().stream()
                            .filter(entry -> entry instanceof RegistryEntry registryEntry && Objects.equals(registryEntry.value, focussed))
                            .findAny()
                            .ifPresent(this::setFocused);
                }
            }

            @Override
            protected int scrollBarX() {
                return getRight() - 6;
            }

            @Override
            public int getRowWidth() {
                return width - 4;
            }

            private abstract class BaseEntry extends ObjectSelectionList.Entry<BaseEntry> {
                @Override
                public int getWidth() {
                    return super.getWidth() - (scrollbarVisible() ? 6 : 0);
                }
            }

            private final class HeadingEntry extends BaseEntry {
                private final Component text;

                private HeadingEntry(Component text) {
                    this.text = text;
                }

                @Override
                public @NotNull Component getNarration() {
                    return text;
                }

                @Override
                public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
                    guiGraphics.drawCenteredString(minecraft.font, text, getX() + getWidth() / 2, getContentYMiddle() - minecraft.font.lineHeight / 2, -1);
                }
            }

            private final class RegistryEntry extends BaseEntry {
                private final Identifier value;

                private RegistryEntry(Identifier value) {
                    this.value = value;
                }

                private void enable() {
                    selection.add(value);
                    availableList.update();
                    selectedList.update();
                }

                private void disable() {
                    selection.remove(value);
                    availableList.update();
                    selectedList.update();
                }

                private void moveUp() {
                    int index = selection.indexOf(value);
                    selection.remove(value);
                    selection.add(index - 1, value);
                    selectedList.update();
                }

                private void moveDown() {
                    int index = selection.indexOf(value);
                    selection.remove(value);
                    selection.add(index + 1, value);
                    selectedList.update();
                }

                @Override
                public @NotNull Component getNarration() {
                    return Component.translatable(value.toLanguageKey(registryI18n));
                }

                @Override
                public void renderContent(GuiGraphics guiGraphics, int mouseX, int mouseY, boolean isHovering, float partialTick) {
                    guiGraphics.drawString(minecraft.font, getNarration(), getContentX() + 34, getContentYMiddle() - minecraft.font.lineHeight / 2, -1);

                    int relativeX = mouseX - getContentX();
                    int relativeY = mouseY - getContentY();
                    if (isSelectedList) {
                        guiGraphics.blitSprite(
                                RenderPipelines.GUI_TEXTURED,
                                isHovering && relativeX < 16
                                        ? Identifier.withDefaultNamespace("transferable_list/unselect_highlighted")
                                        : Identifier.withDefaultNamespace("transferable_list/unselect"),
                                getContentX(), getContentY(),
                                32, 32
                        );
                        if (!Objects.equals(selection.getFirst(), value)) {
                            guiGraphics.blitSprite(
                                    RenderPipelines.GUI_TEXTURED,
                                    isHovering && relativeX > 16 && relativeX < 32 && relativeY < 16
                                            ? Identifier.withDefaultNamespace("transferable_list/move_up_highlighted")
                                            : Identifier.withDefaultNamespace("transferable_list/move_up"),
                                    getContentX(), getContentY(),
                                    32, 32
                            );
                        }
                        if (!Objects.equals(selection.getLast(), value)) {
                            guiGraphics.blitSprite(
                                    RenderPipelines.GUI_TEXTURED,
                                    isHovering && relativeX > 16 && relativeX < 32 && relativeY > 16
                                            ? Identifier.withDefaultNamespace("transferable_list/move_down_highlighted")
                                            : Identifier.withDefaultNamespace("transferable_list/move_down"),
                                    getContentX(), getContentY(),
                                    32, 32
                            );
                        }
                    } else {
                        guiGraphics.blitSprite(
                                RenderPipelines.GUI_TEXTURED,
                                isHovering && relativeX < 32
                                        ? Identifier.withDefaultNamespace("transferable_list/select_highlighted")
                                        : Identifier.withDefaultNamespace("transferable_list/select"),
                                getContentX(), getContentY(),
                                32, 32
                        );
                    }
                }

                @Override
                public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
                    double relativeX = event.x() - getContentX();
                    double relativeY = event.y() - getContentY();
                    if (isSelectedList) {
                        if (relativeX < 16) {
                            disable();
                            return true;
                        }
                        if (relativeX < 32 && relativeX > 16 && relativeY < 16 && !Objects.equals(selection.getFirst(), value)) {
                            moveUp();
                            return true;
                        }
                        if (relativeX < 32 && relativeX > 16 && relativeY > 16 && !Objects.equals(selection.getLast(), value)) {
                            moveDown();
                            return true;
                        }
                    } else if (relativeX < 32) {
                        enable();
                        return true;
                    }
                    return super.mouseClicked(event, isDoubleClick);
                }

                @Override
                public boolean keyPressed(KeyEvent event) {
                    if (isSelectedList) {
                        if (event.isConfirmation()) {
                            disable();
                            return true;
                        }
                        if (event.hasShiftDown()) {
                            if (event.isUp() && !Objects.equals(selection.getFirst(), value)) {
                                moveUp();
                                return true;
                            }
                            if (event.isDown() && !Objects.equals(selection.getLast(), value)) {
                                moveDown();
                                return true;
                            }
                        }
                    } else if (event.isConfirmation()) {
                        enable();
                        return true;
                    }
                    return super.keyPressed(event);
                }
            }
        }
    }
}
