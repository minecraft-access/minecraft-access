package org.mcaccess.minecraftaccess.utils.position;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;

import java.util.Optional;

/**
 * Functions about getting player entity's position, facing direction etc.
 */
@Slf4j
public class PlayerPositionUtils {
    private static final String POSITION_FORMAT = "{x}, {y}, {z}";

    public static double getX() {
        String tempPosX = String.valueOf(getPlayerPosition().orElseThrow().x);
        tempPosX = tempPosX.substring(0, tempPosX.indexOf(".") + 2);

        return Double.parseDouble(tempPosX);
    }

    public static double getY() {
        String tempPosY;
        tempPosY = String.valueOf(getPlayerPosition().orElseThrow().y);
        tempPosY = tempPosY.substring(0, tempPosY.indexOf(".") + 2);

        return Double.parseDouble(tempPosY);
    }

    public static double getZ() {
        String tempPosZ;
        tempPosZ = String.valueOf(getPlayerPosition().orElseThrow().z);
        tempPosZ = tempPosZ.substring(0, tempPosZ.indexOf(".") + 2);

        return Double.parseDouble(tempPosZ);
    }

    /**
     * Player position is at player's leg.
     */
    public static Optional<Vec3> getPlayerPosition() {
        Minecraft client = Minecraft.getInstance();
        return client.player == null ? java.util.Optional.empty() : Optional.of(client.player.position());
    }

    public static Optional<BlockPos> getPlayerBlockPosition() {
        Optional<Vec3> op = getPlayerPosition();
        if (op.isEmpty()) return Optional.empty();
        Vec3 p = op.get();
        return Optional.of(WorldUtils.blockPosOf(p));
    }

    public static String getNarratableXYZPosition() {
        return POSITION_FORMAT.replace("{x}", getNarratableXPos()).replace("{y}", getNarratableYPos()).replace("{z}", getNarratableZPos());
    }

    public static String getNarratableXPos() {
        return NarrationUtils.narrateNumber(getX()) + "x";
    }

    public static String getNarratableYPos() {
        return NarrationUtils.narrateNumber(getY()) + "y";
    }

    public static String getNarratableZPos() {
        return NarrationUtils.narrateNumber(getZ()) + "z";
    }

    /**
     * @return -90 (head up) ~ 90 (head down)
     */
    public static int getVerticalFacingDirection() {
        return (int) WorldUtils.getClientPlayer().getRotationVector().x;
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
            return I18n.get("minecraft_access.direction.degrees", -angle) + " " + I18n.get("minecraft_access.direction.up");
        } else if (isBetween(angle, -2, 2)) {
            return I18n.get("minecraft_access.direction.straight");
        } else if (isBetween(angle, 3, 97)) {
            return I18n.get("minecraft_access.direction.degrees", angle) + " " + I18n.get("minecraft_access.direction.down");
        } else if (isBetween(angle, 88, 90)) {
            return I18n.get("minecraft_access.direction.down");
        } else return null;
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    public static int getHorizontalFacingDirectionInDegrees() {
        int angle = (int) WorldUtils.getClientPlayer().getRotationVector().y;
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
