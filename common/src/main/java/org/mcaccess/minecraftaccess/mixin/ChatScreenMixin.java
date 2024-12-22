package com.github.khanshoaib3.minecraft_access.mixin;

import com.github.khanshoaib3.minecraft_access.MainClass;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.screen.narration.NarrationPart;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;


@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Unique
    private static final Text USAGE_TEXT = Text.translatable("chat_screen.usage");

    @Shadow
    protected TextFieldWidget chatField;

    /**
     * Removes `message to send` from the spoken text when entering a chat message.
     */
    @Inject(at = @At("HEAD"), method = "addScreenNarrations", cancellable = true)
    private void addScreenNarrations(NarrationMessageBuilder builder, CallbackInfo callbackInfo) {
        if (MinecraftClient.getInstance().currentScreen == null) return;
        builder.put(NarrationPart.TITLE, MinecraftClient.getInstance().currentScreen.getTitle());
        builder.put(NarrationPart.USAGE, USAGE_TEXT);
        String string = this.chatField.getText();
        if (!string.isEmpty()) {
            builder.nextMessage().put(NarrationPart.TITLE, string);
        }
        callbackInfo.cancel();
    }

    /**
     * Add custom keystroke handling for chat screen.
     */
    @Inject(at = @At("HEAD"), method = "keyPressed", cancellable = true)
    private void keyPressed(int keyCode, int scanCode, int modifiers, CallbackInfoReturnable<Boolean> cir) {
        if (!minecraft_access$repeatPreviousChatMessage(keyCode)) return;

        // Method executes to here means one of custom keystroke handling above is triggered,
        // so we want to cancel the logic in injected original method,
        // since its logic is also return after one handling triggered.
        cir.setReturnValue(true);
        cir.cancel();
    }

    /**
     * This method checks if the key code corresponds to a numeric key or numeric keypad key between 1 and 9,
     * while Alt key is pressed too.
     * If it does, it calls the {@link #minecraft_access$speakPreviousChatAtIndex(int)}
     * method with the corresponding index and returns true.
     *
     * @param keyCode the key code of the input event.
     * @return true if the input was handled, false otherwise.
     */
    @Unique
    private static boolean minecraft_access$repeatPreviousChatMessage(int keyCode) {
        if (Screen.hasAltDown()) {
            for (int i = 1; i <= 9; i++) {
                if (keyCode == GLFW.GLFW_KEY_0 + i || keyCode == GLFW.GLFW_KEY_KP_0 + i) {
                    minecraft_access$speakPreviousChatAtIndex(i - 1);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Speaks the previous chat message at the specified index offset.
     *
     * @param indexOffset the index offset from the most recent chat message to speak.
     */
    @Unique
    private static void minecraft_access$speakPreviousChatAtIndex(int indexOffset) {
        List<ChatHudLine> messages = ((ChatHudAccessor) MinecraftClient.getInstance().inGameHud.getChatHud()).getMessages();
        if ((messages.size() - indexOffset) <= 0) return;

        MainClass.speakWithNarrator(messages.get(indexOffset).content().getString(), true);
    }

    /**
     * Since there is no text modifying narration, we want to manually speak when the chat history is switched.
     */
    @Inject(at = @At("TAIL"), method = "setChatFromHistory")
    private void speakSwitchedChatHistory(int index, CallbackInfo ci) {
        MainClass.speakWithNarratorIfNotEmpty(this.chatField.getText(), true);
    }
}
