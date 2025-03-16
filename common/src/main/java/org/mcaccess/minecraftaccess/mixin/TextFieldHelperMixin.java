package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import org.apache.logging.log4j.util.Strings;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.MainClass;
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
@Mixin(TextFieldHelper.class)
public abstract class TextFieldHelperMixin {
    @Final
    @Shadow
    private Supplier<String> getMessageFn;
    @Shadow
    private int cursorPos;
    @Shadow
    private int selectionPos;

    @Shadow
    protected abstract String getSelected(String string);

    @Inject(at = @At("TAIL"), method = "setCursorToEnd()V")
    public void speakTextOfSwitchedLine(CallbackInfo ci) {
        MainClass.speakWithNarratorIfNotEmpty(this.getMessageFn.get(), true);
    }

    @Inject(at = @At("HEAD"), method = "keyPressed")
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
                String text = this.getMessageFn.get();
                if (Strings.isNotEmpty(text)) {
                    MainClass.speakWithNarrator(text.substring(0, 1), true);
                }
                return;
            }
            case GLFW.GLFW_KEY_END: {
                String text = this.getMessageFn.get();
                if (Strings.isNotEmpty(text)) {
                    MainClass.speakWithNarrator(text.substring(text.length() - 1), true);
                }
            }
        }
    }

    @Unique
    private int getCursorPosByWordsWithOffset(int offset) {
        return StringSplitter.getWordPosition(this.getMessageFn.get(), offset, this.cursorPos, true);
    }

    @Unique
    private int getCursorPosWithOffset(int offset) {
        return Util.offsetByCodepoints(this.getMessageFn.get(), this.cursorPos, offset);
    }

    @Inject(at = @At("RETURN"), method = "keyPressed")
    private void speakSelectedText(int keyCode, CallbackInfoReturnable<Boolean> cir) {
        String selectedText = this.getSelected(this.getMessageFn.get());
        MainClass.speakWithNarratorIfNotEmpty(selectedText, true);
    }

    @Inject(at = @At("HEAD"), method = "removeCharsFromCursor")
    private void speakErasedText(int offset, CallbackInfo ci) {
        int cursorPos = Util.offsetByCodepoints(this.getMessageFn.get(), this.cursorPos, offset);
        // select all text (ctrl+a) will not change the cursor position,
        // if we delete all text then, the erasedText will be a wrong value (one char ahead of cursor)
        // don't speak under this condition
        boolean allTextAreSelected = this.selectionPos == 0;
        if (!allTextAreSelected) {
            String erasedText = getCursorHoveredOverText(cursorPos);
            MainClass.speakWithNarratorIfNotEmpty(erasedText, true);
        }
    }

    @Unique
    private String getCursorHoveredOverText(int changedCursorPos) {
        int currentCursorPos = this.cursorPos;
        int startPos = Math.min(changedCursorPos, currentCursorPos);
        int endPos = Math.max(changedCursorPos, currentCursorPos);
        return startPos == endPos ? "" : this.getMessageFn.get().substring(startPos, endPos);
    }
}
