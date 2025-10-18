package org.mcaccess.minecraftaccess.features;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

/**
 * Adds key bindings to narrate the player's position.<br><br>
 * Keybindings and combinations:<br>
 * 1. Narrate Player Position Key (default: G) = Narrates the player's x y and z position.<br>
 * 2. Left Alt + X = Narrates only the x position.<br>
 * 3. Left Alt + C = Narrates only the y position.<br>
 * 4. Left Alt + Z = Narrates only the z position.<br>
 */
@Slf4j
public final class PositionNarrator {
    @Getter
    private static final PositionNarrator INSTANCE = new PositionNarrator();
    public static Keystroke keyX = new Keystroke(() -> KeyUtils.isAnyPressed(InputConstants.KEY_X));
    public static Keystroke keyC = new Keystroke(() -> KeyUtils.isAnyPressed(InputConstants.KEY_C));
    public static Keystroke keyZ = new Keystroke(() -> KeyUtils.isAnyPressed(InputConstants.KEY_Z));
    public static Keystroke positionNarrationKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.Keys.POSITION_NARRATION_KEY.mapping));

    private PositionNarrator() {
    }

    public void tick() {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen != null) return;

        boolean isLeftAltPressed = KeyUtils.isLeftAltPressed();
        if (isLeftAltPressed) {
            if (keyX.canBeTriggered()) {
                MainClass.narrate(PlayerPositionUtils.getNarratableXPos(), true);
            } else if (keyC.canBeTriggered()) {
                MainClass.narrate(PlayerPositionUtils.getNarratableYPos(), true);
            } else if (keyZ.canBeTriggered()) {
                MainClass.narrate(PlayerPositionUtils.getNarratableZPos(), true);
            }
        }

        if (positionNarrationKey.canBeTriggered()) {
            MainClass.narrate(PlayerPositionUtils.getNarratableXYZPosition(), true);
        }
    }
}
