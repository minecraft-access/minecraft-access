package org.mcaccess.minecraftaccess.utils.config;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import org.jetbrains.annotations.NotNull;

abstract class BaseEntry<T, W extends AbstractWidget> extends AbstractConfigListEntry<T> {
    private final Field field;
    private final Object config;
    protected W widget;
    private final T defaultValue;
    protected T value;
    private final StringWidget label;
    private final Button resetButton;

    protected BaseEntry(String i18n, Field field, Object config, Object defaults) {
        super(Component.translatable(i18n), false);
        saveCallback = value -> ConfigExtension.setField(config, field, value);
        this.field = field;
        this.config = config;
        widget = initWidget();
        defaultValue = ConfigExtension.getField(defaults, field);
        value = ConfigExtension.getField(config, field);
        label = new StringWidget(getDisplayedFieldName(), Minecraft.getInstance().font);
        resetButton = Button.builder(Component.translatable("text.cloth-config.reset_value"), b -> value = defaultValue)
                .width(Minecraft.getInstance().font.width(Component.translatable("text.cloth-config.reset_value")) + 6)
                .build();
    }

    protected abstract W initWidget();

    protected abstract void updateWidget(W widget);

    @Override
    public Optional<T> getDefaultValue() {
        return Optional.of(defaultValue);
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public boolean isEdited() {
        return super.isEdited() || !Objects.deepEquals(getValue(), ConfigExtension.getField(config, field));
    }

    @Override
    public void render(GuiGraphicsExtractor graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        resetButton.active = isEditable() && !Objects.deepEquals(getValue(), defaultValue);
        resetButton.setY(y);
        widget.active = isEditable();
        widget.setY(y);
        widget.setWidth(150 - resetButton.getWidth() - 2);
        updateWidget(widget);
        label.setY(y + 6);
        if (!Objects.equals(label.getMessage().getStyle().getColor(), TextColor.fromRgb(getPreferredTextColor()))) {
            label.setMessage(label.getMessage().copy().withColor(getPreferredTextColor()));
        }
        if (Minecraft.getInstance().font.isBidirectional()) {
            label.setX(Minecraft.getInstance().getWindow().getGuiScaledWidth() - x - label.getWidth());
            resetButton.setX(x);
            widget.setX(x + resetButton.getWidth() + 2);
        } else {
            label.setX(x);
            resetButton.setX(x + entryWidth - resetButton.getWidth());
            widget.setX(x + entryWidth - 150);
        }
        label.render(graphics, mouseX, mouseY, delta);
        widget.render(graphics, mouseX, mouseY, delta);
        resetButton.render(graphics, mouseX, mouseY, delta);
    }

    @Override
    public List<? extends NarratableEntry> narratables() {
        return List.of(label, widget, resetButton);
    }

    @Override
    public @NotNull List<? extends GuiEventListener> children() {
        return List.of(label, widget, resetButton);
    }
}
