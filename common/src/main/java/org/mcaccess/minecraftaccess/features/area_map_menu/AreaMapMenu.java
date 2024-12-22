package com.github.khanshoaib3.minecraft_access.features.area_map_menu;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.AreaMapConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.NarrationUtils;
import com.github.khanshoaib3.minecraft_access.utils.PlayerUtils;
import com.github.khanshoaib3.minecraft_access.utils.condition.Interval;
import com.github.khanshoaib3.minecraft_access.utils.condition.IntervalKeystroke;
import com.github.khanshoaib3.minecraft_access.utils.condition.Keystroke;
import com.github.khanshoaib3.minecraft_access.utils.condition.MenuKeystroke;
import com.github.khanshoaib3.minecraft_access.utils.position.Orientation;
import com.github.khanshoaib3.minecraft_access.utils.position.PlayerPositionUtils;
import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;

/**
 * This menu gives user a bird eye view of surrounding area.
 * It plays the role of the Map function in other games.
 * User can move a virtual cursor to explore the area (speak out pointed block's information).
 * Open the AreaMap menu with F6.
 */
@Slf4j
public class AreaMapMenu {
    @Getter
    private static final AreaMapMenu instance;

    private static final MenuKeystroke menuKey;
    private static final IntervalKeystroke[] cursorMovingKeys = new IntervalKeystroke[6];
    private static final Keystroke cursorResetKey;
    private static final Keystroke mapLockKey;
    public static final Set<Pair<IntervalKeystroke, Orientation>> CURSOR_MOVING_DIRECTIONS = new HashSet<>(6);

    private boolean enabled;
    private int verticalBound = 2;
    private int horizontalBound = 96;
    private BlockPos cursor;
    private boolean mapLocked = false;

    static {
        instance = new AreaMapMenu();

        menuKey = new MenuKeystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().areaMapMenuKey));

        int keyInterval = AreaMapConfigMap.getInstance().getDelayInMilliseconds();
        int cursorMovingKeyIndex = 0;
        for (var p : List.<Pair<Orientation, BooleanSupplier>>of(
                new Pair<>(Orientation.NORTH, () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().areaMapNorthKey)),
                new Pair<>(Orientation.SOUTH, () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().areaMapSouthKey)),
                new Pair<>(Orientation.WEST, () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().areaMapWestKey)),
                new Pair<>(Orientation.EAST, () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().areaMapEastKey)),
                new Pair<>(Orientation.UP, () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().areaMapUpKey)),
                new Pair<>(Orientation.DOWN, () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().areaMapDownKey))
        )) {
            cursorMovingKeys[cursorMovingKeyIndex] = new IntervalKeystroke(p.getRight(), Keystroke.TriggeredAt.PRESSING, Interval.inMilliseconds(keyInterval));
            CURSOR_MOVING_DIRECTIONS.add(new Pair<>(cursorMovingKeys[cursorMovingKeyIndex], p.getLeft()));
            cursorMovingKeyIndex += 1;
        }

        cursorResetKey = new Keystroke(
                () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().areaMapCursorResetKey),
                Keystroke.TriggeredAt.PRESSING);

        mapLockKey = new Keystroke(
                () -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().areaMapMapLockKey),
                Keystroke.TriggeredAt.PRESSING);
    }

    public void update() {
        try {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return;
            if (client.player == null) return;

            // functional core, imperative shell, for easier testing
            execute(client);
        } catch (Exception e) {
            log.error("An error occurred in AreaMapMenu.", e);
        }
    }

    public void execute(MinecraftClient client) {
        updateConfigs();
        if (!enabled) return;

        if (client.currentScreen == null) {
            if (menuKey.canOpenMenu()) {
                openAreaMapMenu();
                updateMapStatesOnMenuOpening();
            }
        } else {
            if (client.currentScreen instanceof AreaMapMenuGUI) {
                if (menuKey.closeMenuIfMenuKeyPressing()) return;
                handleInMenuActions();
            } else {
                // other menus is opened currently, won't open the menu
                return;
            }
        }

        menuKey.updateStateForNextTick();
        cursorResetKey.updateStateForNextTick();
        mapLockKey.updateStateForNextTick();
        Arrays.stream(cursorMovingKeys).forEach(IntervalKeystroke::updateStateForNextTick);
    }

    private void updateConfigs() {
        AreaMapConfigMap map = AreaMapConfigMap.getInstance();
        this.enabled = map.isEnabled();
        this.verticalBound = map.getVerticalBound();
        this.horizontalBound = map.getHorizontalBound();

        // set key intervals
        Arrays.stream(cursorMovingKeys).forEach(k -> k.setInterval(Interval.inMilliseconds(map.getDelayInMilliseconds(), k.interval())));
    }

    private void openAreaMapMenu() {
        MinecraftClient client = MinecraftClient.getInstance();
        client.setScreen(new AreaMapMenuGUI(client));
    }

    private void updateMapStatesOnMenuOpening() {
        // reset if out of distance bound
        if (mapLocked && checkCursorWithinDistanceBound(this.cursor)) return;
        resetCursorToPlayerPosition(false);
    }

    private void handleInMenuActions() {
        // move cursor
        for (Pair<IntervalKeystroke, Orientation> p : CURSOR_MOVING_DIRECTIONS) {
            if (p.getLeft().isCooledDownAndTriggered()) {
                Orientation direction = p.getRight();
                moveCursorTowards(direction);
                return;
            }
        }

        if (cursorResetKey.canBeTriggered()) {
            resetCursorToPlayerPosition(true);
            return;
        }

        if (mapLockKey.canBeTriggered()) {
            mapLocked = !mapLocked;
            if (mapLocked) {
                MainClass.speakWithNarrator(I18n.translate("minecraft_access.area_map.map_lock"), true);
            } else {
                MainClass.speakWithNarrator(I18n.translate("minecraft_access.area_map.map_unlock"), true);
                // Play the same unlock sound as POI Unlocking
                PlayerUtils.playSoundOnPlayer(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, 0.4f, 2f);
            }
        }
    }

    private void moveCursorTowards(Orientation direction) {
        BlockPos nextStep = cursor.add(direction.vector);
        if (!checkCursorWithinDistanceBound(nextStep)) return;

        this.cursor = nextStep;
       log.debug("Cursor moves " + direction + ": " + cursor);
        String blockDescription = NarrationUtils.narrateBlock(this.cursor, "");
        MainClass.speakWithNarrator(blockDescription, true);
        // TODO Alt + speak position key
//        MainClass.speakWithNarrator(blockDescription.getLeft() + I18n.translate("minecraft_access.other.words_connection") + PlayerPositionUtils.getI18NPosition(), true);
    }

    private boolean checkCursorWithinDistanceBound(BlockPos nextStep) {
        BlockPos playerPos = PlayerPositionUtils.getPlayerBlockPosition().orElseThrow();
        int distanceOnX = Math.abs(playerPos.getX() - nextStep.getX());
        int distanceOnY = Math.abs(playerPos.getY() - nextStep.getY());
        int distanceOnZ = Math.abs(playerPos.getZ() - nextStep.getZ());
        if(distanceOnX > horizontalBound || distanceOnZ > horizontalBound || distanceOnY > verticalBound) {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.area_map.cursor_reach_bound"), true);
            return false;
        }
        return true;
    }

    private void resetCursorToPlayerPosition(boolean interruptNarration) {
        cursor = PlayerPositionUtils.getPlayerBlockPosition().orElseThrow();
        MainClass.speakWithNarrator(I18n.translate("minecraft_access.area_map.cursor_reset", PlayerPositionUtils.getNarratableXYZPosition()), interruptNarration);
    }
}
