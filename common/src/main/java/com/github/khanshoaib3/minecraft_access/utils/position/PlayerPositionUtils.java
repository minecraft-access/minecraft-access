package com.github.khanshoaib3.minecraft_access.utils.position;

import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;
import com.github.khanshoaib3.minecraft_access.utils.NarrationUtils;
import com.github.khanshoaib3.minecraft_access.utils.WorldUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Functions about getting player entity's position, facing direction etc.
 */
@Slf4j
public class PlayerPositionUtils {

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
    public static Optional<Vec3d> getPlayerPosition() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client.player == null ? java.util.Optional.empty() : Optional.of(client.player.getPos());
    }

    public static Optional<BlockPos> getPlayerBlockPosition() {
        Optional<Vec3d> op = getPlayerPosition();
        if (op.isEmpty()) return Optional.empty();
        Vec3d p = op.get();
        return Optional.of(WorldUtils.blockPosOf(p));
    }

    public static String getI18NPosition() {
        String format = OtherConfigsMap.getInstance().getPositionNarratorFormat();

        // check if configured format is valid
        if (Objects.isNull(format) || !Stream.of("{x}", "{y}", "{z}").allMatch(format::contains)) {
            format = OtherConfigsMap.DEFAULT_POSITION_FORMAT;
        }

        String posX = NarrationUtils.narrateNumber(getX());
        String posY = NarrationUtils.narrateNumber(getY());
        String posZ = NarrationUtils.narrateNumber(getZ());

        return format.replace("{x}", posX).replace("{y}", posY).replace("{z}", posZ);
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
        return (int) WorldUtils.getClientPlayer().getRotationClient().x;
    }

    /**
     * Get the vertical direction in words.
     *
     * @return the vertical direction in words. null on error.
     */
    public static String getVerticalFacingDirectionInWords() {
        int angle = getVerticalFacingDirection();
        if (isBetween(angle, -90, -88)) {
            return I18n.translate("minecraft_access.direction.up");
        } else if (isBetween(angle, -87, -3)) {
            return I18n.translate("minecraft_access.direction.degrees", -angle) + " " + I18n.translate("minecraft_access.direction.up");
        } else if (isBetween(angle, -2, 2)) {
            return I18n.translate("minecraft_access.direction.straight");
        } else if (isBetween(angle, 3, 97)) {
            return I18n.translate("minecraft_access.direction.degrees", angle) + " " + I18n.translate("minecraft_access.direction.down");
        } else if (isBetween(angle, 88, 90)) {
            return I18n.translate("minecraft_access.direction.down");
        } else return null;
    }

    public static boolean isBetween(int x, int lower, int upper) {
        return lower <= x && x <= upper;
    }

    public static int getHorizontalFacingDirectionInDegrees() {
        int angle = (int) WorldUtils.getClientPlayer().getRotationClient().y;
        return angle % 360;
    }

    /**
     * Get the horizontal direction in words.
     *
     * @param onlyDirectionKey directly return the direction word without i18n it.
     * @return onlyDirectionKey ? direction word : translated direction
     */
    public static String getHorizontalFacingDirectionInCardinal(boolean onlyDirectionKey) {
        int angle = getHorizontalFacingDirectionInDegrees();
        String direction;

        if ((angle >= -150 && angle <= -120) || (angle >= 210 && angle <= 240)) {
            // Looking North East
            direction = "north_east";
        } else if ((angle >= -60 && angle <= -30) || (angle >= 300 && angle <= 330)) {
            // Looking South East
            direction = "south_east";
        } else if ((angle >= 30 && angle <= 60) || (angle >= -330 && angle <= -300)) {
            // Looking South West
            direction = "south_west";
        } else if ((angle >= 120 && angle <= 150) || (angle >= -240 && angle <= -210)) {
            // Looking North West
            direction = "north_west";
        } else {
            direction = WorldUtils.getClientPlayer().getHorizontalFacing().asString().toLowerCase();
        }

        if (onlyDirectionKey) return direction;
        else return I18n.translate("minecraft_access.direction." + direction);
    }

}
