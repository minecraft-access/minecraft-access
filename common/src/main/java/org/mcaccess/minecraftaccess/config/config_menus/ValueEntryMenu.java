package com.github.khanshoaib3.minecraft_access.config.config_menus;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.Config;
import com.github.khanshoaib3.minecraft_access.utils.BaseScreen;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.resource.language.I18n;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Supplier;

@Slf4j
public class ValueEntryMenu extends BaseScreen {
    public static class ValueConfig {
        private final Supplier<Object> valueGetter;
        private final Consumer<String> valueSetter;
        private final ValueType valueType;

        public ValueConfig(Supplier<Object> valueGetter, Consumer<String> valueSetter, ValueType valueType) {
            this.valueGetter = valueGetter;
            this.valueSetter = valueSetter;
            this.valueType = valueType;
        }

        public String buildButtonText(String fieldI18NKey) {
            return I18n.translate("minecraft_access.gui.common.button.button_with_string_value", I18n.translate(fieldI18NKey), this.valueGetter.get());
        }
    }

    public enum ValueType {
        INT,
        FLOAT,
        STRING
    }

    String value;
    ValueConfig configType;
    String previousValue;

    protected ValueEntryMenu(ValueConfig configType, BaseScreen previousScreen) {
        super("value_entry_menu", previousScreen);
        this.configType = configType;
    }

    @Override
    protected void init() {
        super.init();
        this.value = String.valueOf(this.configType.valueGetter.get());
        this.previousValue = this.value;
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.gui.value_entry_menu.info_text", this.value), true);
    }

    /**
     * Removes the default title narration on menu open.
     */
    @Override
    protected void addScreenNarrations(NarrationMessageBuilder builder) {
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
        } else if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.updateConfig();
            this.close();
        } else if (this.configType.valueType != ValueType.STRING && ((keyCode >= GLFW.GLFW_KEY_0 && keyCode <= GLFW.GLFW_KEY_9)
                || (keyCode >= GLFW.GLFW_KEY_KP_0 && keyCode <= GLFW.GLFW_KEY_KP_9)
                || (this.configType.valueType != ValueType.INT && (keyCode == GLFW.GLFW_KEY_PERIOD || keyCode == GLFW.GLFW_KEY_KP_DECIMAL)))) {
            this.value += GLFW.glfwGetKeyName(keyCode, scanCode);
        } else if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !this.value.isEmpty()) {
            this.value = this.value.substring(0, this.value.length() - 1);
        } else if (this.configType.valueType == ValueType.STRING && GLFW.glfwGetKeyName(keyCode, scanCode) != null) {
            this.value += GLFW.glfwGetKeyName(keyCode, scanCode);
        } else {
            return false;
        }

        return true;
    }

    private void updateConfig() {
        try {
            this.configType.valueSetter.accept(this.value);
            Config.getInstance().writeJSON();

        } catch (Exception e) {
            log.error("Error occurred while updating the config. The user possibly entered wrong value type.", e);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.speakValue();
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 15, 16777215);
        context.drawCenteredTextWithShadow(this.textRenderer, value, this.width / 2, this.height / 2, 16777215);
        super.render(context, mouseX, mouseY, delta);
    }

    /* Pre 1.20.x
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.speakValue();
        this.renderBackground(matrices);
        DrawableHelper.drawCenteredText(matrices, this.textRenderer, this.title, this.width / 2, 15, 16777215);
        DrawableHelper.drawCenteredText(matrices, this.textRenderer, value, this.width / 2, this.height / 2, 16777215);
        super.render(matrices, mouseX, mouseY, delta);
    }
    */

    private void speakValue() {
        if (this.previousValue.equals(this.value)) return;
        this.previousValue = this.value;
        MainClass.speakWithNarrator(this.value, true);
    }
}
