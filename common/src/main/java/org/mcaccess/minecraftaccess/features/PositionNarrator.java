package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.condition.Keystroke;
import com.github.khanshoaib3.minecraft_access.utils.position.PlayerPositionUtils;
import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

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

            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.currentScreen != null) return;

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

            KeyX.updateStateForNextTick();
            KeyC.updateStateForNextTick();
            KeyZ.updateStateForNextTick();
            positionNarrationKey.updateStateForNextTick();

        } catch (Exception e) {
            log.error("An error occurred in PositionNarrator.", e);
        }
    }


}
