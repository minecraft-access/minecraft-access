package org.mcaccess.minecraftaccess.utils.condition;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Contract;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

import java.util.Objects;
import java.util.function.BooleanSupplier;

/**
 * After experimentation, there is only one valid combination for opening and closing a menu with single key:
 * isReleased -> open the menu
 * isPressing -> close the menu
 * <p>
 * So here is an encapsulation of this knowledge.
 */
public class MenuKeystroke extends Keystroke {
    /**
     * Prevent the menu open again after menu is just closed by pressing MENU KEY.
     * The menu is closed by pressing the MENU KEY, and is opened by releasing the MENU KEY,
     * so if you slowly press down the MENU KEY while menu is opening, the menu will be opened again when you release the MENU KEY.
     */
    boolean isMenuJustClosed = false;

    public MenuKeystroke(KeyMapping singleKey) {
        this(() -> KeyUtils.isAnyPressed(singleKey));
    }

    public MenuKeystroke(BooleanSupplier condition) {
        super(condition);
    }

    @Contract(pure = true)
    public boolean canOpenMenu() {
        return isReleased() && !isMenuJustClosed;
    }

    /**
     * @return true if the menu is closed
     */
    public boolean closeMenuIfMenuKeyPressing() {
        if (isPressing()) {
            isMenuJustClosed = true;
            Objects.requireNonNull(Minecraft.getInstance().screen).onClose();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void updateStateForNextTick() {
        // clean the flag
        if (isReleased()) isMenuJustClosed = false;
        super.updateStateForNextTick();
    }
}
