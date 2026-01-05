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
                "key.minecraft_access.camera_controls.up",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_I,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_RIGHT(
                "key.minecraft_access.camera_controls.right",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_L,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_DOWN(
                "key.minecraft_access.camera_controls.down",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_K,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_LEFT(
                "key.minecraft_access.camera_controls.left",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_J,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_ALTERNATE_UP(
                "key.minecraft_access.camera_controls.alternate_up",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD8,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_ALTERNATE_RIGHT(
                "key.minecraft_access.camera_controls.alternate_right",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD6,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_ALTERNATE_DOWN(
                "key.minecraft_access.camera_controls.alternate_down",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD2,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_ALTERNATE_LEFT(
                "key.minecraft_access.camera_controls.alternate_left",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD4,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_NORTH(
                "key.minecraft_access.camera_controls.north",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD7,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_EAST(
                "key.minecraft_access.camera_controls.east",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD9,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_WEST(
                "key.minecraft_access.camera_controls.west",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD1,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_SOUTH(
                "key.minecraft_access.camera_controls.south",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD3,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_CENTER_CAMERA(
                "key.minecraft_access.camera_controls.center_camera",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD5,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_STRAIGHT_UP(
                "key.minecraft_access.camera_controls.straight_up_camera",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD0,
                Categories.CAMERA_CONTROLS
        ),
        CAMERA_CONTROLS_STRAIGHT_DOWN(
                "key.minecraft_access.camera_controls.straight_down_camera",
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
