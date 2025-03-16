package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.PageButton;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.StringUtils;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;
import org.mcaccess.minecraftaccess.utils.system.MouseUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(BookEditScreen.class)
public abstract class BookEditScreenMixin {
    @Shadow
    private int currentPage;
    @Final
    @Shadow
    private List<String> pages;
    @Shadow
    private PageButton forwardButton;
    @Shadow
    private PageButton backButton;
    @Shadow
    private boolean isSigning;
    @Shadow
    private Button cancelButton;
    @Shadow
    private Button finalizeButton;
    @Shadow
    private Button signButton;
    @Shadow
    private Button doneButton;
    @Shadow
    @Final
    private TextFieldHelper pageEdit;

    @Shadow
    protected abstract void keyHome();

    @Shadow
    protected abstract void keyEnd();

    @Shadow
    protected abstract void keyUp();

    @Shadow
    protected abstract void keyDown();

    @Shadow
    @Final
    private TextFieldHelper titleEdit;
    @Unique
    private static final Keystroke minecraft_access$tabKey = new Keystroke(() -> KeyUtils.isAnyPressed(GLFW.GLFW_KEY_TAB));
    @Unique
    private static final Keystroke minecraft_access$spaceKey = new Keystroke(KeyUtils::isSpacePressed);

    @Unique
    private int minecraft_access$currentFocusedButtonStateCode = 0;
    @Unique
    private static final int BUTTON_OFFSET = 3;

    @Inject(at = @At("HEAD"), method = "render")
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.screen == null) return;

        // Switch between buttons with Tab key
        // Only switch once until release and press Tab again
        if (minecraft_access$tabKey.isPressed()) {
            minecraft_access$switchMouseHoveredButton();
        }

        if (minecraft_access$spaceKey.isPressed()) {
            if (this.isSigning) {
                if (this.cancelButton.isHovered()) {
                    this.cancelButton.onPress();
                    return;
                }
                if (this.finalizeButton.isHovered()) {
                    this.finalizeButton.onPress();
                }
            } else {
                if (this.doneButton.isHovered()) {
                    this.doneButton.onPress();
                    return;
                }
                if (this.signButton.isHovered()) {
                    this.signButton.onPress();
                }
            }
        }
    }

    /**
     * Switch buttons focus with Tab key,
     * put the cursor on the focused button instead of invoking "setFocused()",
     * since somewhere we don't know will modify buttons focus states, which affects our logic.
     */
    @Unique
    private void minecraft_access$switchMouseHoveredButton() {
        if (this.isSigning) {
            // finalizeButton & cancelButton under the screen
            switch (minecraft_access$currentFocusedButtonStateCode) {
                case 0 -> minecraft_access$hoverMouseOnTo(this.cancelButton);
                case 1 -> minecraft_access$moveMouseAway();
                case 2 -> {
                    // the finalizeButton only be activated while title is not empty
                    if (this.finalizeButton.active) {
                        minecraft_access$hoverMouseOnTo(this.finalizeButton);
                    } else {
                        minecraft_access$currentFocusedButtonStateCode = 0;
                        minecraft_access$hoverMouseOnTo(this.cancelButton);
                    }
                }
            }
        } else {
            // signButton & doneButton under the screen
            switch (minecraft_access$currentFocusedButtonStateCode) {
                case 0 -> minecraft_access$hoverMouseOnTo(this.signButton);
                case 1 -> minecraft_access$hoverMouseOnTo(this.doneButton);
                case 2 -> minecraft_access$moveMouseAway();
            }
        }

        // loop between three status
        minecraft_access$currentFocusedButtonStateCode = (minecraft_access$currentFocusedButtonStateCode + 1) % 3;
    }

    @Unique
    private void minecraft_access$hoverMouseOnTo(Button button) {
        MouseUtils.performAt(((AbstractWidgetAccessor) button).callGetX() + BUTTON_OFFSET,
                ((AbstractWidgetAccessor) button).callGetY() + BUTTON_OFFSET,
                MouseUtils::move);
    }

    @Unique
    private void minecraft_access$moveMouseAway() {
        MouseUtils.performAt(((AbstractWidgetAccessor) this.signButton).callGetX() - 10,
                ((AbstractWidgetAccessor) this.signButton).callGetY() - 10,
                MouseUtils::move);
        MainClass.speakWithNarrator(I18n.get("minecraft_access.book_edit.focus_moved_away"), true);
    }

    /**
     * Rewrite the keyPressed method to reuse {@link TextFieldHelper} keypress handling to reuse logic in {@link TextFieldHelperMixin}.
     * They should have been written this method in this way, as well as in {@link AbstractSignEditScreen}.
     */
    @Inject(at = @At("HEAD"), method = "bookKeyPressed", cancellable = true)
    private void rewriteKeyPressedHandling(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();

        if (this.pageEdit.keyPressed(keyCode)) {
            cir.setReturnValue(true);
            return;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER: {
                this.pageEdit.insertText("\n");
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_UP: {
                this.keyUp();
                minecraft_access$speakCurrentLineContent();
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_DOWN: {
                this.keyDown();
                minecraft_access$speakCurrentLineContent();
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_PAGE_UP: {
                this.backButton.onPress();
                minecraft_access$speakCurrentPageContent();
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_PAGE_DOWN: {
                this.forwardButton.onPress();
                minecraft_access$speakCurrentPageContent();
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_HOME: {
                this.keyHome();
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_END: {
                this.keyEnd();
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(false);
    }

    @Inject(at = @At("RETURN"), method = "titleKeyPressed")
    private void speakWholeSigningTextWhileSigning(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        String signingText = ((TextFieldHelperAccessor) this.titleEdit).getGetMessageFn().get();
        MainClass.speakWithNarratorIfNotEmpty(signingText, true);
    }

    @Unique
    private void minecraft_access$speakCurrentLineContent() {
        String pageText = minecraft_access$getPageText();
        int cursor = this.pageEdit.getCursorPos();
        String lineText = StringUtils.getLineTextWhereTheCursorIsLocatedIn(pageText, cursor);
        MainClass.speakWithNarratorIfNotEmpty(lineText, true);
    }

    @Unique
    private String minecraft_access$getPageText() {
        return this.pages.get(this.currentPage).trim();
    }

    @Unique
    private void minecraft_access$speakCurrentPageContent() {
        String pageText = minecraft_access$getPageText();
        MutableComponent pageIndicatorText = Component.translatable("book.pageIndicator", this.currentPage + 1, this.pages.size());
        pageText = "%s\n\n%s".formatted(pageText, pageIndicatorText.getString());
        MainClass.speakWithNarratorIfNotEmpty(pageText, true);
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    private void speakPageContentWhileOpeningScreen(CallbackInfo ci) {
        minecraft_access$speakCurrentPageContent();
    }
}
