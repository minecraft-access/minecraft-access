package com.github.khanshoaib3.minecraft_access.utils.system;

import com.github.khanshoaib3.minecraft_access.mixin.KeyBindingAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * For Alt (left OR right), Shift (left OR right), Control (left OR right) pressing conditions,
 * use Screen.has[Alt|Shift|Control]Down() methods instead.
 */
public class KeyUtils {
    public static boolean isOnePressed(int keyCode) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) return false;
        long handle = minecraftClient.getWindow().getHandle();
        return InputUtil.isKeyPressed(handle, keyCode);
    }

    /**
     * Pass any number of key codes (they are registered as constants: GLFW.GLFW_*)
     */
    public static boolean isAnyPressed(int... keyCodes) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) return false;
        long handle = minecraftClient.getWindow().getHandle();
        return IntStream.of(keyCodes).anyMatch(c -> InputUtil.isKeyPressed(handle, c));
    }

    @SuppressWarnings("unused")
    public static boolean isAllPressed(int... keyCodes) {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) return false;
        long handle = minecraftClient.getWindow().getHandle();
        return IntStream.of(keyCodes).allMatch(c -> InputUtil.isKeyPressed(handle, c));
    }

    /**
     * This works even if the keybinding is duplicate i.e. another keybinding has the same key bound to it.<br>
     * <a href="https://minecraft.wiki/w/Key_codes">Vanilla Keybinding instance translation keys in Minecraft</a><br>
     * Get these instances via InputUtil.fromTranslationKey({key})
     * <p>
     * According to {@link InputUtil.Type}, code of non-keyboard keys (mouse keys 0~7 + unknown key -1) are less than 8.
     * Since the {@link KeyBinding} only supports 1 binding per key
     * (meaning the last to register is the winner and be handled for checking isPressed()),
     * our multiple-keybindings-on-one-key usage is not supported.
     */
    public static boolean isAnyPressed(KeyBinding... keyBindings) {
        return Arrays.stream(keyBindings).anyMatch(b -> {
                    int keyCode = ((KeyBindingAccessor) b).getBoundKey().getCode();
                    if (keyCode > 7) {
                        // If this keybinding is bound to a keyboard-key,
                        // let's use our key-pressing-check logic to circumvent the limitation.
                        return KeyUtils.isOnePressed(keyCode);
                    } else {
                        // If this keybinding is bound to a non-keyboard key, execute the original method.
                        return b.isPressed();
                    }
                }
        );
    }

    @SuppressWarnings("unused")
    public static boolean isAllPressed(KeyBinding... keyBindings) {
        return Arrays.stream(keyBindings).allMatch(b -> {
                    int keyCode = ((KeyBindingAccessor) b).getBoundKey().getCode();
                    if (keyCode > 7) {
                        return KeyUtils.isOnePressed(keyCode);
                    } else {
                        return b.isPressed();
                    }
                }
        );
    }

    public static boolean isF3Pressed() {
        return isAnyPressed(GLFW.GLFW_KEY_F3);
    }

    public static boolean isLeftShiftPressed() {
        return isAnyPressed(GLFW.GLFW_KEY_LEFT_SHIFT);
    }

    public static boolean isLeftAltPressed() {
        return isAnyPressed(GLFW.GLFW_KEY_LEFT_ALT);
    }

    public static boolean isRightAltPressed() {
        return isAnyPressed(GLFW.GLFW_KEY_RIGHT_ALT);
    }

    public static boolean isEnterPressed() {
        return isAnyPressed(GLFW.GLFW_KEY_ENTER, GLFW.GLFW_KEY_KP_ENTER);
    }

    public static boolean isSpacePressed() {
        return isAnyPressed(GLFW.GLFW_KEY_SPACE);
    }
}
