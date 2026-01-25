package org.mcaccess.minecraftaccess.features;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.position.Orientation;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;

/**
 * This feature adds key binds to control the camera.
 */
@Slf4j
public class CameraControls implements BalmClientModule {
    private static final Config.CameraControls CONFIG = Config.getInstance().cameraControls;
    private static final float DEGREES_PER_MOUSE_DELTA = 0.15f;

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls");
    }

    @Override
    public void initialize() {
        // narrate_facing_direction keys
        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.narrate_facing_direction/horizontal"))
                .withDefault(InputBinding.key(InputConstants.KEY_H))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    MainClass.narrate(I18n.get("minecraft_access.other.facing_direction", PlayerPositionUtils.getHorizontalFacingDirectionInWords()), true);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.narrate_facing_direction/vertical"))
                .withDefault(InputBinding.key(InputConstants.KEY_H, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    MainClass.narrate(I18n.get("minecraft_access.other.facing_direction", PlayerPositionUtils.getVerticalFacingDirectionInWords()), true);
                    return true;
                })
                .build();

        // look keys
        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look/up"))
                .withDefault(InputBinding.key(InputConstants.KEY_I))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraBy(false, RotatingDirection.UP);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look/left"))
                .withDefault(InputBinding.key(InputConstants.KEY_J))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraBy(false, RotatingDirection.LEFT);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look/down"))
                .withDefault(InputBinding.key(InputConstants.KEY_K))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraBy(false, RotatingDirection.DOWN);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look/right"))
                .withDefault(InputBinding.key(InputConstants.KEY_L))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraBy(false, RotatingDirection.RIGHT);
                    return true;
                })
                .build();

        // look_modified keys
        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_modified/up"))
                .withDefault(InputBinding.key(InputConstants.KEY_I, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraBy(true, RotatingDirection.UP);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_modified/left"))
                .withDefault(InputBinding.key(InputConstants.KEY_J, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraBy(true, RotatingDirection.LEFT);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_modified/down"))
                .withDefault(InputBinding.key(InputConstants.KEY_K, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraBy(true, RotatingDirection.DOWN);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_modified/right"))
                .withDefault(InputBinding.key(InputConstants.KEY_L, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraBy(true, RotatingDirection.RIGHT);
                    return true;
                })
                .build();

        // look_direction keys
        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_direction/north"))
                .withDefault(InputBinding.key(InputConstants.KEY_I, KeyModifiers.of(KeyModifier.CONTROL)))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraTo(Orientation.NORTH);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_direction/east"))
                .withDefault(InputBinding.key(InputConstants.KEY_L, KeyModifiers.of(KeyModifier.CONTROL)))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraTo(Orientation.EAST);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_direction/south"))
                .withDefault(InputBinding.key(InputConstants.KEY_K, KeyModifiers.of(KeyModifier.CONTROL)))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraTo(Orientation.SOUTH);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_direction/west"))
                .withDefault(InputBinding.key(InputConstants.KEY_J, KeyModifiers.of(KeyModifier.CONTROL)))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraTo(Orientation.WEST);
                    return true;
                })
                .build();

        // look_straight keys
        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_straight/forward"))
                .withDefault(InputBinding.key(InputConstants.KEY_COMMA))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    centerCamera(false);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_straight/behind"))
                .withDefault(InputBinding.key(InputConstants.KEY_COMMA, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    centerCamera(true);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_straight/down"))
                .withDefault(InputBinding.key(InputConstants.KEY_PERIOD))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraTo(Orientation.DOWN);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "camera_controls.look_straight/up"))
                .withDefault(InputBinding.key(InputConstants.KEY_PERIOD, KeyModifiers.of(KeyModifier.ALT)))
                .overrideCategory(KeyMappingCategories.CAMERA_CONTROLS)
                .handleWorldInput(event -> {
                    rotateCameraTo(Orientation.UP);
                    return true;
                })
                .build();
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
            isRotatingHorizontal = horizontalWight != 0;
        }
    }

    /**
     * Rotates the player's camera.
     *
     * @param isModified Whether to use normal rotating angle or modified angle
     * @param direction on given direction
     */
    private static void rotateCameraBy(boolean isModified, RotatingDirection direction) {
        float angle = (isModified ? CONFIG.modifiedRotatingAngle : CONFIG.normalRotatingAngle) / DEGREES_PER_MOUSE_DELTA;

        if (handleLocking()) return;
        float horizontalAngleDelta = angle * direction.horizontalWight;
        float verticalAngleDelta = angle * direction.verticalWight;
        log.debug("Rotating camera by x:{} y:{}", horizontalAngleDelta, verticalAngleDelta);

        assert Minecraft.getInstance().player != null;
        Minecraft.getInstance().player.turn(horizontalAngleDelta, verticalAngleDelta);

        String horizontalDirection = PlayerPositionUtils.getHorizontalFacingDirectionInWords();
        String verticalDirection = PlayerPositionUtils.getVerticalFacingDirectionInWords();
        if (Config.getInstance().features.facingDirectionEnabled) {
            if (direction.isRotatingHorizontal) {
                MainClass.narrate(horizontalDirection, true);
            } else if (verticalDirection != null) {
                MainClass.narrate(verticalDirection, true);
            }
        }
    }

    /**
     * Move the camera (player's view).
     *
     * @param direction to given direction
     */
    private static void rotateCameraTo(Orientation direction) {
        if (handleLocking()) return;
        LocalPlayer player = Minecraft.getInstance().player;
        assert player != null;
        Vec3 playerBlockPosition = player.position();
        Vec3 targetBlockPosition = playerBlockPosition.add(Vec3.atLowerCornerOf(direction.vector));
        player.lookAt(EntityAnchorArgument.Anchor.FEET, targetBlockPosition);

        log.debug("Rotating camera to: {}", direction.name());

        if (Config.getInstance().features.facingDirectionEnabled) {
            if (direction.in(Orientation.Layer.MIDDLE)) {
                MainClass.narrate(PlayerPositionUtils.getHorizontalFacingDirectionInWords(), true);
            } else {
                MainClass.narrate(PlayerPositionUtils.getVerticalFacingDirectionInWords(), true);
            }
        }
    }

    /**
     * Snaps the camera to the closest cardinal direction and centers it vertically.
     *
     * @param lookOpposite Whether to snap the opposite cardinal direction or not and centers it.
     */
    private static void centerCamera(boolean lookOpposite) {
        if (handleLocking()) return;
        Orientation o = PlayerPositionUtils.getHorizontalFacing();
        rotateCameraTo(lookOpposite ? o.getOpposite() : o);
    }

    private static boolean handleLocking() {
        assert Minecraft.getInstance().getCameraEntity() != null;
        assert Minecraft.getInstance().player != null;
        if (!(MainClass.poiManager.lockingHandler.isPlayerLocked() || !Minecraft.getInstance().getCameraEntity().is(Minecraft.getInstance().player))) {
            return false;
        }
        MainClass.narrate(I18n.get("minecraft_access.other.camera_locked"), true);
        return true;
    }
}
