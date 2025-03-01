package org.mcaccess.minecraftaccess.utils;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;

public class BaseScreen extends Screen {
    int centerX;
    int buttonHeight;
    int marginY;
    int calculatedYPosition;
    int calculatedXPosition;
    int leftColumnX;
    int rightColumnX;
    boolean shouldRenderInLeftColumn;
    BaseScreen previousScreen;

    public BaseScreen(String title) {
        super(Component.nullToEmpty(I18n.get("minecraft_access.gui.screen." + title)));
        previousScreen = null;
    }

    public BaseScreen(String title, BaseScreen previousScreen) {
        super(Component.nullToEmpty(I18n.get("minecraft_access.gui.screen." + title)));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        this.centerX = this.width / 2;
        this.buttonHeight = 20;
        this.marginY = buttonHeight + buttonHeight / 4;
        this.calculatedYPosition = this.height / 6 - marginY; // Starting Y position (marginY will again be added in buildButtonWidget() so it is subtracted here to equate)
        this.leftColumnX = 10;
        this.rightColumnX = centerX + 10;
        shouldRenderInLeftColumn = true;
    }

    protected Button buildButtonWidget(String translationKeyOrText, Button.OnPress pressAction) {
        return buildButtonWidget(translationKeyOrText, pressAction, false);
    }

    protected Button buildButtonWidget(String translationKeyOrText, Button.OnPress pressAction, boolean shouldRenderInSeparateRow) {
        String buttonText = I18n.exists(translationKeyOrText) ? I18n.get((translationKeyOrText)) : translationKeyOrText;
        int calculatedButtonWidth = this.font.width(buttonText) + 35;
        if (shouldRenderInSeparateRow) {
            calculatedXPosition = centerX - calculatedButtonWidth / 2;
            calculatedYPosition += marginY;
            shouldRenderInLeftColumn = true;
        } else {
            calculatedXPosition = (shouldRenderInLeftColumn) ? leftColumnX : rightColumnX;
            if (shouldRenderInLeftColumn) calculatedYPosition += marginY;
            shouldRenderInLeftColumn = !shouldRenderInLeftColumn;
        }

        return Button.builder(Component.nullToEmpty(buttonText), pressAction)
                .bounds(calculatedXPosition, calculatedYPosition, calculatedButtonWidth, buttonHeight)
                .build();
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        context.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
        super.render(context, mouseX, mouseY, delta);
    }
}
