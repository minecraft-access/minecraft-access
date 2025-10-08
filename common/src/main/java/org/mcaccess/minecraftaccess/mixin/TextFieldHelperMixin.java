package org.mcaccess.minecraftaccess.mixin;

import java.util.function.Supplier;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.Util;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import org.apache.logging.log4j.util.Strings;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import org.mcaccess.minecraftaccess.MainClass;

/**
 * Mixin the inner content manager of {@link AbstractSignEditScreen}, {@link BookEditScreen} to make text editing on these screens accessible.
 */
@Mixin(TextFieldHelper.class)
abstract class TextFieldHelperMixin {
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
    private void narrateTextOfSwitchedLine(CallbackInfo ci) {
        MainClass.narrate(getMessageFn.get(), true);
    }

    @Inject(at = @At("HEAD"), method = "keyPressed")
    private void narrateCursorHoverOverText(int keyCode, CallbackInfoReturnable<Boolean> cir) {
        // is selecting, let the selecting text narrating method do the job instead
        if (Screen.hasShiftDown()) {
            return;
        }

        switch (keyCode) {
            case InputConstants.KEY_LEFT -> {
                if (Screen.hasControlDown()) {
                    String hoveredText = getCursorHoveredOverText(getCursorPosByWordsWithOffset(-1));
                    MainClass.narrate(hoveredText, true);
                } else {
                    String hoveredText = getCursorHoveredOverText(getCursorPosWithOffset(-1));
                    MainClass.narrate(hoveredText, true);
                }
            }
            case InputConstants.KEY_RIGHT -> {
                if (Screen.hasControlDown()) {
                    String hoveredText = getCursorHoveredOverText(getCursorPosByWordsWithOffset(1));
                    MainClass.narrate(hoveredText, true);
                } else {
                    String hoveredText = getCursorHoveredOverText(getCursorPosWithOffset(1));
                    MainClass.narrate(hoveredText, true);
                }
            }
            case InputConstants.KEY_HOME -> {
                String text = getMessageFn.get();
                if (Strings.isNotEmpty(text)) {
                    MainClass.narrate(text.substring(0, 1), true);
                }
            }
            case InputConstants.KEY_END -> {
                String text = getMessageFn.get();
                if (Strings.isNotEmpty(text)) {
                    MainClass.narrate(text.substring(text.length() - 1), true);
                }
            }
        }
    }

    @Unique
    private int getCursorPosByWordsWithOffset(int offset) {
        return StringSplitter.getWordPosition(getMessageFn.get(), offset, cursorPos, true);
    }

    @Unique
    private int getCursorPosWithOffset(int offset) {
        return Util.offsetByCodepoints(getMessageFn.get(), cursorPos, offset);
    }

    @Inject(at = @At("RETURN"), method = "keyPressed")
    private void narrateSelectedText(CallbackInfoReturnable<Boolean> cir) {
        String selectedText = getSelected(getMessageFn.get());
        MainClass.narrate(selectedText, true);
    }

    @Inject(at = @At("HEAD"), method = "removeCharsFromCursor")
    private void narrateErasedText(int offset, CallbackInfo ci) {
        int cursorPos = Util.offsetByCodepoints(getMessageFn.get(), this.cursorPos, offset);
        // select all text (ctrl+a) will not change the cursor position,
        // if we delete all text then, the erasedText will be a wrong value (one char ahead of cursor)
        // don't narrate under this condition
        boolean allTextSelected = selectionPos == 0;
        if (!allTextSelected) {
            String erasedText = getCursorHoveredOverText(cursorPos);
            MainClass.narrate(erasedText, true);
        }
    }

    @Unique
    private String getCursorHoveredOverText(int changedCursorPos) {
        int currentCursorPos = cursorPos;
        int startPos = Math.min(changedCursorPos, currentCursorPos);
        int endPos = Math.max(changedCursorPos, currentCursorPos);
        return startPos == endPos ? "" : getMessageFn.get().substring(startPos, endPos);
    }
}
