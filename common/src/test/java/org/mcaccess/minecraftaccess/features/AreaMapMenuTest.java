package org.mcaccess.minecraftaccess.features;

import org.mcaccess.minecraftaccess.config.config_maps.AreaMapConfigMap;
import org.mcaccess.minecraftaccess.features.area_map_menu.AreaMapMenu;
import org.mcaccess.minecraftaccess.features.area_map_menu.AreaMapMenuGUI;
import org.mcaccess.minecraftaccess.test_utils.MockKeystrokeAction;
import org.mcaccess.minecraftaccess.test_utils.MockKeystrokeActionArray;
import org.mcaccess.minecraftaccess.test_utils.MockMinecraftClientWrapper;
import org.mcaccess.minecraftaccess.test_utils.annotations.FeatureText;
import org.mcaccess.minecraftaccess.test_utils.annotations.MockMinecraftClient;
import org.mcaccess.minecraftaccess.test_utils.annotations.MockPlayerPositionUtils;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.position.Orientation;
import org.mcaccess.minecraftaccess.utils.position.PlayerPositionUtils;
import net.minecraft.Bootstrap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.MockedStatic;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

@FeatureText
class AreaMapMenuTest {
    @MockMinecraftClient
    MockMinecraftClientWrapper mockClient;
    @MockPlayerPositionUtils
    MockedStatic<PlayerPositionUtils> mockPlayerPositionUtils;
    static MockKeystrokeAction menuKeyAction;
    static MockKeystrokeActionArray cursorMovingKeyActions;
    static MockKeystrokeAction cursorResetKeyAction;
    static MockKeystrokeAction mapLockKeyAction;

    @BeforeAll
    static void beforeAll() {
        menuKeyAction = MockKeystrokeAction.mock(AreaMapMenu.class, "menuKey");
        cursorMovingKeyActions = new MockKeystrokeActionArray(AreaMapMenu.class, "cursorMovingKeys");
        cursorResetKeyAction = MockKeystrokeAction.mock(AreaMapMenu.class, "cursorResetKey");
        mapLockKeyAction = MockKeystrokeAction.mock(AreaMapMenu.class, "mapLockKey");
    }

    @BeforeEach
    void setUp() {
        Set.of(menuKeyAction, cursorResetKeyAction, mapLockKeyAction).forEach(MockKeystrokeAction::resetTargetInnerState);
        cursorMovingKeyActions.resetTargetInnerState();
        resetAreaMapMenuInnerState();
    }

    @Test
    void testOpenMenuWithMenuKey() {
        // ensure not opened
        mockClient.verifyClosingMenu();
        // open
        openOrCloseAreaMapMenu();
        // verify opening
        mockClient.verifyOpeningMenuOf(AreaMapMenuGUI.class);
        assertThat(valueOfMapCursor())
                .as("Cursor should be reset to player position (zero)")
                .isEqualTo(new BlockPos(Vec3i.ZERO));
    }

    @Test
    void testCloseMenuWithMenuKey() {
        // open
        openOrCloseAreaMapMenu();
        // close
        openOrCloseAreaMapMenu();
        // verify closing
        mockClient.verifyClosingMenu();
    }

    @Test
    void testOpenCloseOpenMenu() {
        // open
        openOrCloseAreaMapMenu();
        // close
        openOrCloseAreaMapMenu();
        // reopen
        openOrCloseAreaMapMenu();
        mockClient.verifyOpeningMenuOf(AreaMapMenuGUI.class);
    }

    @ParameterizedTest
    @MethodSource
    void testCursorMovingWithKeys(@NotNull Orientation directionUnderTest, int index) {
        openOrCloseAreaMapMenu();
        setMapCursorTo(new BlockPos(Vec3i.ZERO));
        pressAndRelease(cursorMovingKeyActions.get(index));

        String msg = "Cursor should be same as vector of " + directionUnderTest + " since init position is zero";
        assertThat(valueOfMapCursor()).as(msg).isEqualTo(directionUnderTest.vector);
    }

    static Stream<Arguments> testCursorMovingWithKeys() {
        return Stream.of(
                arguments(Orientation.NORTH, 0),
                arguments(Orientation.SOUTH, 1),
                arguments(Orientation.WEST, 2),
                arguments(Orientation.EAST, 3),
                arguments(Orientation.UP, 4),
                arguments(Orientation.DOWN, 5)
        );
    }

    @Test
    void testCursorResetWithKey() {
        openOrCloseAreaMapMenu();
        BlockPos zeroPos = new BlockPos(Vec3i.ZERO);
        setMapCursorTo(zeroPos.offset(Direction.NORTH));
        pressAndRelease(cursorResetKeyAction);

        assertThat(valueOfMapCursor())
                .as("Cursor should be reset to player position (zero)")
                .isEqualTo(zeroPos);
    }

    @Test
    @DisplayName("test cursor reset when player position changed when reopen the menu")
    void testCursorResetWhenReopenTheMenu() {
        // open the menu, set cursor from null to 0,0,0
        openOrCloseAreaMapMenu();
        assertThat(valueOfMapCursor())
                .as("Cursor should be reset to player position (zero)")
                .isEqualTo(new BlockPos(Vec3i.ZERO));

        // close the menu
        openOrCloseAreaMapMenu();

        // mock player moving
        BlockPos newPlayerPosition = new BlockPos(new Vec3i(1, 1, 1));
        mockPlayerPositionUtils.when(PlayerPositionUtils::getPlayerBlockPosition).thenReturn(Optional.of(newPlayerPosition));

        // open the menu again
        openOrCloseAreaMapMenu();
        openOrCloseAreaMapMenu();

        assertThat(valueOfMapCursor())
                .as("Cursor should be reset to player position (1,1,1)")
                .isEqualTo(newPlayerPosition);
    }

