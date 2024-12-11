package io.github.minecraft_access.minecraft_access.features;

import io.github.minecraft_access.minecraft_access.MainClass;
import io.github.minecraft_access.minecraft_access.utils.KeyBindingsHandler;
import io.github.minecraft_access.minecraft_access.utils.position.PlayerPositionUtils;
import io.github.minecraft_access.minecraft_access.utils.system.KeyUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;

/**
 * Adds key binding to speak the player's facing direction.<br>
 * - Speak Facing Direction Key (default: H) = Speaks the player facing direction.
 */
@Slf4j
public class FacingDirection {
    public void update() {
        try {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.currentScreen != null) return;

            boolean isDirectionNarrationKeyPressed = KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().directionNarrationKey);
            if (!isDirectionNarrationKeyPressed) return;

            boolean isLeftAltPressed = KeyUtils.isLeftAltPressed();

            String toSpeak;
            if (isLeftAltPressed) {
                String t = PlayerPositionUtils.getVerticalFacingDirectionInWords();
                toSpeak = I18n.translate("minecraft_access.other.facing_direction", t);
            } else {
                String string = PlayerPositionUtils.getHorizontalFacingDirectionInWords();
                toSpeak = I18n.translate("minecraft_access.other.facing_direction", string);
            }

            MainClass.speakWithNarrator(toSpeak, true);
        } catch (Exception e) {
            log.error("An error occurred in DirectionNarrator.", e);
        }
    }
}
