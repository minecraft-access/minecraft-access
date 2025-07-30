package org.mcaccess.minecraftaccess.features;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;

@Slf4j
public final class PositionNarrator {
    private boolean isXKeyDown = false;
    private boolean isCKeyDown = false;
    private boolean isZKeyDown = false;
    private boolean isPositionNarrationKeyDown = false;
    private boolean wasAltDownLastTick = false;

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.screen != null) return;

        boolean isAltDown = Screen.hasAltDown();
        long window = client.getWindow().getWindow();

        if (isAltDown) {
            if (InputConstants.isKeyDown(window, InputConstants.KEY_X)) {
                // Only trigger if Alt was already down when X was pressed
                if (!isXKeyDown && wasAltDownLastTick) {
                    isXKeyDown = true;
                    MainClass.narrate(PlayerPositionUtils.getNarratableXPos(), true);
                } else if (!isXKeyDown) {
                    // X pressed but Alt wasn't down first - just mark as down without narrating
                    isXKeyDown = true;
                }
            } else {
                isXKeyDown = false;
            }

            if (InputConstants.isKeyDown(window, InputConstants.KEY_C)) {
                if (!isCKeyDown && wasAltDownLastTick) {
                    isCKeyDown = true;
                    MainClass.narrate(PlayerPositionUtils.getNarratableYPos(), true);
                } else if (!isCKeyDown) {
                    isCKeyDown = true;
                }
            } else {
                isCKeyDown = false;
            }

            if (InputConstants.isKeyDown(window, InputConstants.KEY_Z)) {
                if (!isZKeyDown && wasAltDownLastTick) {
                    isZKeyDown = true;
                    MainClass.narrate(PlayerPositionUtils.getNarratableZPos(), true);
                } else if (!isZKeyDown) {
                    isZKeyDown = true;
                }
            } else {
                isZKeyDown = false;
            }
        } else {
            isXKeyDown = false;
            isCKeyDown = false;
            isZKeyDown = false;
        }

        // Update Alt state for next tick
        wasAltDownLastTick = isAltDown;

        if (KeyBindingsHandler.POSITION_NARRATION_KEY.mapping.consumeClick()) {
            if (!isPositionNarrationKeyDown) {
                isPositionNarrationKeyDown = true;
                MainClass.narrate(PlayerPositionUtils.getNarratableXYZPosition(), true);
            }
        } else if (!KeyBindingsHandler.POSITION_NARRATION_KEY.mapping.isDown()) {
            isPositionNarrationKeyDown = false;
        }
    }
}
