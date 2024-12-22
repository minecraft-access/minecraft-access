package com.github.khanshoaib3.minecraft_access.utils.condition;

import net.minecraft.client.MinecraftClient;

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

    public MenuKeystroke(BooleanSupplier condition) {
        super(condition);
    }

    public boolean canOpenMenu() {
        return isReleased() && !isMenuJustClosed;
    }

    /**
     * @return true if the menu is closed
     */
    public boolean closeMenuIfMenuKeyPressing() {
        if (isPressing()) {
            isMenuJustClosed = true;
            Objects.requireNonNull(MinecraftClient.getInstance().currentScreen).close();
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
