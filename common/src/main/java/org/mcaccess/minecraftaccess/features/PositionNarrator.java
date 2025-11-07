package org.mcaccess.minecraftaccess.features;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingsHandler;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;


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
    private static Window window = Minecraft.getInstance().getWindow();
    public static Keystroke keyX = new Keystroke(() -> InputConstants.isKeyDown(window, InputConstants.KEY_X));
    public static Keystroke keyC = new Keystroke(() -> InputConstants.isKeyDown(window, InputConstants.KEY_C));
    public static Keystroke keyZ = new Keystroke(() -> InputConstants.isKeyDown(window, InputConstants.KEY_Z));
    public static Keystroke positionNarrationKey = new Keystroke(() -> KeyMappingsHandler.Keys.POSITION_NARRATION_KEY.mapping.isDown());

    private PositionNarrator() {
    }

    public void tick() {
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen != null) return;

        if (Minecraft.getInstance().hasAltDown()) {
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
