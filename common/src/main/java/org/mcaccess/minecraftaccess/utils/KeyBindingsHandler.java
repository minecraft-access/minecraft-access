package org.mcaccess.minecraftaccess.utils;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;

import java.util.Set;

/**
 * Initializes all the keybindings used by the mod.
 */
public class KeyBindingsHandler {
    public static final KeyMapping speakPlayerStatusKey;
    public static final KeyMapping lockingHandlerKey;
    public static final KeyMapping positionNarrationKey;
    public static final KeyMapping accessMenuKey;
    public static final KeyMapping narrateTarget;
    public static final KeyMapping targetPosition;
    public static final KeyMapping lightLevel;
    public static final KeyMapping closestWaterSource;
    public static final KeyMapping closestLavaSource;
    public static final KeyMapping currentBiome;
    public static final KeyMapping timeOfDay;
    public static final KeyMapping xpLevel;
    public static final KeyMapping refreshScreenReader;
    public static final KeyMapping openConfigMenu;
    public static final KeyMapping directionNarrationKey;
    public static final KeyMapping narrateHeldItemKey;

    public static final KeyMapping cameraControlsUp;
    public static final KeyMapping cameraControlsRight;
    public static final KeyMapping cameraControlsDown;
    public static final KeyMapping cameraControlsLeft;
    public static final KeyMapping cameraControlsAlternateUp;
    public static final KeyMapping cameraControlsAlternateRight;
    public static final KeyMapping cameraControlsAlternateDown;
    public static final KeyMapping cameraControlsAlternateLeft;
    public static final KeyMapping cameraControlsNorth;
    public static final KeyMapping cameraControlsEast;
    public static final KeyMapping cameraControlsWest;
    public static final KeyMapping cameraControlsSouth;
    public static final KeyMapping cameraControlsCenterCamera;
    public static final KeyMapping cameraControlsStraightUp;
    public static final KeyMapping cameraControlsStraightDown;

    public static final KeyMapping inventoryControlsGroupKey;
    public static final KeyMapping inventoryControlsUpKey;
    public static final KeyMapping inventoryControlsRightKey;
    public static final KeyMapping inventoryControlsDownKey;
    public static final KeyMapping inventoryControlsLeftKey;
    public static final KeyMapping inventoryControlsSwitchTabKey;
    public static final KeyMapping inventoryControlsToggleCraftableKey;

    public static final KeyMapping mouseSimulationLeftMouseKey;
    public static final KeyMapping mouseSimulationRightMouseKey;
    public static final KeyMapping mouseSimulationMiddleMouseKey;
    public static final KeyMapping mouseSimulationScrollUpKey;
    public static final KeyMapping mouseSimulationScrollDownKey;

    public static final KeyMapping objectTrackerNextItem;
    public static final KeyMapping objectTrackerPreviousItem;
    public static final KeyMapping objectTrackerNarrateCurrentObject;
    public static final KeyMapping targetNearestObject;

    private static final String OTHER_GROUP_TRANSLATION_KEY = "minecraft_access.keys.other.group_name";
    private static final String CAMERA_CONTROLS_TRANSLATION_KEY = "minecraft_access.keys.camera_controls.group_name";
    private static final String INVENTORY_CONTROLS_TRANSLATION_KEY = "minecraft_access.keys.inventory_controls.group_name";
    private static final String MOUSE_SIMULATION_KEY = "minecraft_access.keys.mouse_simulation.group_name";
    private static final String OBJECT_TRACKER_KEY = "minecraft_access.keys.object_tracker.group_name";

    private KeyBindingsHandler() {
    }

    public static KeyBindingsHandler getInstance() {
        return new KeyBindingsHandler();
    }

