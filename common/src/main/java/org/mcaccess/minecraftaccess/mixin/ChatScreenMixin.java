package org.mcaccess.minecraftaccess.mixin;

import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.MainClass;
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
    private static final Component USAGE_TEXT = Component.translatable("chat_screen.usage");

    @Shadow
    protected EditBox input;

    /**
     * Removes `message to send` from the spoken text when entering a chat message.
     */
    @Inject(at = @At("HEAD"), method = "updateNarrationState", cancellable = true)
    private void addScreenNarrations(NarrationElementOutput builder, CallbackInfo callbackInfo) {
        if (Minecraft.getInstance().screen == null) return;
        builder.add(NarratedElementType.TITLE, Minecraft.getInstance().screen.getTitle());
        builder.add(NarratedElementType.USAGE, USAGE_TEXT);
        String string = this.input.getValue();
        if (!string.isEmpty()) {
            builder.nest().add(NarratedElementType.TITLE, string);
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
        List<GuiMessage> messages = ((ChatComponentAccessor) Minecraft.getInstance().gui.getChat()).getAllMessages();
        if ((messages.size() - indexOffset) <= 0) return;

        MainClass.speakWithNarrator(messages.get(indexOffset).content().getString(), true);
    }

    /**
     * Since there is no text modifying narration, we want to manually speak when the chat history is switched.
     */
    @Inject(at = @At("TAIL"), method = "moveInHistory")
    private void speakSwitchedChatHistory(int index, CallbackInfo ci) {
        MainClass.speakWithNarratorIfNotEmpty(this.input.getValue(), true);
    }
}
