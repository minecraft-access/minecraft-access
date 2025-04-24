package org.mcaccess.minecraftaccess.utils.system;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.mcaccess.minecraftaccess.mixin.KeyMappingAccessor;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * For Alt (left OR right), Shift (left OR right), Control (left OR right) pressing conditions,
 * use Screen.has[Alt|Shift|Control]Down() methods instead.
 */
public class KeyUtils {
    public static boolean isOnePressed(int keyCode) {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null) return false;
        long handle = minecraftClient.getWindow().getWindow();
        return InputConstants.isKeyDown(handle, keyCode);
    }

    /**
     * Pass any number of key codes (they are registered as constants: InputConstants.KEY_*)
     */
    public static boolean isAnyPressed(int... keyCodes) {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null) return false;
        long handle = minecraftClient.getWindow().getWindow();
        return IntStream.of(keyCodes).anyMatch(c -> InputConstants.isKeyDown(handle, c));
    }

    /**
     * This works even if the keybinding is duplicate i.e. another keybinding has the same key bound to it.<br>
     * <a href="https://minecraft.wiki/w/Key_codes">Vanilla Keybinding instance translation keys in Minecraft</a><br>
     * Get these instances via InputUtil.fromTranslationKey({key})
     * <p>
     * According to {@link InputConstants.Type}, code of non-keyboard keys (mouse keys 0~7 + unknown key -1) are less than 8.
     * Since the {@link KeyMapping} only supports 1 binding per key
     * (meaning the last to register is the winner and be handled for checking isPressed()),
     * our multiple-keybindings-on-one-key usage is not supported.
     */
    public static boolean isAnyPressed(KeyMapping... keyBindings) {
        return Arrays.stream(keyBindings).anyMatch(KeyUtils::isKeyPressed);
    }

    private static boolean isKeyPressed(KeyMapping b) {
        int keyCode = ((KeyMappingAccessor) b).getKey().getValue();
        if (keyCode > 7) {
            // If this keybinding is bound to a keyboard-key,
            // let's use our key-pressing-check logic to circumvent the limitation.
            return KeyUtils.isOnePressed(keyCode);
        } else {
            // If this keybinding is bound to a non-keyboard key, execute the original method.
            return b.isDown();
        }
    }

    public static boolean isF3Pressed() {
        return isAnyPressed(InputConstants.KEY_F3);
    }

    public static boolean isLeftShiftPressed() {
        return isAnyPressed(InputConstants.KEY_LSHIFT);
    }

    public static boolean isLeftAltPressed() {
        return isAnyPressed(InputConstants.KEY_LALT);
    }

    public static boolean isRightAltPressed() {
        return isAnyPressed(InputConstants.KEY_RALT);
    }

    public static boolean isEnterPressed() {
        return isAnyPressed(InputConstants.KEY_RETURN, InputConstants.KEY_NUMPADENTER);
    }

    public static boolean isSpacePressed() {
        return isAnyPressed(InputConstants.KEY_SPACE);
    }

    public static boolean isSpaceOrEnterPressed() {
        return isEnterPressed() || isSpacePressed();
    }
}
