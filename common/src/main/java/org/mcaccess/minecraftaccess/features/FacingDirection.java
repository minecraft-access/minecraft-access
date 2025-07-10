package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

/**
 * Adds key binding to narrate the player's facing direction.<br>
 * - Narrate Facing Direction Key (default: H) = Narrates the player facing direction.
 */
@Slf4j
public class FacingDirection {
    public void tick() {
        try {
            Minecraft minecraftClient = Minecraft.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.screen != null) return;

            boolean isDirectionNarrationKeyPressed = KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().directionNarrationKey);
            if (!isDirectionNarrationKeyPressed) return;

            boolean isLeftAltPressed = KeyUtils.isLeftAltPressed();

            String narration;
            if (isLeftAltPressed) {
                String t = PlayerPositionUtils.getVerticalFacingDirectionInWords();
                narration = I18n.get("minecraft_access.other.facing_direction", t);
            } else {
                String string = PlayerPositionUtils.getHorizontalFacingDirectionInWords();
                narration = I18n.get("minecraft_access.other.facing_direction", string);
            }

            MainClass.narrate(narration, true);
        } catch (Exception e) {
            log.error("An error occurred in DirectionNarrator.", e);
        }
    }
}
