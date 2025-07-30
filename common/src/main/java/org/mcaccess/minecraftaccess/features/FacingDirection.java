package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;

/**
 * Adds key binding to narrate the player's facing direction.<br>
 * - Narrate Facing Direction Key (default: H) = Narrates the player facing direction.
 */
@Slf4j
public class FacingDirection {
    private boolean isDirectionNarrationKeyDown = false;

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        if (client.screen != null) return;

        if (KeyBindingsHandler.DIRECTION_NARRATION_KEY.mapping.consumeClick()) {
            if (!isDirectionNarrationKeyDown) {
                isDirectionNarrationKeyDown = true;
                String narration;
                if (Screen.hasAltDown()) {
                    String verticleDirection = PlayerPositionUtils.getVerticalFacingDirectionInWords();
                    narration = I18n.get("minecraft_access.other.facing_direction", verticleDirection);
                } else {
                    String horizontalDirection = PlayerPositionUtils.getHorizontalFacingDirectionInWords();
                    narration = I18n.get("minecraft_access.other.facing_direction", horizontalDirection);
                }

                MainClass.narrate(narration, true);
            }
        } else if (!KeyBindingsHandler.DIRECTION_NARRATION_KEY.mapping.isDown()) {
            isDirectionNarrationKeyDown = false;
        }
    }
}
