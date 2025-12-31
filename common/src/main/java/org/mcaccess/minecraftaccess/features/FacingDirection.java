package org.mcaccess.minecraftaccess.features;

import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingsHandler;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;


/**
 * Adds key binding to narrate the player's facing direction.<br>
 * - Narrate Facing Direction Key (default: H) = Narrates the player facing direction.
 */
public class FacingDirection implements BalmClientModule {
    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "facing_direction");
    }

    @Override
    public void initialize() {
        ClientTickCallback.ClientPlayerTick.AFTER.register(this::tick);
    }

    private void tick(Player player) {
        if (Minecraft.getInstance().screen != null) return;

        boolean isDirectionNarrationKeyPressed = KeyMappingsHandler.Keys.DIRECTION_NARRATION_KEY.mapping.isDown();
        if (!isDirectionNarrationKeyPressed) return;

        String narration;
        if (Minecraft.getInstance().hasAltDown()) {
            String t = PlayerPositionUtils.getVerticalFacingDirectionInWords();
            narration = I18n.get("minecraft_access.other.facing_direction", t);
        } else {
            String string = PlayerPositionUtils.getHorizontalFacingDirectionInWords();
            narration = I18n.get("minecraft_access.other.facing_direction", string);
        }

        MainClass.narrate(narration, true);
    }
}
