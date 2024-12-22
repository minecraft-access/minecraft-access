package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.utils.StringUtils;
import com.github.khanshoaib3.minecraft_access.utils.condition.Keystroke;
import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;
import com.github.khanshoaib3.minecraft_access.utils.system.MouseUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.PageTurnWidget;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
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
    private PageTurnWidget nextPageButton;
    @Shadow
    private PageTurnWidget previousPageButton;
    @Shadow
    private boolean signing;
    @Shadow
    private ButtonWidget cancelButton;
    @Shadow
    private ButtonWidget finalizeButton;
    @Shadow
    private ButtonWidget signButton;
    @Shadow
    private ButtonWidget doneButton;
    @Shadow
    @Final
    private SelectionManager currentPageSelectionManager;

    @Shadow
    protected abstract void moveToLineStart();

    @Shadow
    protected abstract void moveToLineEnd();

    @Shadow
    protected abstract void moveUpLine();

    @Shadow
    protected abstract void moveDownLine();

    @Shadow
    @Final
    private SelectionManager bookTitleSelectionManager;
    @Unique
    private static final Keystroke minecraft_access$tabKey = new Keystroke(() -> KeyUtils.isAnyPressed(GLFW.GLFW_KEY_TAB));
    @Unique
    private static final Keystroke minecraft_access$spaceKey = new Keystroke(KeyUtils::isSpacePressed);

    @Unique
    private int minecraft_access$currentFocusedButtonStateCode = 0;
    @Unique
    private static final int BUTTON_OFFSET = 3;

    @Inject(at = @At("HEAD"), method = "render")
    public void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.currentScreen == null) return;

        // Switch between buttons with Tab key
        // Only switch once until release and press Tab again
        if (minecraft_access$tabKey.isPressed()) {
            minecraft_access$switchMouseHoveredButton();
        }
        // update the state
        minecraft_access$tabKey.updateStateForNextTick();

        if (minecraft_access$spaceKey.isPressed()) {
            minecraft_access$spaceKey.updateStateForNextTick();
            if (this.signing) {
                if (this.cancelButton.isHovered()) {
                    this.cancelButton.onPress();
                    return;
                }
                if (this.finalizeButton.isHovered()) {
                    this.finalizeButton.onPress();
                    return;
                }
            } else {
                if (this.doneButton.isHovered()) {
                    this.doneButton.onPress();
                    return;
                }
                if (this.signButton.isHovered()) {
                    this.signButton.onPress();
                    return;
                }
            }
        }
        // update the state
        minecraft_access$spaceKey.updateStateForNextTick();
    }

    /**
     * Switch buttons focus with Tab key,
     * put the cursor on the focused button instead of invoking "setFocused()",
     * since somewhere we don't know will modify buttons focus states, which affects our logic.
     */
    @Unique
    private void minecraft_access$switchMouseHoveredButton() {
        if (this.signing) {
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
    private void minecraft_access$hoverMouseOnTo(ButtonWidget button) {
        MouseUtils.performAt(((ClickableWidgetAccessor) button).callGetX() + BUTTON_OFFSET,
                ((ClickableWidgetAccessor) button).callGetY() + BUTTON_OFFSET,
                MouseUtils::move);
    }

    @Unique
    private void minecraft_access$moveMouseAway() {
        MouseUtils.performAt(((ClickableWidgetAccessor) this.signButton).callGetX() - 10,
                ((ClickableWidgetAccessor) this.signButton).callGetY() - 10,
                MouseUtils::move);
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.book_edit.focus_moved_away"), true);
    }

    /**
     * Rewrite the keyPressed method to reuse {@link SelectionManager} keypress handling to reuse logic in {@link SelectionManagerMixin}.
     * They should have been written this method in this way, as well as in {@link AbstractSignEditScreen}.
     */
    @Inject(at = @At("HEAD"), method = "keyPressedEditMode", cancellable = true)
    private void rewriteKeyPressedHandling(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();

        if (this.currentPageSelectionManager.handleSpecialKey(keyCode)) {
            cir.setReturnValue(true);
            return;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_ENTER:
            case GLFW.GLFW_KEY_KP_ENTER: {
                this.currentPageSelectionManager.insert("\n");
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_UP: {
                this.moveUpLine();
                speakCurrentLineContent();
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_DOWN: {
                this.moveDownLine();
                speakCurrentLineContent();
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_PAGE_UP: {
                this.previousPageButton.onPress();
                speakCurrentPageContent();
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_PAGE_DOWN: {
                this.nextPageButton.onPress();
                speakCurrentPageContent();
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_HOME: {
                this.moveToLineStart();
                cir.setReturnValue(true);
                return;
            }
            case GLFW.GLFW_KEY_END: {
                this.moveToLineEnd();
                cir.setReturnValue(true);
                return;
            }
        }
        cir.setReturnValue(false);
    }

    @Inject(at = @At("RETURN"), method = "keyPressedSignMode")
    private void speakWholeSigningTextWhileSigning(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        String signingText = ((SelectionManagerAccessor) this.bookTitleSelectionManager).getStringGetter().get();
        MainClass.speakWithNarratorIfNotEmpty(signingText, true);
    }

    @Unique
    private void speakCurrentLineContent() {
        String pageText = getPageText();
        int cursor = this.currentPageSelectionManager.getSelectionStart();
        String lineText = StringUtils.getLineTextWhereTheCursorIsLocatedIn(pageText, cursor);
        MainClass.speakWithNarratorIfNotEmpty(lineText, true);
    }

    @Unique
    private String getPageText() {
        return this.pages.get(this.currentPage).trim();
    }

    @Unique
    private void speakCurrentPageContent() {
        String pageText = getPageText();
        MutableText pageIndicatorText = Text.translatable("book.pageIndicator", this.currentPage + 1, this.pages.size());
        pageText = "%s\n\n%s".formatted(pageText, pageIndicatorText.getString());
        MainClass.speakWithNarratorIfNotEmpty(pageText, true);
    }

    @Inject(at = @At("RETURN"), method = "<init>")
    private void speakPageContentWhileOpeningScreen(CallbackInfo ci) {
        speakCurrentPageContent();
    }
}
