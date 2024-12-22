package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.MainClass;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.apache.logging.log4j.util.Strings;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mainly add custom keypress handling
 * to simulate screen reader's text speaking behavior when editing text in input fields.
 */
@Mixin(TextFieldWidget.class)
public abstract class TextFieldWidgetMixin {
    @Shadow
    private String text;
    @Shadow
    private int selectionStart;
    @Shadow
    private int selectionEnd;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Shadow
    public abstract boolean isActive();

    @Shadow
    public abstract int getWordSkipPosition(int wordOffset);

    @Shadow
    protected abstract int getCursorPosWithOffset(int offset);

    @Shadow
    public abstract String getSelectedText();

    /**
     * Prevents any character input if alt is held down.
     * This logic is for "alt + num key to repeat chat message" function in {@link ChatScreenMixin}
     */
    @Inject(at = @At("HEAD"), method = "charTyped", cancellable = true)
    private void charTyped(char chr, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!Screen.hasAltDown()) return;

        cir.setReturnValue(false);
        cir.cancel();
    }

    @Inject(at = @At("HEAD"), method = "keyPressed")
    private void speakCursorHoverOverText(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!this.isActive()) {
            return;
        }
        // is selecting, let the selecting text speaking method do the job instead
        if (Screen.hasShiftDown()) {
            return;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT: {
                if (Screen.hasControlDown()) {
                    String hoveredText = this.getCursorHoverOverText(this.getWordSkipPosition(-1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                } else {
                    String hoveredText = this.getCursorHoverOverText(this.getCursorPosWithOffset(-1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                }
                return;
            }
            case GLFW.GLFW_KEY_RIGHT: {
                if (Screen.hasControlDown()) {
                    String hoveredText = this.getCursorHoverOverText(this.getWordSkipPosition(1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                } else {
                    String hoveredText = this.getCursorHoverOverText(this.getCursorPosWithOffset(1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                }
                return;
            }
            case GLFW.GLFW_KEY_HOME: {
                if (Strings.isNotEmpty(this.text)) {
                    MainClass.speakWithNarrator(this.text.substring(0, 1), true);
                }
                return;
            }
            case GLFW.GLFW_KEY_END: {
                if (Strings.isNotEmpty(this.text)) {
                    MainClass.speakWithNarrator(this.text.substring(this.text.length() - 1), true);
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "keyPressed")
    private void speakSelectedText(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!this.isActive()) {
            return;
        }
        String selectedText = this.getSelectedText();
        MainClass.speakWithNarratorIfNotEmpty(selectedText, true);
    }

    @Inject(at = @At("HEAD"), method = "eraseCharacters")
    private void speakErasedText(int characterOffset, CallbackInfo ci) {
        int cursorPos = this.getCursorPosWithOffset(characterOffset);
        // select all text (ctrl+a) will not change the cursor position,
        // if we delete all text then, the erasedText will be a wrong value (one char ahead of cursor)
        // don't speak under this condition
        boolean allTextAreSelected = this.selectionEnd == 0;
        if (!allTextAreSelected) {
            String erasedText = getCursorHoverOverText(cursorPos);
            MainClass.speakWithNarratorIfNotEmpty(erasedText, true);
        }
    }

    @Unique
    private String getCursorHoverOverText(int changedCursorPos) {
        int currentCursorPos = this.selectionStart;
        int startPos = Math.min(changedCursorPos, currentCursorPos);
        int endPos = Math.max(changedCursorPos, currentCursorPos);
        return startPos == endPos ? "" : this.text.substring(startPos, endPos);
    }
}
