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
public class PositionNarrator {
    @Getter
    private static final PositionNarrator instance;
    public static Keystroke KeyX = new Keystroke(() -> KeyUtils.isAnyPressed(InputConstants.KEY_X));
    public static Keystroke KeyC = new Keystroke(() -> KeyUtils.isAnyPressed(InputConstants.KEY_C));
    public static Keystroke KeyZ = new Keystroke(() -> KeyUtils.isAnyPressed(InputConstants.KEY_Z));
    public static Keystroke positionNarrationKey;

    static {
        instance = new PositionNarrator();
        positionNarrationKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().positionNarrationKey));
    }

    private PositionNarrator() {
    }

    public void tick() {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen != null) return;

        boolean isLeftAltPressed = KeyUtils.isLeftAltPressed();
        if (isLeftAltPressed) {
            if (KeyX.canBeTriggered()) {
                MainClass.narrate(PlayerPositionUtils.getNarratableXPos(), true);
            } else if (KeyC.canBeTriggered()) {
                MainClass.narrate(PlayerPositionUtils.getNarratableYPos(), true);
            } else if (KeyZ.canBeTriggered()) {
                MainClass.narrate(PlayerPositionUtils.getNarratableZPos(), true);
            }
        }

        if (positionNarrationKey.canBeTriggered()) {
            MainClass.narrate(PlayerPositionUtils.getNarratableXYZPosition(), true);
        }
    }
}
