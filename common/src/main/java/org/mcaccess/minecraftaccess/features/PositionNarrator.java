package org.mcaccess.minecraftaccess.features;

import com.mojang.blaze3d.platform.InputConstants;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

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
public class PositionNarrator implements BalmClientModule {
    public static Keystroke keyX = new Keystroke(() -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_X));
    public static Keystroke keyC = new Keystroke(() -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_C));
    public static Keystroke keyZ = new Keystroke(() -> InputConstants.isKeyDown(Minecraft.getInstance().getWindow(), InputConstants.KEY_Z));
    public static Keystroke positionNarrationKey = new Keystroke(KeyMappingsHandler.Keys.POSITION_NARRATION_KEY.mapping::isDown);

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "position_narrator");
    }

    @Override
    public void initialize() {
        ClientTickCallback.ClientPlayerTick.AFTER.register(this::tick);
    }

    public void tick(Player player) {
        if (Minecraft.getInstance().screen != null) return;

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