    static {
        inventoryControlsGroupKey = new KeyMapping(
                "minecraft_access.keys.inventory_controls.change_group_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_C,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsUpKey = new KeyMapping(
                "minecraft_access.keys.inventory_controls.up_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_I,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsRightKey = new KeyMapping(
                "minecraft_access.keys.inventory_controls.right_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_L,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsDownKey = new KeyMapping(
                "minecraft_access.keys.inventory_controls.down_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_K,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsLeftKey = new KeyMapping(
                "minecraft_access.keys.inventory_controls.left_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_J,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsSwitchTabKey = new KeyMapping(
                "minecraft_access.keys.inventory_controls.switch_tabs_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_V,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsToggleCraftableKey = new KeyMapping(
                "minecraft_access.keys.inventory_controls.toggle_craftable_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_R,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsUp = new KeyMapping(
                "minecraft_access.keys.camera_controls.up_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_I,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsRight = new KeyMapping(
                "minecraft_access.keys.camera_controls.right_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_L,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsDown = new KeyMapping(
                "minecraft_access.keys.camera_controls.down_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_K,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsLeft = new KeyMapping(
                "minecraft_access.keys.camera_controls.left_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_J,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsAlternateUp = new KeyMapping(
                "minecraft_access.keys.camera_controls.alternate_up_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD8,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsAlternateRight = new KeyMapping(
                "minecraft_access.keys.camera_controls.alternate_right_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD6,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsAlternateDown = new KeyMapping(
                "minecraft_access.keys.camera_controls.alternate_down_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD2,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsAlternateLeft = new KeyMapping(
                "minecraft_access.keys.camera_controls.alternate_left_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD4,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsNorth = new KeyMapping(
                "minecraft_access.keys.camera_controls.north_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD7,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsEast = new KeyMapping(
                "minecraft_access.keys.camera_controls.east_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD9,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsWest = new KeyMapping(
                "minecraft_access.keys.camera_controls.west_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD1,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsSouth = new KeyMapping(
                "minecraft_access.keys.camera_controls.south_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD3,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsStraightUp = new KeyMapping(
                "minecraft_access.keys.camera_controls.straight_up_camera_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD0,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsStraightDown = new KeyMapping(
                "minecraft_access.keys.camera_controls.straight_down_camera_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPADCOMMA,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsCenterCamera = new KeyMapping(
                "minecraft_access.keys.camera_controls.center_camera_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_NUMPAD5,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        mouseSimulationLeftMouseKey = new KeyMapping(
                "minecraft_access.keys.mouse_simulation.left_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_LBRACKET,
                MOUSE_SIMULATION_KEY
        );

        mouseSimulationRightMouseKey = new KeyMapping(
                "minecraft_access.keys.mouse_simulation.right_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_RBRACKET,
                MOUSE_SIMULATION_KEY
        );

        mouseSimulationMiddleMouseKey = new KeyMapping(
                "minecraft_access.keys.mouse_simulation.middle_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_BACKSLASH,
                MOUSE_SIMULATION_KEY
        );

        mouseSimulationScrollUpKey = new KeyMapping(
                "minecraft_access.keys.mouse_simulation.scroll_up_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_SEMICOLON,
                MOUSE_SIMULATION_KEY
        );

        mouseSimulationScrollDownKey = new KeyMapping(
                "minecraft_access.keys.mouse_simulation.scroll_down_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_APOSTROPHE,
                MOUSE_SIMULATION_KEY
        );

        speakPlayerStatusKey = new KeyMapping(
                "minecraft_access.keys.other.player_status_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_R,
                OTHER_GROUP_TRANSLATION_KEY
        );

        lockingHandlerKey = new KeyMapping(
                "minecraft_access.keys.other.locking_handler_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_Y,
                OTHER_GROUP_TRANSLATION_KEY
        );

        positionNarrationKey = new KeyMapping(
                "minecraft_access.keys.other.player_position_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_G,
                OTHER_GROUP_TRANSLATION_KEY
        );

        accessMenuKey = new KeyMapping(
                "minecraft_access.keys.other.access_menu_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_F4,
                OTHER_GROUP_TRANSLATION_KEY
        );

        narrateTarget = new KeyMapping(
                "minecraft_access.access_menu.gui.button.block_and_fluid_target_info",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_B,
                OTHER_GROUP_TRANSLATION_KEY
        );

        targetPosition = new KeyMapping(
                "minecraft_access.access_menu.gui.button.block_and_fluid_target_position",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        lightLevel = new KeyMapping(
                "minecraft_access.access_menu.gui.button.light_level",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        closestWaterSource = new KeyMapping(
                "minecraft_access.access_menu.gui.button.find_water",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        closestLavaSource = new KeyMapping(
                "minecraft_access.access_menu.gui.button.find_lava",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        currentBiome = new KeyMapping(
                "minecraft_access.access_menu.gui.button.biome",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        timeOfDay = new KeyMapping(
                "minecraft_access.access_menu.gui.button.time_of_day",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        xpLevel = new KeyMapping(
                "minecraft_access.access_menu.gui.button.xp",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        refreshScreenReader = new KeyMapping(
                "minecraft_access.access_menu.gui.button.refresh_screen_reader",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        openConfigMenu = new KeyMapping(
                "minecraft_access.access_menu.gui.button.open_config_menu",
                InputConstants.Type.KEYSYM,
                InputConstants.UNKNOWN.getValue(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        directionNarrationKey = new KeyMapping(
                "minecraft_access.keys.other.facing_direction_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_H,
                OTHER_GROUP_TRANSLATION_KEY
        );

        narrateHeldItemKey = new KeyMapping(
                "minecraft_access.keys.other.narrate_held_item_key_name",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_GRAVE,
                OTHER_GROUP_TRANSLATION_KEY
        );

        objectTrackerNextItem = new KeyMapping(
                "minecraft_access.keys.object_tracker.next_item",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_PAGEDOWN,
                OBJECT_TRACKER_KEY
        );

        objectTrackerPreviousItem = new KeyMapping(
                "minecraft_access.keys.object_tracker.previous_item",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_PAGEUP,
                OBJECT_TRACKER_KEY
        );

        objectTrackerNarrateCurrentObject = new KeyMapping(
                "minecraft_access.keys.object_tracker.narrate_current_object",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_HOME,
                OBJECT_TRACKER_KEY
        );

        targetNearestObject = new KeyMapping(
                "minecraft_access.keys.object_tracker.target_nearest",
                InputConstants.Type.KEYSYM,
                InputConstants.KEY_END,
                OBJECT_TRACKER_KEY
        );
    }

    public Set<KeyMapping> getKeys() {
        return Set.of(inventoryControlsGroupKey,
                inventoryControlsUpKey,
                inventoryControlsRightKey,
                inventoryControlsDownKey,
                inventoryControlsLeftKey,
                inventoryControlsSwitchTabKey,
                inventoryControlsToggleCraftableKey,
                cameraControlsUp,
                cameraControlsRight,
                cameraControlsDown,
                cameraControlsLeft,
                cameraControlsAlternateUp,
                cameraControlsAlternateRight,
                cameraControlsAlternateDown,
                cameraControlsAlternateLeft,
                cameraControlsNorth,
                cameraControlsEast,
                cameraControlsWest,
                cameraControlsSouth,
                cameraControlsStraightUp,
                cameraControlsStraightDown,
                cameraControlsCenterCamera,
                mouseSimulationLeftMouseKey,
                mouseSimulationRightMouseKey,
                mouseSimulationMiddleMouseKey,
                mouseSimulationScrollUpKey,
                mouseSimulationScrollDownKey,
                speakPlayerStatusKey,
                lockingHandlerKey,
                positionNarrationKey,
                accessMenuKey,
                narrateTarget,
                targetPosition,
                lightLevel,
                closestWaterSource,
                closestLavaSource,
                currentBiome,
                timeOfDay,
                xpLevel,
                refreshScreenReader,
                openConfigMenu,
                directionNarrationKey,
                narrateHeldItemKey,
                objectTrackerNextItem,
                objectTrackerPreviousItem,
                objectTrackerNarrateCurrentObject,
                targetNearestObject
        );
    }
}