    @Test
    void testMapLockingUnlockingWithKey() {
        // open the menu, set cursor from null to 0,0,0
        openOrCloseAreaMapMenu();
        BlockPos zeroPos = new BlockPos(Vec3i.ZERO);
        assertThat(valueOfMapCursor()).isEqualTo(zeroPos);

        // lock the map
        pressAndRelease(mapLockKeyAction);

        // close the menu
        openOrCloseAreaMapMenu();

        // mock player moving
        BlockPos newPlayerPosition = new BlockPos(new Vec3i(1, 1, 1));
        mockPlayerPositionUtils.when(PlayerPositionUtils::getPlayerBlockPosition).thenReturn(Optional.of(newPlayerPosition));

        // open the menu
        openOrCloseAreaMapMenu();
        assertThat(valueOfMapCursor())
                .as("Cursor should still be (0,0,0) since map is locked")
                .isEqualTo(zeroPos);

        try {
            var b = Bootstrap.class.getDeclaredField("initialized");
            b.trySetAccessible();
            b.set(null, true);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // unlock the map
        pressAndRelease(mapLockKeyAction);

        // close the menu
        openOrCloseAreaMapMenu();
        // and open the menu again
        openOrCloseAreaMapMenu();

        assertThat(valueOfMapCursor())
                .as("Cursor should be reset to player position (1,1,1) since map is unlocked")
                .isEqualTo(newPlayerPosition);
    }

    @ParameterizedTest
    @MethodSource
    void testDistanceBoundCheckingWhileCursorMoving(int keyIndex, BlockPos position) {
        openOrCloseAreaMapMenu();
        setMapCursorTo(position);
        pressAndRelease(cursorMovingKeyActions.get(keyIndex));

        String msg = "Cursor should stay at original position since reaches distance bounds";
        assertThat(valueOfMapCursor()).as(msg).isEqualTo(position);
    }

    static Stream<Arguments> testDistanceBoundCheckingWhileCursorMoving() {
        var map = AreaMapConfigMap.getInstance();
        int vb = map.getVerticalBound();
        int hb = map.getHorizontalBound();
        // https://minecraft.wiki/w/Coordinates
        // North = -Z, Up = +Y, East = +X
        // set init position as edge of bounds
        return Stream.of(
                arguments(0, new BlockPos(0, 0, -hb)),
                arguments(1, new BlockPos(0, 0, hb)),
                arguments(2, new BlockPos(-hb, 0, 0)),
                arguments(3, new BlockPos(hb, 0, 0)),
                arguments(4, new BlockPos(0, vb, 0)),
                arguments(5, new BlockPos(0, -vb, 0))
        );
    }

    @Test
    void testCursorResetWhenReopenTheMenuWhileMapLockedButCursorOutOfDistanceBound() {
        // open the menu
        openOrCloseAreaMapMenu();
        // lock the map
        pressAndRelease(mapLockKeyAction);
        // close the menu
        openOrCloseAreaMapMenu();

        // set cursor position out of distance bound
        var map = AreaMapConfigMap.getInstance();
        setMapCursorTo(new BlockPos(map.getHorizontalBound() + 1, 0, 0));

        // mock player moving, keep x-axis stays at 0
        BlockPos newPlayerPosition = new BlockPos(new Vec3i(0, 1, 0));
        mockPlayerPositionUtils.when(PlayerPositionUtils::getPlayerBlockPosition).thenReturn(Optional.of(newPlayerPosition));

        // reopen the menu
        openOrCloseAreaMapMenu();
        openOrCloseAreaMapMenu();

        assertThat(valueOfMapCursor())
                .as("Cursor should be reset to current player position " +
                        "since cursor has been out of distance bound, no matter the map is locked")
                .isEqualTo(newPlayerPosition);
    }

    private static void setMapCursorTo(BlockPos pos) {
        try {
            Field cursorField = AreaMapMenu.class.getDeclaredField("cursor");
            cursorField.trySetAccessible();
            ReflectionUtil.setFieldValue(cursorField, AreaMapMenu.getInstance(), pos);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private Vec3i valueOfMapCursor() {
        try {
            Field cursorField = AreaMapMenu.class.getDeclaredField("cursor");
            cursorField.trySetAccessible();
            return (Vec3i) ReflectionUtils.tryToReadFieldValue(cursorField, AreaMapMenu.getInstance()).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * By pressing and releasing the menu key
     */
    private void openOrCloseAreaMapMenu() {
        pressAndRelease(menuKeyAction);
    }

    private void pressAndRelease(MockKeystrokeAction keystrokeAction) {
        keystrokeAction.press();
        oneTickForward();
        keystrokeAction.release();
        oneTickForward();
    }

    private void oneTickForward() {
        AreaMapMenu.getInstance().execute(mockClient.mockito());
        Keystroke.updateInstances();
    }

    /**
     * To avoid test cases affect each other.
     */
    private void resetAreaMapMenuInnerState() {
        try {
            var mapLocked = AreaMapMenu.class.getDeclaredField("mapLocked");
            mapLocked.trySetAccessible();
            mapLocked.set(AreaMapMenu.getInstance(), false);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}