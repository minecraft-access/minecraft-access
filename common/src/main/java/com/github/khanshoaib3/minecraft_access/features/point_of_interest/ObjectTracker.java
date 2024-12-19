package com.github.khanshoaib3.minecraft_access.features.point_of_interest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.utils.KeyBindingsHandler;
import com.github.khanshoaib3.minecraft_access.utils.NarrationUtils;
import com.github.khanshoaib3.minecraft_access.utils.WorldUtils;
import com.github.khanshoaib3.minecraft_access.utils.condition.Keystroke;
import com.github.khanshoaib3.minecraft_access.utils.system.KeyUtils;

import lombok.Getter;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

public class ObjectTracker {
    @Getter
    private static ObjectTracker instance = new ObjectTracker();

    private Keystroke nextItemKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().objectTrackerNextItem), Keystroke.TriggeredAt.PRESSED);
    private Keystroke previousItemKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().objectTrackerPreviousItem), Keystroke.TriggeredAt.PRESSED);
    private Keystroke narrateCurrentObjectKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().objectTrackerNarrateCurrentObject), Keystroke.TriggeredAt.PRESSED);

    private List<POIGroup> groups = new ArrayList<>();

    private List<POIGroup> getPOIGroups() {
        List<POIGroup> groupList = Stream.concat(
            POIEntities.getInstance().builtInGroups.values().stream(),
            POIBlocks.getInstance().builtInGroups.values().stream()
        ).toList();

        List<POIGroup> result = new ArrayList<>();

        for (POIGroup group : groupList) {
            if (!group.isEmpty()) result.add(group);
        }

        return result;
    }

    private int currentGroupIndex = 0;
    private int currentObjectIndex = 0;

    public void update() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.world == null) return;
        if (minecraftClient.currentScreen != null) return;

        updateGroups();

        if (narrateCurrentObjectKeyPressed.canBeTriggered()) narrateCurrentObject(true);

        if (nextItemKeyPressed.canBeTriggered() && Screen.hasControlDown()) moveGroup(1);
        if (previousItemKeyPressed.canBeTriggered() && Screen.hasControlDown()) moveGroup(-1);

        if (nextItemKeyPressed.canBeTriggered() && !Screen.hasControlDown()) moveObject(1);
        if (previousItemKeyPressed.canBeTriggered() && !Screen.hasControlDown()) moveObject(-1);

        nextItemKeyPressed.updateStateForNextTick();
        previousItemKeyPressed.updateStateForNextTick();
        narrateCurrentObjectKeyPressed.updateStateForNextTick();
    }

    private void updateGroups() {
        groups = getPOIGroups();

        if (!groups.isEmpty() && currentGroupIndex > groups.size() - 1) currentGroupIndex = 0;
    }

    private void narrateCurrentObject(boolean interupt) {
        if (checkAndSpeakIfAllGroupsEmpty()) return;

        POIGroup currentGroup = groups.get(currentGroupIndex);

        if (currentGroup.isEmpty()) {
            MainClass.speakWithNarrator("No objects in current group", interupt);
            return;
        }

        switch (currentGroup.getType()) {
            case ENTITY:
                Entity entity = currentGroup.getEntities().values().stream().toList().get(currentObjectIndex);
                MainClass.speakWithNarrator(NarrationUtils.narrateEntity(entity), interupt);

                WorldUtils.playSoundAtPosition(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 1, 1f, entity.getPos());
                break;
            case BLOCK:
                BlockPos block = currentGroup.getBlocks().keySet().stream().toList().get(currentObjectIndex);
                MainClass.speakWithNarrator(NarrationUtils.narrateBlock(block, null), interupt);

                WorldUtils.playSoundAtPosition(SoundEvents.BLOCK_NOTE_BLOCK_CHIME, 1, 1f, block.toCenterPos());
                break;
        }
    }

    private void moveGroup(int step) {
        if (checkAndSpeakIfAllGroupsEmpty()) return;

        if ((currentGroupIndex + step) > (groups.size() - 1)) {
            MainClass.speakWithNarrator("End of list", true);
            MainClass.speakWithNarrator(groups.get(currentGroupIndex).name, false);
            return;
        }

        if ((currentGroupIndex + step) < 0) {
            MainClass.speakWithNarrator("Start of list", true);
            MainClass.speakWithNarrator(groups.get(currentGroupIndex).name, false);
            return;
        }

        currentGroupIndex += step;
        currentObjectIndex = 0;
        MainClass.speakWithNarrator(groups.get(currentGroupIndex).name, true);
        narrateCurrentObject(false);
    }

    private void moveObject(int step) {
        if (checkAndSpeakIfAllGroupsEmpty()) return;

        POIGroup currentGroup = groups.get(currentGroupIndex);

        switch (currentGroup.getType()) {
            case ENTITY:
                List<Entity> entities = currentGroup.getEntities().values().stream().toList();

                if ((currentObjectIndex + step) > (entities.size() - 1)) {
                    MainClass.speakWithNarrator("End of list", true);
                    currentObjectIndex = entities.size() - 1;
                    narrateCurrentObject(false);
                    return;
                }

                if ((currentObjectIndex + step) < 0) {
                    MainClass.speakWithNarrator("Start of list", true);
                    narrateCurrentObject(false);
                    return;
                }

                currentObjectIndex += step;
                break;
                case BLOCK:
            List<BlockPos> blocks = currentGroup.getBlocks().keySet().stream().toList();

            if ((currentObjectIndex + step) > (blocks.size() - 1)) {
                MainClass.speakWithNarrator("End of list", true);
                currentObjectIndex = blocks.size() - 1;
                narrateCurrentObject(false);
                return;
            }

            if ((currentObjectIndex + step) < 0) {
                MainClass.speakWithNarrator("Start of list", true);
                narrateCurrentObject(false);
                return;
            }

            currentObjectIndex += step;
            break;
        }

        narrateCurrentObject(true);
    }

    private boolean checkAndSpeakIfAllGroupsEmpty() {
        if (groups.isEmpty()) {
            MainClass.speakWithNarrator("No points of interest detected", true);

            return true;
        } else return false;
    }

    public Object getCurrentObject() {
        POIGroup group = groups.get(currentGroupIndex);

        switch (group.getType()) {
            case ENTITY:
                return group.getEntities().values().stream().toList().get(currentObjectIndex);
            case BLOCK:
                return group.getBlocks().keySet().stream().toList().get(currentObjectIndex);
        }

        return null;
    }
}
