package org.mcaccess.minecraftaccess.features;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.config.config_maps.OtherConfigsMap;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

/**
 * Adds key bindings to speak the player's position.<br><br>
 * Keybindings and combinations:<br>
 * 1. Speak Player Position Key (default: G) = Speaks the player's x y and z position.<br>
 * 2. Left Alt + X = Speaks only the x position.<br>
 * 3. Left Alt + C = Speaks only the y position.<br>
 * 4. Left Alt + Z = Speaks only the z position.<br>
 */
@Slf4j
public class PositionNarrator {
    @Getter
    private static final PositionNarrator instance;
    public static Keystroke KeyX = new Keystroke(() -> KeyUtils.isAnyPressed(GLFW.GLFW_KEY_X));
    public static Keystroke KeyC = new Keystroke(() -> KeyUtils.isAnyPressed(GLFW.GLFW_KEY_C));
    public static Keystroke KeyZ = new Keystroke(() -> KeyUtils.isAnyPressed(GLFW.GLFW_KEY_Z));
    public static Keystroke positionNarrationKey;

    static {
        instance = new PositionNarrator();
        positionNarrationKey = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().positionNarrationKey));
    }

    private PositionNarrator() {
    }

    public void update() {
        try {
            if (!OtherConfigsMap.getInstance().isPositionNarratorEnabled()) return;

            Minecraft minecraftClient = Minecraft.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.screen != null) return;

            boolean isLeftAltPressed = KeyUtils.isLeftAltPressed();
            if (isLeftAltPressed) {
                if (KeyX.canBeTriggered()) {
                    MainClass.speakWithNarrator(PlayerPositionUtils.getNarratableXPos(), true);
                } else if (KeyC.canBeTriggered()) {
                    MainClass.speakWithNarrator(PlayerPositionUtils.getNarratableYPos(), true);
                } else if (KeyZ.canBeTriggered()) {
                    MainClass.speakWithNarrator(PlayerPositionUtils.getNarratableZPos(), true);
                }
            }

            if (positionNarrationKey.canBeTriggered()) {
                MainClass.speakWithNarrator(PlayerPositionUtils.getNarratableXYZPosition(), true);
            }

        } catch (Exception e) {
            log.error("An error occurred in PositionNarrator.", e);
        }
    }


}
