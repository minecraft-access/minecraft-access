package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.CameraControlsConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.condition.DoubleClick;
import com.github.khanshoaib3.minecraft_access.utils.condition.Interval;
import com.github.khanshoaib3.minecraft_access.utils.position.Orientation;
import com.github.khanshoaib3.minecraft_access.utils.position.PlayerPositionUtils;
import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.Vec3d;

/**
 * This feature adds the following key binds to control the camera.<br><br>
 * Key binds and combinations:-<br>
 * 1) Look Up Key (default=i, alternate=keypad 8): Moves the camera vertically up by the normal rotating angle (default=22.5).<br>
 * 2) Look Right Key (default=l, alternate=keypad 6): Moves the camera vertically right by the normal rotating angle (default=22.5).<br>
 * 3) Look Down Key (default=k, alternate=keypad 2): Moves the camera vertically down by the normal rotating angle (default=22.5).<br>
 * 4) Look Left Key (default=j, alternate=keypad 4): Moves the camera vertically left by the normal rotating angle (default=22.5).<br>
 * 5) Left Alt + Look Up Key: Moves the camera vertically up by the modified rotating angle (default=11.25).<br>
 * 6) Left Alt + Look Right Key: Moves the camera vertically right by the modified rotating angle (default=11.25).<br>
 * 7) Left Alt + Look Down Key: Moves the camera vertically down by the modified rotating angle (default=11.25).<br>
 * 8) Left Alt + Look Left Key: Moves the camera vertically left by the modified rotating angle (default=11.25).<br>
 * 9) Right Alt + Look Up Key or Look North Key (default=keypad 7): Snaps the camera to the north block.<br>
 * 10) Right Alt + Look Right Key or Look East Key (default=keypad 9): Snaps the camera to the east block.<br>
 * 11) Right Alt + Look Down Key or Look South Key (default=keypad 3): Snaps the camera to the south block.<br>
 * 12) Right Alt + Look Left Key or Look West Key (default=keypad 1): Snaps the camera to the west block.<br>
 * 13) Center Camera Key (default=keypad 5): Snaps the camera to the closest cardinal direction and center it.<br>
 * 14) Left Alt + Center Camera Key : Snaps the camera to the closest opposite cardinal direction and center it.<br>
 * 15) Right Alt + double Look Up Key or Look Straight Up Key (default: Keypad 0): Snaps the camera to the look above head direction.<br>
 * 16) Right Alt + double Look Down Key or Look Straight Down Key (default: Keypad .): Snaps the camera to the look down at feet direction.
 */
@Slf4j
public class CameraControls {
    private MinecraftClient minecraftClient;

    private float normalRotatingDeltaAngle;
    private float modifiedRotatingDeltaAngle;
    private Interval interval;

    private static final DoubleClick straightUpDoubleClick;
    private static final DoubleClick straightDownDoubleClick;

