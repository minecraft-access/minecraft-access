package org.mcaccess.minecraftaccess.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

import org.mcaccess.minecraftaccess.MainClass;

/**
 * Initializes all the keybindings used by the mod.
 */
public final class KeyMappingsHandler {
    private KeyMappingsHandler() {
    }

    public enum Categories {
        CAMERA_CONTROLS("old_camera_controls");

        public final KeyMapping.Category category;

        Categories(String name) {
            category = KeyMapping.Category.register(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, name));
        }
    }

    public enum Keys {
        CAMERA_CONTROLS_UP(
                "minecraft_access.keys.camera_controls.up_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_I,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_RIGHT(
                "minecraft_access.keys.camera_controls.right_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_L,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_DOWN(
                "minecraft_access.keys.camera_controls.down_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_K,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_LEFT(
                "minecraft_access.keys.camera_controls.left_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_J,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_ALTERNATE_UP(
                "minecraft_access.keys.camera_controls.alternate_up_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD8,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_ALTERNATE_RIGHT(
                "minecraft_access.keys.camera_controls.alternate_right_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD6,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_ALTERNATE_DOWN(
                "minecraft_access.keys.camera_controls.alternate_down_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD2,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_ALTERNATE_LEFT(
                "minecraft_access.keys.camera_controls.alternate_left_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD4,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_NORTH(
                "minecraft_access.keys.camera_controls.north_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD7,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_EAST(
                "minecraft_access.keys.camera_controls.east_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD9,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_WEST(
                "minecraft_access.keys.camera_controls.west_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD1,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_SOUTH(
                "minecraft_access.keys.camera_controls.south_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD3,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_CENTER_CAMERA(
                "minecraft_access.keys.camera_controls.center_camera_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD5,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_STRAIGHT_UP(
                "minecraft_access.keys.camera_controls.straight_up_camera_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD0,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_STRAIGHT_DOWN(
                "minecraft_access.keys.camera_controls.straight_down_camera_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPADCOMMA,
                Categories.CAMERA_CONTROLS
        );

        public final KeyMapping mapping;

        Keys(String name, InputConstants.Type type, int keyCode, Categories category) {
            mapping = new KeyMapping(name, type, keyCode, category.category);
        }
    }
}
