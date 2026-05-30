package org.mcaccess.minecraftaccess.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.util.Strings;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.mcaccess.minecraftaccess.MainClass;

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
    @Shadow
    private @Nullable String suggestion;

    protected EditBoxMixin(int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    @Shadow
    public abstract boolean canConsumeInput();

    @Shadow
    public abstract int getWordPosition(int wordOffset);

    @Shadow
    protected abstract int getCursorPos(int offset);

    @Shadow
    public abstract String getHighlighted();

    @Inject(method = "updateWidgetNarration", at = @At("TAIL"))
    private void narrateSuggestionWhenContentIsEmpty(NarrationElementOutput output, CallbackInfo ci) {
        if (value.isBlank() && suggestion != null) {
            output.add(NarratedElementType.HINT, suggestion);
        }
    }

    /*
     * Prevents any character input if alt is held down.
     * This logic is for "alt + num key to repeat chat message" function in {@link ChatScreenMixin}
     */
    @Inject(at = @At("HEAD"), method = "charTyped", cancellable = true)
    private void charTyped(CallbackInfoReturnable<Boolean> cir) {
        if (!Minecraft.getInstance().hasAltDown()) return;

        cir.setReturnValue(false);
        cir.cancel();
    }

    @Inject(at = @At("HEAD"), method = "keyPressed")
    private void narrateCursorHoverOverText(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
        if (!canConsumeInput()) {
            return;
        }
        // is selecting, let the selecting text narrating method do the job instead
        if (Minecraft.getInstance().hasShiftDown()) {
            return;
        }

        switch (event.key()) {
            case InputConstants.KEY_LEFT -> {
                if (Minecraft.getInstance().hasControlDown()) {
                    String hoveredText = getCursorHoverOverText(getWordPosition(-1));
                    MainClass.narrate(hoveredText, true);
                } else {
                    String hoveredText = getCursorHoverOverText(getCursorPos(-1));
                    MainClass.narrate(hoveredText, true);
                }
            }
            case InputConstants.KEY_RIGHT -> {
                if (Minecraft.getInstance().hasControlDown()) {
                    String hoveredText = getCursorHoverOverText(getWordPosition(1));
                    MainClass.narrate(hoveredText, true);
                } else {
                    String hoveredText = getCursorHoverOverText(getCursorPos(1));
                    MainClass.narrate(hoveredText, true);
                }
            }
            case InputConstants.KEY_HOME -> {
                if (Strings.isNotEmpty(value)) {
                    MainClass.narrate(value.substring(0, 1), true);
                }
            }
            case InputConstants.KEY_END -> {
                if (Strings.isNotEmpty(value)) {
                    MainClass.narrate(value.substring(value.length() - 1), true);
                }
            }
        }
    }

    @Inject(at = @At("RETURN"), method = "keyPressed")
    private void narrateSelectedText(CallbackInfoReturnable<Boolean> cir) {
        if (!canConsumeInput()) {
            return;
        }
        String selectedText = getHighlighted();
        if (!selectedText.isBlank()) {
            MainClass.narrate(selectedText, true);
        }
    }

    @Inject(at = @At("HEAD"), method = "deleteChars")
    private void narrateErasedText(int dir, CallbackInfo ci) {
        int cursorPos = getCursorPos(dir);
        // select all text (ctrl+a) will not change the cursor position,
        // if we delete all text then, the erasedText will be a wrong value (one char ahead of cursor)
        // don't narrate under this condition
        boolean allTextAreSelected = highlightPos == 0;
        if (!allTextAreSelected) {
            String erasedText = getCursorHoverOverText(cursorPos);
            MainClass.narrate(erasedText, true);
        }
    }

    @Unique
    private String getCursorHoverOverText(int changedCursorPos) {
        int currentCursorPos = cursorPos;
        int startPos = Math.min(changedCursorPos, currentCursorPos);
        int endPos = Math.max(changedCursorPos, currentCursorPos);
        return startPos == endPos ? "" : value.substring(startPos, endPos);
    }
}
