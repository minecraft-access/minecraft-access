package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.config.config_maps.POIConfigMap;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

@Slf4j
public class ObjectTracker {
    @Getter
    private static ObjectTracker instance = new ObjectTracker();

    private Keystroke nextItemKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().objectTrackerNextItem), Keystroke.TriggeredAt.PRESSED);
    private Keystroke previousItemKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().objectTrackerPreviousItem), Keystroke.TriggeredAt.PRESSED);
    private Keystroke narrateCurrentObjectKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().objectTrackerNarrateCurrentObject), Keystroke.TriggeredAt.PRESSED);
    private Keystroke targetNearestObjectKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.getInstance().targetNearestObject), Keystroke.TriggeredAt.PRESSED);

    private List<POIGroup<?>> groups = new ArrayList<>();

    private List<POIGroup<?>> getPOIGroups() {
        List<POIGroup<?>> groupList = Stream.concat(
            Arrays.stream(POIEntities.getInstance().groups),
            Arrays.stream(POIBlocks.getInstance().groups)
        ).toList();

        List<POIGroup<?>> result = new ArrayList<>();

        for (POIGroup<?> group : groupList) {
            if (!group.isEmpty()) result.add(group);
        }

        return result;
    }

    private int currentGroupIndex = 0;
    private int currentObjectIndex = 0;
    @Getter
    private Object currentObject;

    private boolean speakDistance;

    public void update() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();

        if (minecraftClient == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.world == null) return;
        if (minecraftClient.currentScreen != null) return;

        updateGroups();
        loadConfigurations();

        if (narrateCurrentObjectKeyPressed.canBeTriggered()) narrateCurrentObject(true);

        if (nextItemKeyPressed.canBeTriggered() && Screen.hasControlDown()) moveGroup(1);
        if (previousItemKeyPressed.canBeTriggered() && Screen.hasControlDown()) moveGroup(-1);

        if (nextItemKeyPressed.canBeTriggered() && !Screen.hasControlDown()) moveObject(1);
        if (previousItemKeyPressed.canBeTriggered() && !Screen.hasControlDown()) moveObject(-1);

        if (targetNearestObjectKeyPressed.canBeTriggered()) targetNearestObject();

        nextItemKeyPressed.updateStateForNextTick();
        previousItemKeyPressed.updateStateForNextTick();
        narrateCurrentObjectKeyPressed.updateStateForNextTick();
        targetNearestObjectKeyPressed.updateStateForNextTick();
    }

    private void updateGroups() {
        groups = getPOIGroups();

        if (!groups.isEmpty() && currentGroupIndex > groups.size() - 1) currentGroupIndex = 0;
    }

    private void loadConfigurations() {
        POIConfigMap map = POIConfigMap.getInstance();
        speakDistance = map.isSpeakTargetPosition();
    }

    private void narrateCurrentObject(boolean interupt) {
        if (checkAndSpeakIfAllGroupsEmpty()) return;

        POIGroup<?> currentGroup = groups.get(currentGroupIndex);

        if (currentGroup.isEmpty()) {
            MainClass.speakWithNarrator("No objects in current group", interupt);
            return;
        }

        if (currentObject == null) {
            currentObject = currentGroup.getItems(true).get(currentObjectIndex);
        }

        if (currentObject instanceof Entity) {
            Entity entity = (Entity)currentObject;

            String message = NarrationUtils.narrateEntity(entity);
            if (speakDistance) message += " " + NarrationUtils.narrateRelativePositionOfPlayerAnd(entity.getBlockPos());
            MainClass.speakWithNarrator(message, interupt);
            WorldUtils.playSoundAtPosition(SoundEvents.BLOCK_NOTE_BLOCK_BELL, 1, 1f, entity.getPos());
        }

        if (currentObject instanceof BlockPos) {
            BlockPos block = (BlockPos)currentObject;

            String message = NarrationUtils.narrateBlock(block, null);
            if (speakDistance) message += " " + NarrationUtils.narrateRelativePositionOfPlayerAnd(block);
            MainClass.speakWithNarrator(message, interupt);
            WorldUtils.playSoundAtPosition(SoundEvents.BLOCK_NOTE_BLOCK_BELL, 1, 1f, block.toCenterPos());
        }
    }

    private void moveGroup(int step) {
        if (checkAndSpeakIfAllGroupsEmpty()) return;

        if ((currentGroupIndex + step) > (groups.size() - 1)) {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.other.end_of_list"), true);
            MainClass.speakWithNarrator(groups.get(currentGroupIndex).getName(), false);
            return;
        }

        if ((currentGroupIndex + step) < 0) {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.other.start_of_list"), true);
            MainClass.speakWithNarrator(groups.get(currentGroupIndex).getName(), false);
            return;
        }

        currentGroupIndex += step;
        currentObjectIndex = 0;
        currentObject = groups.get(currentGroupIndex).getItems().get(currentObjectIndex);
        MainClass.speakWithNarrator(groups.get(currentGroupIndex).getName(), true);
        narrateCurrentObject(false);
    }

    private void moveObject(int step) {
        if (checkAndSpeakIfAllGroupsEmpty()) return;

        POIGroup<?> currentGroup = groups.get(currentGroupIndex);

        List<?> objects = currentGroup.getItems(true);

        if ((currentObjectIndex + step) > (objects.size() - 1)) {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.other.end_of_list"), true);
            currentObjectIndex = objects.size() - 1;
            currentObject = currentGroup.getItems().get(currentObjectIndex);
            narrateCurrentObject(false);
            return;
        }

        if ((currentObjectIndex + step) < 0) {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.other.start_of_list"), true);
            narrateCurrentObject(false);
            return;
        }

        currentObjectIndex += step;
        currentObject = currentGroup.getItems().get(currentObjectIndex);

        narrateCurrentObject(true);
    }

    private boolean checkAndSpeakIfAllGroupsEmpty() {
        if (groups.isEmpty()) {
            MainClass.speakWithNarrator(I18n.translate("minecraft_access.point_of_interest.not_found"), true);

            return true;
        } else return false;
    }

    private void targetNearestObject() {
        List<Entity> entities = POIEntities.getInstance().getLastScanResults();
        List<BlockPos> blocks = POIBlocks.getInstance().getLastScanResults();

        if (!entities.isEmpty() && blocks.isEmpty()) currentObject = entities.getFirst();
        if (!blocks.isEmpty() && entities.isEmpty()) currentObject = blocks.getFirst();
        if (!entities.isEmpty() && !blocks.isEmpty()) {
            double distanceToEntity = MinecraftClient.getInstance().player.distanceTo(entities.getFirst());
            double distanceToBlock = MinecraftClient.getInstance().player.getEyePos().distanceTo(blocks.getFirst().toCenterPos());

            if (distanceToEntity <= distanceToBlock) currentObject = entities.getFirst();
            if (distanceToBlock < distanceToEntity) currentObject = blocks.getFirst();
        }

        if (!entities.isEmpty() || !blocks.isEmpty()) {
            MainClass.speakWithNarrator("Targeting nearest object", true);
            narrateCurrentObject(false);
        }
        else MainClass.speakWithNarrator(I18n.translate("minecraft_access.point_of_interest.not_found"), true);
    }
}
