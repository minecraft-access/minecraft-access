package com.github.khanshoaib3.minecraft_access.utils;

import lombok.Getter;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

/**
 * Initializes all the keybindings used in the mod.
 */
public class KeyBindingsHandler {
    public KeyBinding healthNHungerNarrationKey;
    public KeyBinding lockingHandlerKey;
    public KeyBinding positionNarrationKey;
    public KeyBinding accessMenuKey;
    public KeyBinding narrateTarget;
    public KeyBinding targetPosition;
    public KeyBinding lightLevel;
    public KeyBinding closestWaterSource;
    public KeyBinding closestLavaSource;
    public KeyBinding currentBiome;
    public KeyBinding timeOfDay;
    public KeyBinding xpLevel;
    public KeyBinding refreshScreenReader;
    public KeyBinding openConfigMenu;
    public KeyBinding directionNarrationKey;

    public KeyBinding cameraControlsUp;
    public KeyBinding cameraControlsRight;
    public KeyBinding cameraControlsDown;
    public KeyBinding cameraControlsLeft;
    public KeyBinding cameraControlsAlternateUp;
    public KeyBinding cameraControlsAlternateRight;
    public KeyBinding cameraControlsAlternateDown;
    public KeyBinding cameraControlsAlternateLeft;
    public KeyBinding cameraControlsNorth;
    public KeyBinding cameraControlsEast;
    public KeyBinding cameraControlsWest;
    public KeyBinding cameraControlsSouth;
    public KeyBinding cameraControlsCenterCamera;
    public KeyBinding cameraControlsStraightUp;
    public KeyBinding cameraControlsStraightDown;

    public KeyBinding inventoryControlsGroupKey;
    public KeyBinding inventoryControlsUpKey;
    public KeyBinding inventoryControlsRightKey;
    public KeyBinding inventoryControlsDownKey;
    public KeyBinding inventoryControlsLeftKey;
    public KeyBinding inventoryControlsSwitchTabKey;
    public KeyBinding inventoryControlsToggleCraftableKey;

    public KeyBinding mouseSimulationLeftMouseKey;
    public KeyBinding mouseSimulationRightMouseKey;
    public KeyBinding mouseSimulationMiddleMouseKey;
    public KeyBinding mouseSimulationScrollUpKey;
    public KeyBinding mouseSimulationScrollDownKey;

    public KeyBinding areaMapMenuKey;
    public KeyBinding areaMapNorthKey;
    public KeyBinding areaMapSouthKey;
    public KeyBinding areaMapWestKey;
    public KeyBinding areaMapEastKey;
    public KeyBinding areaMapUpKey;
    public KeyBinding areaMapDownKey;
    public KeyBinding areaMapCursorResetKey;
    public KeyBinding areaMapMapLockKey;

    private static final String OTHER_GROUP_TRANSLATION_KEY = "minecraft_access.keys.other.group_name";
    private static final String CAMERA_CONTROLS_TRANSLATION_KEY = "minecraft_access.keys.camera_controls.group_name";
    private static final String INVENTORY_CONTROLS_TRANSLATION_KEY = "minecraft_access.keys.inventory_controls.group_name";
    private static final String MOUSE_SIMULATION_KEY = "minecraft_access.keys.mouse_simulation.group_name";
    private static final String AREA_MAP_GROUP_KEY = "minecraft_access.keys.area_map.group_name";

    @Getter
    private static final KeyBindingsHandler instance;

    /**
     * Constructor that initializes all the keybindings.
     */
    private KeyBindingsHandler() {
        initializeCameraControlsKeybindings();
        initializeInventoryControlsKeybindings();
        initializeMouseSimulationKeybindings();
//        initializeAreaMapKeybindings();
        initializeOtherKeybindings();
    }

    static {
        try {
            instance = new KeyBindingsHandler();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating KeyBindingsHandler instance", e);
        }
    }

