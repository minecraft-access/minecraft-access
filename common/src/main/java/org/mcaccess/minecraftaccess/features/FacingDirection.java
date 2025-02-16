package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

/**
 * Adds key binding to speak the player's facing direction.<br>
 * - Speak Facing Direction Key (default: H) = Speaks the player facing direction.
 */
@Slf4j
public class FacingDirection {
    public void update() {
        try {
            Minecraft minecraftClient = Minecraft.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.screen != null) return;

            boolean isDirectionNarrationKeyPressed = KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().directionNarrationKey);
            if (!isDirectionNarrationKeyPressed) return;

            boolean isLeftAltPressed = KeyUtils.isLeftAltPressed();

            String toSpeak;
            if (isLeftAltPressed) {
                String t = PlayerPositionUtils.getVerticalFacingDirectionInWords();
                toSpeak = I18n.get("minecraft_access.other.facing_direction", t);
            } else {
                String string = PlayerPositionUtils.getHorizontalFacingDirectionInWords();
                toSpeak = I18n.get("minecraft_access.other.facing_direction", string);
            }

            MainClass.speakWithNarrator(toSpeak, true);
        } catch (Exception e) {
            log.error("An error occurred in DirectionNarrator.", e);
        }
    }
}
