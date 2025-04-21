package org.mcaccess.minecraftaccess.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.MainClass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ChatScreen.class)
public class ChatScreenMixin {
    @Unique
    private static final Component USAGE_TEXT = Component.translatable("chat_screen.usage");

    @Unique
    private static int minecraft_access$currentChatMessagePage;

    @Shadow
    protected EditBox input;

    @Inject(at = @At("HEAD"), method = "init")
    private void init(CallbackInfo ci) {
        minecraft_access$currentChatMessagePage = 0;
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/EditBox;getValue()Ljava/lang/String;"), method = "updateNarrationState")
    private String suppressContent(EditBox instance) {
        return "";
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
        long window = Minecraft.getInstance().getWindow().getWindow();
        int numMessages = ((ChatComponentAccessor) Minecraft.getInstance().gui.getChat()).getAllMessages().size();
        int newChatMessagePage = minecraft_access$currentChatMessagePage;
        if (Screen.hasAltDown()) {
            if (InputConstants.isKeyDown(window, InputConstants.KEY_GRAVE) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_KP_MULTIPLY)) {
                if (Screen.hasControlDown()) {
                    newChatMessagePage = numMessages / 10;
                } else {
                    newChatMessagePage = 0;
                }
            } else if (InputConstants.isKeyDown(window, InputConstants.KEY_EQUALS) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_KP_ADD)) {
                newChatMessagePage -= 1;
                if (Screen.hasControlDown()) {
                    newChatMessagePage -= 4;
                }
            } else if (InputConstants.isKeyDown(window, InputConstants.KEY_MINUS) || InputConstants.isKeyDown(window, GLFW.GLFW_KEY_KP_SUBTRACT)) {
                newChatMessagePage += 1;
                if (Screen.hasControlDown()) {
                    newChatMessagePage += 4;
                }
            }

            newChatMessagePage = Math.clamp(newChatMessagePage, 0, numMessages / 10);

            if (newChatMessagePage != minecraft_access$currentChatMessagePage) {
                minecraft_access$currentChatMessagePage = newChatMessagePage;
                MainClass.speakWithNarrator(I18n.get("minecraft_access.gui.chat_screen.showing_message_range", (newChatMessagePage * 10) + 1, (newChatMessagePage + 1) * 10), true);
            }

            for (int i = 1; i <= 9; i++) {
                if (keyCode == GLFW.GLFW_KEY_0 + i || keyCode == GLFW.GLFW_KEY_KP_0 + i) {
                    minecraft_access$speakPreviousChatAtIndex(i + minecraft_access$currentChatMessagePage * 10 - 1);
                    return true;
                }
            }
            if (InputConstants.isKeyDown(window, GLFW.GLFW_KEY_0) || InputConstants.isKeyDown(window, InputConstants.KEY_NUMPAD0)) {
                minecraft_access$speakPreviousChatAtIndex(10 + minecraft_access$currentChatMessagePage * 10 - 1);
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
        MainClass.speakWithNarrator(this.input.getValue(), true);
    }
}
