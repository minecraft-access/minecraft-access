package com.github.khanshoaib3.minecraft_access.features;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.CameraControlsConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.OtherConfigsMap;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.WorldUtils;
import com.github.khanshoaib3.minecraft_access.utils.condition.DoubleClick;
import com.github.khanshoaib3.minecraft_access.utils.condition.Interval;
import com.github.khanshoaib3.minecraft_access.utils.position.Orientation;
import com.github.khanshoaib3.minecraft_access.utils.position.PlayerPositionUtils;
import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.util.math.Vec3d;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class CameraControls {

    private static final Logger log = LoggerFactory.getLogger(CameraControls.class);

    private static boolean enabled;
    private static float normalRotatingDeltaAngle;
    private static float modifiedRotatingDeltaAngle;
    private static final Interval interval = Interval.defaultDelay();

    private static final DoubleClick straightUpDoubleClick;
    private static final DoubleClick straightDownDoubleClick;

    static {
        // config keystroke conditions
        straightUpDoubleClick = new DoubleClick(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().cameraControlsUp));
        straightDownDoubleClick = new DoubleClick(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().cameraControlsDown));
    }

    public static void update() {
        if (!interval.isReady()) return;
        loadConfigurations();
        if (!enabled) return;
        keyListener();
    }

    /**
     * Loads the configs from config.json
     */
    private static void loadConfigurations() {
        CameraControlsConfigMap map = CameraControlsConfigMap.getInstance();
        enabled = map.isEnabled();
        interval.setDelay(map.getDelayInMilliseconds(), Interval.Unit.Millisecond);

        float delta90Degrees = 600f; // 90 / 0.15
        normalRotatingDeltaAngle = delta90Degrees / (90 / map.getNormalRotatingAngle());
        modifiedRotatingDeltaAngle = delta90Degrees / (90 / map.getModifiedRotatingAngle());
    }

    /**
     * Handles the key inputs
     */
    private static void keyListener() {
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

        boolean anyFunctionTriggered = false;

        // these two blocks of logic should be ahead of the normal up/down logic
        if (isStraightUpKeyPressed || isUpKeyDoublePressedWithRightAlt) {
            anyFunctionTriggered = true;
            rotateCameraTo(Orientation.UP);
        }

        if (isStraightDownKeyPressed || isDownKeyDoublePressedWithRightAlt) {
            anyFunctionTriggered = true;
            rotateCameraTo(Orientation.DOWN);
        }

        if (isNorthKeyPressed) {
            anyFunctionTriggered = true;
            rotateCameraTo(Orientation.NORTH);
        }

        if (isEastKeyPressed) {
            anyFunctionTriggered = true;
            rotateCameraTo(Orientation.EAST);
        }

        if (isWestKeyPressed) {
            anyFunctionTriggered = true;
            rotateCameraTo(Orientation.WEST);
        }

        if (isSouthKeyPressed) {
            anyFunctionTriggered = true;
            rotateCameraTo(Orientation.SOUTH);
        }

        float rotateAngle = isLeftAltPressed ? modifiedRotatingDeltaAngle : normalRotatingDeltaAngle;

        if (isUpKeyPressed) {
            anyFunctionTriggered = true;
            rotateCameraBy(rotateAngle, RotatingDirection.UP);
        }

        if (isRightKeyPressed) {
            anyFunctionTriggered = true;
            rotateCameraBy(rotateAngle, RotatingDirection.RIGHT);
        }

        if (isDownKeyPressed) {
            anyFunctionTriggered = true;
            rotateCameraBy(rotateAngle, RotatingDirection.DOWN);
        }

        if (isLeftKeyPressed) {
            anyFunctionTriggered = true;
            rotateCameraBy(rotateAngle, RotatingDirection.LEFT);
        }

        if (isCenterCameraKeyPressed) {
            anyFunctionTriggered = true;
            centerCamera(isLeftAltPressed);
        }

        interval.adjustNextReadyTimeBy(anyFunctionTriggered);
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
    private static void rotateCameraBy(float angle, RotatingDirection direction) {
        float horizontalAngleDelta = angle * direction.horizontalWight;
        float verticalAngleDelta = angle * direction.verticalWight;
        log.debug("Rotating camera by x:{} y:{}", horizontalAngleDelta, verticalAngleDelta);

        WorldUtils.getClientPlayer().changeLookDirection(horizontalAngleDelta, verticalAngleDelta);

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
    private static void rotateCameraTo(Orientation direction) {
        ClientPlayerEntity player = WorldUtils.getClientPlayer();
        Vec3d playerBlockPosition = player.getPos();
        Vec3d targetBlockPosition = playerBlockPosition.add(Vec3d.of(direction.vector));
        player.lookAt(EntityAnchorArgumentType.EntityAnchor.FEET, targetBlockPosition);

        log.debug("Rotating camera to: {}", direction.name());

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
    private static void centerCamera(boolean lookOpposite) {
        Orientation o = PlayerPositionUtils.getHorizontalFacing();
        rotateCameraTo(lookOpposite ? o.getOpposite() : o);
    }
}
