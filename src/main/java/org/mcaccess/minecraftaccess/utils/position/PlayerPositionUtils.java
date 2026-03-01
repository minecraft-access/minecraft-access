package org.mcaccess.minecraftaccess.utils.position;

import net.minecraft.client.Minecraft;

import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

/**
 * Functions about getting player entity's position, facing direction etc.
 */
public final class PlayerPositionUtils {
    private static final Minecraft CLIENT = Minecraft.getInstance();
    private static final String POSITION_FORMAT = "{x}, {y}, {z}";

    private PlayerPositionUtils() {
    }

    public static String getNarratableXYZPosition() {
        return POSITION_FORMAT.replace("{x}", getNarratableXPos()).replace("{y}", getNarratableYPos()).replace("{z}", getNarratableZPos());
    }

    public static String getNarratableXPos() {
        assert CLIENT.player != null;
        return NarrationUtils.narrateNumber(CLIENT.player.position().x) + 'x';
    }

    public static String getNarratableYPos() {
        assert CLIENT.player != null;
        return NarrationUtils.narrateNumber(CLIENT.player.position().y) + 'y';
    }

    public static String getNarratableZPos() {
        assert CLIENT.player != null;
        return NarrationUtils.narrateNumber(CLIENT.player.position().z) + 'z';
    }

    public static Translation getVerticalFacingDirectionInWords() {
        assert CLIENT.player != null;
        int angle = Math.round(CLIENT.player.getRotationVector().x);
        if (Math.abs(angle) >= 88) {
            return new Translation("minecraft_access.direction").variant(angle > 0 ? "up" : "down");
        } else if (Math.abs(angle) >= 3) {
            return new Translation("minecraft_access.direction.degrees")
                    .variable("degrees").put(Math.abs(angle))
                    .variable("direction").put(new Translation("minecraft_access.direction").variant(angle > 0 ? "up" : "down"));
        } else {
            return new Translation("minecraft_access.direction").variant("straight");
        }
    }

    public static Orientation getHorizontalFacing() {
        assert CLIENT.player != null;
        return Orientation.ofHorizontal((int) CLIENT.player.getRotationVector().y % 360);
    }

    public static Translation.Unmodifiable getHorizontalFacingDirectionInWords() {
        return new Translation("minecraft_access.direction")
                .variant(getHorizontalFacing().toString())
                .unmodifiableView();
    }
}