    static {
        // config keystroke conditions
        straightUpDoubleClick = new DoubleClick(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().cameraControlsUp));
        straightDownDoubleClick = new DoubleClick(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().cameraControlsDown));
    }

    public CameraControls() {
        loadConfigurations();
    }

    public void update() {
        if (interval != null && !interval.hasEnded()) return;
        try {
            this.minecraftClient = MinecraftClient.getInstance();

            if (minecraftClient == null) return;
            if (minecraftClient.currentScreen != null) return; //Prevent running if any screen is opened

            loadConfigurations();
            boolean wasAnyKeyPressed = keyListener();
            if (wasAnyKeyPressed) interval.start();
        } catch (Exception e) {
            log.error("Error encountered in Camera Controls feature.", e);
        }
    }

    /**
     * Loads the configs from config.json
     */
    private void loadConfigurations() {
        float delta90Degrees = 600f; // 90 / 0.15

        CameraControlsConfigMap map = CameraControlsConfigMap.getInstance();
        interval = Interval.inMilliseconds(map.getDelayInMilliseconds(), interval);
        float normalRotatingAngle = map.getNormalRotatingAngle();
        float modifiedRotatingAngle = map.getModifiedRotatingAngle();
        normalRotatingDeltaAngle = delta90Degrees / (90 / normalRotatingAngle);
        modifiedRotatingDeltaAngle = delta90Degrees / (90 / modifiedRotatingAngle);
    }

    /**
     * Handles the key inputs
     */
    private boolean keyListener() {
        boolean isLeftAltPressed = KeyUtils.isLeftAltPressed();
        boolean isRightAltPressed = KeyUtils.isRightAltPressed();

        KeyBindingsHandler kbh = KeyBindingsHandler.getInstance();

        boolean isUpKeyPressed = KeyUtils.isAnyPressed(kbh.cameraControlsUp, kbh.cameraControlsAlternateUp);
        boolean isRightKeyPressed = KeyUtils.isAnyPressed(kbh.cameraControlsRight, kbh.cameraControlsAlternateRight);
        boolean isDownKeyPressed = KeyUtils.isAnyPressed(kbh.cameraControlsDown, kbh.cameraControlsAlternateDown);
        boolean isLeftKeyPressed = KeyUtils.isAnyPressed(kbh.cameraControlsLeft, kbh.cameraControlsAlternateLeft);

        boolean isNorthKeyPressed = KeyUtils.isAnyPressed(kbh.cameraControlsNorth)
                || (isUpKeyPressed && isRightAltPressed && !isLeftAltPressed);
        boolean isEastKeyPressed = KeyUtils.isAnyPressed(kbh.cameraControlsEast)
                || (isRightKeyPressed && isRightAltPressed && !isLeftAltPressed);
        boolean isWestKeyPressed = KeyUtils.isAnyPressed(kbh.cameraControlsWest)
                || (isLeftKeyPressed && isRightAltPressed && !isLeftAltPressed);
        boolean isSouthKeyPressed = KeyUtils.isAnyPressed(kbh.cameraControlsSouth)
                || (isDownKeyPressed && isRightAltPressed && !isLeftAltPressed);
        boolean isCenterCameraKeyPressed = KeyUtils.isAnyPressed(kbh.cameraControlsCenterCamera);

        boolean isStraightUpKeyPressed = KeyUtils.isAnyPressed(kbh.cameraControlsStraightUp);
        boolean isUpKeyDoublePressedWithRightAlt = isRightAltPressed && straightUpDoubleClick.canBeTriggered();
        boolean isStraightDownKeyPressed = KeyUtils.isAnyPressed(kbh.cameraControlsStraightDown);
        boolean isDownKeyDoublePressedWithRightAlt = isRightAltPressed && straightDownDoubleClick.canBeTriggered();

        straightUpDoubleClick.updateStateForNextTick();
        straightDownDoubleClick.updateStateForNextTick();

        // these two blocks of logic should be ahead of the normal up/down logic
        if (isStraightUpKeyPressed || isUpKeyDoublePressedWithRightAlt) {
            rotateCameraTo(Orientation.UP);
            return true;
        }

        if (isStraightDownKeyPressed || isDownKeyDoublePressedWithRightAlt) {
            rotateCameraTo(Orientation.DOWN);
            return true;
        }

        if (isNorthKeyPressed) {
            rotateCameraTo(Orientation.NORTH);
            return true;
        }

        if (isEastKeyPressed) {
            rotateCameraTo(Orientation.EAST);
            return true;
        }

        if (isWestKeyPressed) {
            rotateCameraTo(Orientation.WEST);
            return true;
        }

        if (isSouthKeyPressed) {
            rotateCameraTo(Orientation.SOUTH);
            return true;
        }

        float rotateAngle = isLeftAltPressed ? modifiedRotatingDeltaAngle : normalRotatingDeltaAngle;

        if (isUpKeyPressed) {
            rotateCameraBy(rotateAngle, RotatingDirection.UP);
            return true;
        }

        if (isRightKeyPressed) {
            rotateCameraBy(rotateAngle, RotatingDirection.RIGHT);
            return true;
        }

        if (isDownKeyPressed) {
            rotateCameraBy(rotateAngle, RotatingDirection.DOWN);
            return true;
        }

        if (isLeftKeyPressed) {
            rotateCameraBy(rotateAngle, RotatingDirection.LEFT);
            return true;
        }

        if (isCenterCameraKeyPressed) {
            centerCamera(isLeftAltPressed);
            return true;
        }

        return false;
    }

    private enum RotatingDirection {
        UP(0, -1),
        DOWN(0, 1),
        LEFT(-1, 0),
        RIGHT(1, 0);

        final int horizontalWight;
        final int verticalWight;
        final boolean isRotatingHorizontal;

        RotatingDirection(int horizontalWight, int verticalWight) {
            this.horizontalWight = horizontalWight;
            this.verticalWight = verticalWight;
            this.isRotatingHorizontal = horizontalWight != 0;
        }
    }

    /**
     * Rotates the player's camera.
     *
     * @param angle     by given angle
     * @param direction on given direction
     */
    private void rotateCameraBy(float angle, RotatingDirection direction) {
        if (minecraftClient.player == null) return;

        float horizontalAngleDelta = angle * direction.horizontalWight;
        float verticalAngleDelta = angle * direction.verticalWight;

        minecraftClient.player.changeLookDirection(horizontalAngleDelta, verticalAngleDelta);

        // log and speak new facing direction
        log.debug("Rotating camera by x:%d y:%d".formatted((int) horizontalAngleDelta, (int) verticalAngleDelta));

        String horizontalDirection = PlayerPositionUtils.getHorizontalFacingDirectionInWords();
        String verticalDirection = PlayerPositionUtils.getVerticalFacingDirectionInWords();

        if (OtherConfigsMap.getInstance().isFacingDirectionEnabled()) {
            if (direction.isRotatingHorizontal && horizontalDirection != null)
                MainClass.speakWithNarrator(horizontalDirection, true);
            else if (!direction.isRotatingHorizontal && verticalDirection != null)
                MainClass.speakWithNarrator(verticalDirection, true);
        }
    }

    /**
     * Move the camera (player's view).
     *
     * @param direction to given direction
     */
    private void rotateCameraTo(Orientation direction) {
        if (minecraftClient.player == null) return;

        Vec3d playerBlockPosition = minecraftClient.player.getPos();
        Vec3d targetBlockPosition = playerBlockPosition.add(Vec3d.of(direction.vector));

        minecraftClient.player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, targetBlockPosition);

        // log and speak new facing direction
        log.debug("Rotating camera to: %s".formatted(direction.name()));

        if (OtherConfigsMap.getInstance().isFacingDirectionEnabled()) {
            if (direction.in(Orientation.LAYER.MIDDLE)) {
                MainClass.speakWithNarrator(PlayerPositionUtils.getHorizontalFacingDirectionInWords(), true);
            } else {
                MainClass.speakWithNarrator(PlayerPositionUtils.getVerticalFacingDirectionInWords(), true);
            }
        }
    }

    /**
     * Snaps the camera to the closest cardinal direction and centers it vertically.
     *
     * @param lookOpposite Whether to snap the opposite cardinal direction or not and centers it.
     */
    private void centerCamera(boolean lookOpposite) {
        if (minecraftClient.player == null) return;
        Orientation o = PlayerPositionUtils.getHorizontalFacing();
        rotateCameraTo(lookOpposite ? o.getOpposite() : o);
    }
}
