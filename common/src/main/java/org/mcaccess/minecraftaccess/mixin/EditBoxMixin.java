package org.mcaccess.minecraftaccess.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;
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
 * to simulate screen reader's text narrating behavior when editing text in input fields.
 */
@Mixin(EditBox.class)
abstract class EditBoxMixin extends AbstractWidget {
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

    @Shadow
    @Nullable
    private String suggestion;

    public EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @Inject(method = "updateWidgetNarration", at = @At("TAIL"))
    private void narrateSuggestionWhenContentIsEmpty(NarrationElementOutput output, CallbackInfo ci) {
        if (this.value.isBlank() && this.suggestion != null) {
            output.add(NarratedElementType.HINT, this.suggestion);
        }
    }

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
    private void narrateCursorHoverOverText(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!this.canConsumeInput()) {
            return;
        }
        // is selecting, let the selecting text narrating method do the job instead
        if (Screen.hasShiftDown()) {
            return;
        }

        switch (keyCode) {
            case InputConstants.KEY_LEFT: {
                if (Screen.hasControlDown()) {
                    String hoveredText = this.mca$getCursorHoverOverText(this.getWordPosition(-1));
                    MainClass.narrate(hoveredText, true);
                } else {
                    String hoveredText = this.mca$getCursorHoverOverText(this.getCursorPos(-1));
                    MainClass.narrate(hoveredText, true);
                }
                return;
            }
            case InputConstants.KEY_RIGHT: {
                if (Screen.hasControlDown()) {
                    String hoveredText = this.mca$getCursorHoverOverText(this.getWordPosition(1));
                    MainClass.narrate(hoveredText, true);
                } else {
                    String hoveredText = this.mca$getCursorHoverOverText(this.getCursorPos(1));
                    MainClass.narrate(hoveredText, true);
                }
                return;
            }
            case InputConstants.KEY_HOME: {
                if (Strings.isNotEmpty(this.value)) {
                    MainClass.narrate(this.value.substring(0, 1), true);
                }
                return;
            }
            case InputConstants.KEY_END: {
                if (Strings.isNotEmpty(this.value)) {
                    MainClass.narrate(this.value.substring(this.value.length() - 1), true);
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "keyPressed")
    private void narrateSelectedText(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!this.canConsumeInput()) {
            return;
        }
        String selectedText = this.getHighlighted();
        if (!selectedText.isBlank()) {
            MainClass.narrate(selectedText, true);
        }
    }

    @Inject(at = @At("HEAD"), method = "deleteChars")
    private void narrateErasedText(int characterOffset, CallbackInfo ci) {
        int cursorPos = this.getCursorPos(characterOffset);
        // select all text (ctrl+a) will not change the cursor position,
        // if we delete all text then, the erasedText will be a wrong value (one char ahead of cursor)
        // don't narrate under this condition
        boolean allTextAreSelected = this.highlightPos == 0;
        if (!allTextAreSelected) {
            String erasedText = mca$getCursorHoverOverText(cursorPos);
            MainClass.narrate(erasedText, true);
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
