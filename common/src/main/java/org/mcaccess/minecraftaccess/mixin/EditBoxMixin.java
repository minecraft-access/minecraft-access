package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.util.Strings;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.MainClass;
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
@Mixin(EditBox.class)
public abstract class EditBoxMixin {
    @Shadow
    private String value;
    @Shadow
    private int cursorPos;
    @Shadow
    private int highlightPos;

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Shadow
    public abstract boolean canConsumeInput();

    @Shadow
    public abstract int getWordPosition(int wordOffset);

    @Shadow
    protected abstract int getCursorPos(int offset);

    @Shadow
    public abstract String getHighlighted();

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
        if (!this.canConsumeInput()) {
            return;
        }
        // is selecting, let the selecting text speaking method do the job instead
        if (Screen.hasShiftDown()) {
            return;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT: {
                if (Screen.hasControlDown()) {
                    String hoveredText = this.mca$getCursorHoverOverText(this.getWordPosition(-1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                } else {
                    String hoveredText = this.mca$getCursorHoverOverText(this.getCursorPos(-1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                }
                return;
            }
            case GLFW.GLFW_KEY_RIGHT: {
                if (Screen.hasControlDown()) {
                    String hoveredText = this.mca$getCursorHoverOverText(this.getWordPosition(1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                } else {
                    String hoveredText = this.mca$getCursorHoverOverText(this.getCursorPos(1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                }
                return;
            }
            case GLFW.GLFW_KEY_HOME: {
                if (Strings.isNotEmpty(this.value)) {
                    MainClass.speakWithNarrator(this.value.substring(0, 1), true);
                }
                return;
            }
            case GLFW.GLFW_KEY_END: {
                if (Strings.isNotEmpty(this.value)) {
                    MainClass.speakWithNarrator(this.value.substring(this.value.length() - 1), true);
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "keyPressed")
    private void speakSelectedText(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!this.canConsumeInput()) {
            return;
        }
        String selectedText = this.getHighlighted();
        MainClass.speakWithNarratorIfNotEmpty(selectedText, true);
    }

    @Inject(at = @At("HEAD"), method = "deleteChars")
    private void speakErasedText(int characterOffset, CallbackInfo ci) {
        int cursorPos = this.getCursorPos(characterOffset);
        // select all text (ctrl+a) will not change the cursor position,
        // if we delete all text then, the erasedText will be a wrong value (one char ahead of cursor)
        // don't speak under this condition
        boolean allTextAreSelected = this.highlightPos == 0;
        if (!allTextAreSelected) {
            String erasedText = mca$getCursorHoverOverText(cursorPos);
            MainClass.speakWithNarratorIfNotEmpty(erasedText, true);
        }
    }

    @Unique
    private String mca$getCursorHoverOverText(int changedCursorPos) {
        int currentCursorPos = this.cursorPos;
        int startPos = Math.min(changedCursorPos, currentCursorPos);
        int endPos = Math.max(changedCursorPos, currentCursorPos);
        return startPos == endPos ? "" : this.value.substring(startPos, endPos);
    }
}
