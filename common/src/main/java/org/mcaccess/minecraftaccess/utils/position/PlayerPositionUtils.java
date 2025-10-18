package org.mcaccess.minecraftaccess.utils.position;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

import org.mcaccess.minecraftaccess.utils.NarrationUtils;

/**
 * Functions about getting player entity's position, facing direction etc.
 */
@Slf4j
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

    /**
     * @return -90 (head up) ~ 90 (head down)
     */
    public static int getVerticalFacingDirection() {
        assert CLIENT.player != null;
        return (int) CLIENT.player.getRotationVector().x;
    }

    /**
     * Get the vertical direction in words.
     *
     * @return the vertical direction in words. null on error.
     */
    public static String getVerticalFacingDirectionInWords() {
        int angle = getVerticalFacingDirection();
        if (isBetween(angle, -90, -88)) {
            return I18n.get("minecraft_access.direction.up");
        } else if (isBetween(angle, -87, -3)) {
            return I18n.get("minecraft_access.direction.degrees", NarrationUtils.narrateNumber(-angle)) + ' ' + I18n.get("minecraft_access.direction.up");
        } else if (isBetween(angle, -2, 2)) {
            return I18n.get("minecraft_access.direction.straight");
        } else if (isBetween(angle, 3, 87)) {
            return I18n.get("minecraft_access.direction.degrees", NarrationUtils.narrateNumber(angle)) + ' ' + I18n.get("minecraft_access.direction.down");
        } else if (isBetween(angle, 88, 90)) {
            return I18n.get("minecraft_access.direction.down");
        } else {
            return null;
        }
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    public static int getHorizontalFacingDirectionInDegrees() {
        assert CLIENT.player != null;
        int angle = (int) CLIENT.player.getRotationVector().y;
        return angle % 360;
    }

    public static Orientation getHorizontalFacing() {
        int angle = getHorizontalFacingDirectionInDegrees();
        return Orientation.ofHorizontal(angle);
    }

    public static String getHorizontalFacingDirectionInWords() {
        return I18n.get("minecraft_access.direction." + getHorizontalFacing());
    }
}
