package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.MainClass;
import net.minecraft.client.font.TextHandler;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.AbstractSignEditScreen;
import net.minecraft.client.gui.screen.ingame.BookEditScreen;
import net.minecraft.client.util.SelectionManager;
import net.minecraft.util.Util;
import org.apache.logging.log4j.util.Strings;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Supplier;

/**
 * Mixin the inner content manager of {@link AbstractSignEditScreen}, {@link BookEditScreen} to make text editing on these screens accessible.
 */
@Mixin(SelectionManager.class)
public abstract class SelectionManagerMixin {
    @Final
    @Shadow
    private Supplier<String> stringGetter;
    @Shadow
    private int selectionStart;
    @Shadow
    private int selectionEnd;

    @Shadow
    protected abstract String getSelectedText(String string);

    @Inject(at = @At("TAIL"), method = "putCursorAtEnd")
    public void speakTextOfSwitchedLine(CallbackInfo ci) {
        MainClass.speakWithNarratorIfNotEmpty(this.stringGetter.get(), true);
    }

    @Inject(at = @At("HEAD"), method = "handleSpecialKey")
    private void speakCursorHoverOverText(int keyCode, CallbackInfoReturnable<Boolean> cir) {
        // is selecting, let the selecting text speaking method do the job instead
        if (Screen.hasShiftDown()) {
            return;
        }

        switch (keyCode) {
            case GLFW.GLFW_KEY_LEFT: {
                if (Screen.hasControlDown()) {
                    String hoveredText = this.getCursorHoveredOverText(getCursorPosByWordsWithOffset(-1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                } else {
                    String hoveredText = this.getCursorHoveredOverText(getCursorPosWithOffset(-1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                }
                return;
            }
            case GLFW.GLFW_KEY_RIGHT: {
                if (Screen.hasControlDown()) {
                    String hoveredText = this.getCursorHoveredOverText(getCursorPosByWordsWithOffset(1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                } else {
                    String hoveredText = this.getCursorHoveredOverText(getCursorPosWithOffset(1));
                    MainClass.speakWithNarratorIfNotEmpty(hoveredText, true);
                }
                return;
            }
            case GLFW.GLFW_KEY_HOME: {
                String text = this.stringGetter.get();
                if (Strings.isNotEmpty(text)) {
                    MainClass.speakWithNarrator(text.substring(0, 1), true);
                }
                return;
            }
            case GLFW.GLFW_KEY_END: {
                String text = this.stringGetter.get();
                if (Strings.isNotEmpty(text)) {
                    MainClass.speakWithNarrator(text.substring(text.length() - 1), true);
                }
            }
        }
    }

    @Unique
    private int getCursorPosByWordsWithOffset(int offset) {
        return TextHandler.moveCursorByWords(this.stringGetter.get(), offset, this.selectionStart, true);
    }

    @Unique
    private int getCursorPosWithOffset(int offset) {
        return Util.moveCursor(this.stringGetter.get(), this.selectionStart, offset);
    }

    @Inject(at = @At("RETURN"), method = "handleSpecialKey")
    private void speakSelectedText(int keyCode, CallbackInfoReturnable<Boolean> cir) {
        String selectedText = this.getSelectedText(this.stringGetter.get());
        MainClass.speakWithNarratorIfNotEmpty(selectedText, true);
    }

    @Inject(at = @At("HEAD"), method = "delete(I)V")
    private void speakErasedText(int offset, CallbackInfo ci) {
        int cursorPos = Util.moveCursor(this.stringGetter.get(), this.selectionStart, offset);
        // select all text (ctrl+a) will not change the cursor position,
        // if we delete all text then, the erasedText will be a wrong value (one char ahead of cursor)
        // don't speak under this condition
        boolean allTextAreSelected = this.selectionEnd == 0;
        if (!allTextAreSelected) {
            String erasedText = getCursorHoveredOverText(cursorPos);
            MainClass.speakWithNarratorIfNotEmpty(erasedText, true);
        }
    }

    @Unique
    private String getCursorHoveredOverText(int changedCursorPos) {
        int currentCursorPos = this.selectionStart;
        int startPos = Math.min(changedCursorPos, currentCursorPos);
        int endPos = Math.max(changedCursorPos, currentCursorPos);
        return startPos == endPos ? "" : this.stringGetter.get().substring(startPos, endPos);
    }
}
