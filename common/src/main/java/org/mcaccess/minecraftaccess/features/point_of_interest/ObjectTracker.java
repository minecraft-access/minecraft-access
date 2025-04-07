package org.mcaccess.minecraftaccess.features.point_of_interest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class ObjectTracker {
    public static final String START_OF_LIST = "minecraft_access.other.start_of_list";
    public static final String END_OF_LIST = "minecraft_access.other.end_of_list";
    @Getter
    private static ObjectTracker instance = new ObjectTracker();

    private final Keystroke nextItemKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.objectTrackerNextItem), Keystroke.TriggeredAt.PRESSED);
    private final Keystroke previousItemKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.objectTrackerPreviousItem), Keystroke.TriggeredAt.PRESSED);
    private final Keystroke narrateCurrentObjectKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.objectTrackerNarrateCurrentObject), Keystroke.TriggeredAt.PRESSED);
    private final Keystroke targetNearestObjectKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.targetNearestObject), Keystroke.TriggeredAt.PRESSED);

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

    @Getter
    private Object currentObject = null;
    @Getter
    private POIGroup<?> currentGroup = null;

    public void update() {
        Minecraft minecraftClient = Minecraft.getInstance();

        if (minecraftClient.player == null) return;
        if (minecraftClient.level == null) return;
        if (minecraftClient.screen != null) return;

        updateGroups();

        if (narrateCurrentObjectKeyPressed.canBeTriggered()) narrateCurrentObject(true);

        if (nextItemKeyPressed.canBeTriggered() && Screen.hasControlDown()) moveGroup(1);
        if (previousItemKeyPressed.canBeTriggered() && Screen.hasControlDown()) moveGroup(-1);

        if (nextItemKeyPressed.canBeTriggered() && !Screen.hasControlDown()) moveObject(1);
        if (previousItemKeyPressed.canBeTriggered() && !Screen.hasControlDown()) moveObject(-1);

        if (targetNearestObjectKeyPressed.canBeTriggered()) targetNearestObject();
    }

    private void updateGroups() {
        groups = getPOIGroups();

        int currentGroupIndex = groups.indexOf(currentGroup);

        if (!groups.isEmpty() && currentGroupIndex == -1) currentGroup = groups.getFirst();
        if (groups.isEmpty() && currentGroupIndex != -1) currentGroup = null;
    }

    private void narrateCurrentObject(boolean interrupt) {
        if (checkAndSpeakIfAllGroupsEmpty()) return;
        boolean speakDistance = Config.getInstance().poi.speakDistance;

        if (currentObject instanceof Entity entity) {
            String message = NarrationUtils.narrateEntity(entity);
            if (speakDistance) message += " " + NarrationUtils.narrateRelativePositionOfPlayerAnd(entity.blockPosition());
            MainClass.speakWithNarrator(message, interrupt);
            WorldUtils.playSoundAtPosition(SoundEvents.NOTE_BLOCK_BELL, 1, 1f, entity.position());
        }

        if (currentObject instanceof BlockPos blockPos) {
            String message = NarrationUtils.narrateBlock(blockPos, null);
            if (speakDistance) message += " " + NarrationUtils.narrateRelativePositionOfPlayerAnd(blockPos);
            MainClass.speakWithNarrator(message, interrupt);
            WorldUtils.playSoundAtPosition(SoundEvents.NOTE_BLOCK_BELL, 1, 1f, blockPos.getCenter());
        }
    }

    private void moveGroup(int step) {
        if (checkAndSpeakIfAllGroupsEmpty()) return;

        int currentGroupIndex = groups.indexOf(currentGroup);

        if ((currentGroupIndex + step) > (groups.size() - 1)) {
            MainClass.speakWithNarrator(I18n.get(END_OF_LIST), true);
            MainClass.speakWithNarrator(currentGroup.getTranslatedName(), false);
            return;
        }

        if ((currentGroupIndex + step) < 0) {
            MainClass.speakWithNarrator(I18n.get(START_OF_LIST), true);
            MainClass.speakWithNarrator(currentGroup.getTranslatedName(), false);
            return;
        }

        currentGroup = groups.get(currentGroupIndex + step);
        currentObject = currentGroup.getFirst();
        MainClass.speakWithNarrator(currentGroup.getTranslatedName(), true);
        narrateCurrentObject(false);
    }

    private void moveObject(int step) {
        if (checkAndSpeakIfAllGroupsEmpty()) return;

        List<?> objects = currentGroup.getItems();
        int currentObjectIndex = objects.indexOf(currentObject);

        if (currentObjectIndex == -1) {
            MainClass.speakWithNarrator(I18n.get(START_OF_LIST), true);
            currentObject = objects.get(0);
            narrateCurrentObject(false);
            return;
        }

        if ((currentObjectIndex + step) > (objects.size() - 1)) {
            MainClass.speakWithNarrator(I18n.get(END_OF_LIST), true);
            narrateCurrentObject(false);
            return;
        }

        if ((currentObjectIndex + step) < 0) {
            MainClass.speakWithNarrator(I18n.get(START_OF_LIST), true);
            narrateCurrentObject(false);
            return;
        }

        currentObject = objects.get(currentObjectIndex + step);
        narrateCurrentObject(true);
    }

    private boolean checkAndSpeakIfAllGroupsEmpty() {
        if (groups.isEmpty()) {
            MainClass.speakWithNarrator(I18n.get("minecraft_access.point_of_interest.not_found"), true);

            return true;
        } else return false;
    }

    private void targetNearestObject() {
        List<Entity> entities = POIEntities.getInstance().getLastScanResults();
        List<BlockPos> blocks = POIBlocks.getInstance().getLastScanResults();

        if (!entities.isEmpty() && blocks.isEmpty()) currentObject = entities.getFirst();
        if (!blocks.isEmpty() && entities.isEmpty()) currentObject = blocks.getFirst();
        if (!entities.isEmpty() && !blocks.isEmpty()) {
            LocalPlayer player = WorldUtils.getClientPlayer();
            double distanceToEntity = player.distanceTo(entities.getFirst());
            double distanceToBlock = player.getEyePosition().distanceTo(blocks.getFirst().getCenter());

            if (distanceToEntity <= distanceToBlock) currentObject = entities.getFirst();
            if (distanceToBlock < distanceToEntity) currentObject = blocks.getFirst();
        }

        if (!entities.isEmpty() || !blocks.isEmpty()) {
            MainClass.speakWithNarrator(I18n.get("minecraft_access.point_of_interest.targeting_nearest"), true);
            narrateCurrentObject(false);
        } else MainClass.speakWithNarrator(I18n.get("minecraft_access.point_of_interest.not_found"), true);
    }
}