    /**
     * Initializes keybindings related to inventory controls.<br><br>
     * Related key binds and combinations:-<br>
     * 1) Up Key (default: I) = Focus to slot above.<br>
     * 2) Right Key (default: L) = Focus to slot right.<br>
     * 3) Down Key (default: K) = Focus to slot down.<br>
     * 4) Left Key (default: J) = Focus to slot left.<br>
     * 5) Group Key (default: C) = Select next group.<br>
     * 6) Left Shift + Group Key = Select previous group.<br>
     * 7) Switch Tab Key (default: V) = Select next tab (only for creative inventory screen and inventory/crafting screen).<br>
     * 8) Left Shift + Switch Tab Key = Select previous tab (only for creative inventory screen and inventory/crafting screen).<br>
     * 9) Toggle Craftable Key (default: R) = Toggle between show all and show only craftable recipes in inventory/crafting screen.<br>
     * 10) T Key (not re-mappable) = Select the search box.<br>
     * 11) Enter Key (not re-mappable) = Deselect the search box.<br>
     */
    private void initializeInventoryControlsKeybindings() {
        inventoryControlsGroupKey = new KeyBinding(
                "minecraft_access.keys.inventory_controls.change_group_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsUpKey = new KeyBinding(
                "minecraft_access.keys.inventory_controls.up_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsRightKey = new KeyBinding(
                "minecraft_access.keys.inventory_controls.right_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsDownKey = new KeyBinding(
                "minecraft_access.keys.inventory_controls.down_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsLeftKey = new KeyBinding(
                "minecraft_access.keys.inventory_controls.left_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsSwitchTabKey = new KeyBinding(
                "minecraft_access.keys.inventory_controls.switch_tabs_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );

        inventoryControlsToggleCraftableKey = new KeyBinding(
                "minecraft_access.keys.inventory_controls.toggle_craftable_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                INVENTORY_CONTROLS_TRANSLATION_KEY
        );
    }

    /**
     * Initializes keybindings related to camera controls.<br><br>
     * Related key binds and combinations:-<br>
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
    private void initializeCameraControlsKeybindings() {
        cameraControlsUp = new KeyBinding(
                "minecraft_access.keys.camera_controls.up_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsRight = new KeyBinding(
                "minecraft_access.keys.camera_controls.right_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsDown = new KeyBinding(
                "minecraft_access.keys.camera_controls.down_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsLeft = new KeyBinding(
                "minecraft_access.keys.camera_controls.left_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsAlternateUp = new KeyBinding(
                "minecraft_access.keys.camera_controls.alternate_up_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_8,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsAlternateRight = new KeyBinding(
                "minecraft_access.keys.camera_controls.alternate_right_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_6,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsAlternateDown = new KeyBinding(
                "minecraft_access.keys.camera_controls.alternate_down_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_2,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsAlternateLeft = new KeyBinding(
                "minecraft_access.keys.camera_controls.alternate_left_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_4,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsNorth = new KeyBinding(
                "minecraft_access.keys.camera_controls.north_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_7,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsEast = new KeyBinding(
                "minecraft_access.keys.camera_controls.east_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_9,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsWest = new KeyBinding(
                "minecraft_access.keys.camera_controls.west_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_1,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsSouth = new KeyBinding(
                "minecraft_access.keys.camera_controls.south_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_3,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsStraightUp = new KeyBinding(
                "minecraft_access.keys.camera_controls.straight_up_camera_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_0,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsStraightDown = new KeyBinding(
                "minecraft_access.keys.camera_controls.straight_down_camera_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_DECIMAL,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );

        cameraControlsCenterCamera = new KeyBinding(
                "minecraft_access.keys.camera_controls.center_camera_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_5,
                CAMERA_CONTROLS_TRANSLATION_KEY
        );
    }

    /**
     * Initializes all mouse simulation keybindings.<br><br>
     * Related key binds and combinations:-<br>
     * 1) Left Mouse Sim Key (default: [) = Simulates left mouse key.<br>
     * 2) Right Mouse Sim Key (default: ]) = Simulates right mouse key.<br>
     * 3) Mouse Wheel Scroll Up (default: \) = Simulates middle mouse key.<br>
     * 4) Mouse Wheel Scroll Up (default: ;) = Simulates mouse wheel scroll up.<br>
     * 5) Mouse Wheel Scroll Down (default: ') = Simulates mouse wheel scroll down.\
     */
    private void initializeMouseSimulationKeybindings() {
        mouseSimulationLeftMouseKey = new KeyBinding(
                "minecraft_access.keys.mouse_simulation.left_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_BRACKET,
                MOUSE_SIMULATION_KEY
        );

        mouseSimulationRightMouseKey = new KeyBinding(
                "minecraft_access.keys.mouse_simulation.right_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_BRACKET,
                MOUSE_SIMULATION_KEY
        );

        mouseSimulationMiddleMouseKey = new KeyBinding(
                "minecraft_access.keys.mouse_simulation.middle_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_BACKSLASH,
                MOUSE_SIMULATION_KEY
        );

        mouseSimulationScrollUpKey = new KeyBinding(
                "minecraft_access.keys.mouse_simulation.scroll_up_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_SEMICOLON,
                MOUSE_SIMULATION_KEY
        );

        mouseSimulationScrollDownKey = new KeyBinding(
                "minecraft_access.keys.mouse_simulation.scroll_down_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_APOSTROPHE,
                MOUSE_SIMULATION_KEY
        );
    }

    /**
     * 1. Open Area Map Menu Key (default: F6) = Opens the area map menu.
     * 2. Cursor North Key (default: I) = Move the map cursor one block north.
     * 3. Cursor South Key (default: K) = Move the map cursor one block south.
     * 4. Cursor West Key (default: J) = Moves the map cursor one block west.
     * 5. Cursor East Key (default: L) = Moves the map cursor one block east.
     * 6. Cursor Up Key (default: U) = Moves the map cursor one block up.
     * 7. Cursor Down Key (default: O) = Moves the map cursor one block down.
     */
    @SuppressWarnings("unused")
    private void initializeAreaMapKeybindings() {
        areaMapMenuKey = new KeyBinding(
                "minecraft_access.keys.area_map.menu_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F6,
                AREA_MAP_GROUP_KEY
        );

        areaMapNorthKey = new KeyBinding(
                "minecraft_access.keys.area_map.north_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_I,
                AREA_MAP_GROUP_KEY
        );

        areaMapSouthKey = new KeyBinding(
                "minecraft_access.keys.area_map.south_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                AREA_MAP_GROUP_KEY
        );

        areaMapWestKey = new KeyBinding(
                "minecraft_access.keys.area_map.west_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_J,
                AREA_MAP_GROUP_KEY
        );

        areaMapEastKey = new KeyBinding(
                "minecraft_access.keys.area_map.east_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_L,
                AREA_MAP_GROUP_KEY
        );

        areaMapUpKey = new KeyBinding(
                "minecraft_access.keys.area_map.up_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                AREA_MAP_GROUP_KEY
        );

        areaMapDownKey = new KeyBinding(
                "minecraft_access.keys.area_map.down_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                AREA_MAP_GROUP_KEY
        );

        areaMapCursorResetKey = new KeyBinding(
                "minecraft_access.keys.area_map.cursor_reset_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                AREA_MAP_GROUP_KEY
        );

        areaMapMapLockKey = new KeyBinding(
                "minecraft_access.keys.area_map.map_lock_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Y,
                AREA_MAP_GROUP_KEY
        );
    }

    /**
     * Initializes all the other keybindings.<br><br>
     * Related key binds and combinations:-<br>
     * 1) Locking Key (default: Y) = Locks onto the nearest entity or block<br>
     * 2) Alt key + Locking Key = Unlocks from the currently locked entity or block<br>
     * 3) Speak Facing Direction Key (default: H) = Speaks the player facing direction.
     * 4) Speak Player Health and Hunger Key (default: R) = Speaks the health and hunger.
     * 5) Access Menu Key (default: F4) = Opens a menu with few options.
     * 6) Speak Player Position Key (default: G) = Speaks the player's x y and z position.<br>
     * 7) Left Alt + X = Speaks only the x position.<br>
     * 8) Left Alt + C = Speaks only the y position.<br>
     * 9) Left Alt + Z = Speaks only the z position.<br>
     */
    private void initializeOtherKeybindings() {
        healthNHungerNarrationKey = new KeyBinding(
                "minecraft_access.keys.other.health_n_hunger_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                OTHER_GROUP_TRANSLATION_KEY
        );

        lockingHandlerKey = new KeyBinding(
                "minecraft_access.keys.other.locking_handler_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Y,
                OTHER_GROUP_TRANSLATION_KEY
        );

        positionNarrationKey = new KeyBinding(
                "minecraft_access.keys.other.player_position_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                OTHER_GROUP_TRANSLATION_KEY
        );

        accessMenuKey = new KeyBinding(
                "minecraft_access.keys.other.access_menu_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F4,
                OTHER_GROUP_TRANSLATION_KEY
        );

        narrateTarget = new KeyBinding(
                "minecraft_access.access_menu.gui.button.block_and_fluid_target_info",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                OTHER_GROUP_TRANSLATION_KEY
        );

        targetPosition = new KeyBinding(
                "minecraft_access.access_menu.gui.button.block_and_fluid_target_position",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        lightLevel = new KeyBinding(
                "minecraft_access.access_menu.gui.button.light_level",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        closestWaterSource = new KeyBinding(
                "minecraft_access.access_menu.gui.button.find_water",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        closestLavaSource = new KeyBinding(
                "minecraft_access.access_menu.gui.button.find_lava",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        currentBiome = new KeyBinding(
                "minecraft_access.access_menu.gui.button.biome",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        timeOfDay = new KeyBinding(
                "minecraft_access.access_menu.gui.button.time_of_day",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        xpLevel = new KeyBinding(
                "minecraft_access.access_menu.gui.button.xp",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        refreshScreenReader = new KeyBinding(
                "minecraft_access.access_menu.gui.button.refresh_screen_reader",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        openConfigMenu = new KeyBinding(
                "minecraft_access.access_menu.gui.button.open_config_menu",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                OTHER_GROUP_TRANSLATION_KEY
        );

        directionNarrationKey = new KeyBinding(
                "minecraft_access.keys.other.facing_direction_key_name",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                OTHER_GROUP_TRANSLATION_KEY
        );
    }

    public Set<KeyBinding> getKeys() {
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
//                        areaMapMenuKey,
//                        areaMapNorthKey,
//                        areaMapSouthKey,
//                        areaMapWestKey,
//                        areaMapEastKey,
//                        areaMapUpKey,
//                        areaMapDownKey,
//                        areaMapResetCursorKey,
//                        areaMapMapLockKey,
                healthNHungerNarrationKey,
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
                directionNarrationKey);
    }
}
