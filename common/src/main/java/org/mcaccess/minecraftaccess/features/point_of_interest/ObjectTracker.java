package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;
import org.mcaccess.minecraftaccess.utils.system.KeyUtils;

@Slf4j
public class ObjectTracker {
    public static final String START_OF_LIST = "minecraft_access.other.start_of_list";
    public static final String END_OF_LIST = "minecraft_access.other.end_of_list";

    private final Keystroke nextItemKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.OBJECT_TRACKER_NEXT_ITEM.mapping), Keystroke.TriggeredAt.PRESSED);
    private final Keystroke previousItemKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.OBJECT_TRACKER_PREVIOUS_ITEM.mapping), Keystroke.TriggeredAt.PRESSED);
    private final Keystroke narrateCurrentObjectKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.OBJECT_TRACKER_NARRATE_CURRENT_OBJECT.mapping), Keystroke.TriggeredAt.PRESSED);
    private final Keystroke targetNearestObjectKeyPressed = new Keystroke(() -> KeyUtils.isAnyPressed(KeyBindingsHandler.TARGET_NEAREST_OBJECT.mapping), Keystroke.TriggeredAt.PRESSED);

    @Getter
    private Object currentObject = null;
    @Getter
    private POIGroup<?> currentGroup = null;

    private List<POIGroup<?>> groups = new ArrayList<>();

    ObjectTracker() {
    }

    private List<POIGroup<?>> getPOIGroups() {
        List<POIGroup<?>> groupList = Stream.concat(
                Arrays.stream(MainClass.poiManager.poiEntities.groups),
                Arrays.stream(MainClass.poiManager.poiBlocks.groups)
        ).toList();

        List<POIGroup<?>> result = new ArrayList<>();

        for (POIGroup<?> group : groupList) {
            if (!group.isEmpty()) result.add(group);
        }

        return result;
    }

    public void tick() {
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
        if (currentObject == null) {
            MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_selected"), true);
        }

        if (checkAndNarrateIfAllGroupsEmpty()) return;
        boolean narrateDistance = Config.getInstance().poi.narrateDistance;

        if (currentObject instanceof Entity entity) {
            StringBuilder narration = new StringBuilder(NarrationUtils.narrateEntity(entity));
            if (narrateDistance) {
                narration.append(' ')
                        .append(NarrationUtils.narrateRelativePositionOfPlayerAnd(entity.blockPosition()));
            }
            MainClass.narrate(narration.toString(), interrupt);
            Minecraft.getInstance().level.playLocalSound(
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.NOTE_BLOCK_BELL.value(),
                    SoundSource.BLOCKS,
                    1,
                    1.0f,
                    true
            );
        }

        if (currentObject instanceof BlockPos blockPos) {
            StringBuilder narration = new StringBuilder(NarrationUtils.narrateBlock(blockPos, null));
            if (narrateDistance) {
                narration.append(' ')
                        .append(NarrationUtils.narrateRelativePositionOfPlayerAnd(blockPos));
            }
            MainClass.narrate(narration.toString(), interrupt);
            Minecraft.getInstance().level.playLocalSound(
                    blockPos,
                    SoundEvents.NOTE_BLOCK_BELL.value(),
                    SoundSource.BLOCKS,
                    1,
                    1.0f,
                    true
            );
        }
    }

    private void moveGroup(int step) {
        if (checkAndNarrateIfAllGroupsEmpty()) return;

        int currentGroupIndex = groups.indexOf(currentGroup);

        if ((currentGroupIndex + step) > (groups.size() - 1)) {
            MainClass.narrate(I18n.get(END_OF_LIST), true);
            MainClass.narrate(currentGroup.getTranslatedName(), false);
            return;
        }

        if ((currentGroupIndex + step) < 0) {
            MainClass.narrate(I18n.get(START_OF_LIST), true);
            MainClass.narrate(currentGroup.getTranslatedName(), false);
            return;
        }

        currentGroup = groups.get(currentGroupIndex + step);
        currentObject = currentGroup.sortByDistance().getFirst();
        MainClass.narrate(currentGroup.getTranslatedName(), true);
        narrateCurrentObject(false);
    }

    private void moveObject(int step) {
        if (checkAndNarrateIfAllGroupsEmpty()) return;

        List<?> objects = currentGroup.sortByDistance();
        int currentObjectIndex = objects.indexOf(currentObject);

        if (currentObjectIndex == -1) {
            MainClass.narrate(I18n.get(START_OF_LIST), true);
            currentObject = objects.getFirst();
            narrateCurrentObject(false);
            return;
        }

        if ((currentObjectIndex + step) > (objects.size() - 1)) {
            MainClass.narrate(I18n.get(END_OF_LIST), true);
            narrateCurrentObject(false);
            return;
        }

        if ((currentObjectIndex + step) < 0) {
            MainClass.narrate(I18n.get(START_OF_LIST), true);
            narrateCurrentObject(false);
            return;
        }

        currentObject = objects.get(currentObjectIndex + step);
        narrateCurrentObject(true);
    }

    private boolean checkAndNarrateIfAllGroupsEmpty() {
        if (groups.isEmpty()) {
            MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_found"), true);

            return true;
        } else return false;
    }

    private void targetNearestObject() {
        LocalPlayer player = Minecraft.getInstance().player;

        List<Entity> entities = MainClass.poiManager.poiEntities.getLastScanResults()
                .stream()
                .sorted(Comparator.comparingDouble(a -> a.distanceTo(player)))
                .toList();

        List<BlockPos> blocks = MainClass.poiManager.poiBlocks.getLastScanResults()
                .stream()
                .sorted(Comparator.comparingDouble(a -> player.getEyePosition().distanceTo(a.getCenter())))
                .toList();

        if (Screen.hasControlDown() && !Screen.hasShiftDown()) {
            if (entities.isEmpty()) {
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_found.entity"), true);
            } else {
                currentObject = entities.getFirst();
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.targeting_nearest.entity"), true);
            }
        } else if (Screen.hasShiftDown() && !Screen.hasControlDown()) {
            if (blocks.isEmpty()) {
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_found.block"), true);
            } else {
                currentObject = blocks.getFirst();
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.targeting_nearest.block"), true);
            }
        } else {
            if (entities.isEmpty() && blocks.isEmpty()) {
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_found"), true);
            } else if (entities.isEmpty()) {
                currentObject = blocks.getFirst();
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.targeting_nearest"), true);
            } else if (blocks.isEmpty()) {
                currentObject = entities.getFirst();
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.targeting_nearest"), true);
            } else {
                if (player.getEyePosition().distanceTo(blocks.getFirst().getCenter()) < player.distanceTo(entities.getFirst())) {
                    currentObject = blocks.getFirst();
                } else {
                    currentObject = entities.getFirst();
                }
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.targeting_nearest"), true);
            }
        }

        if (!entities.isEmpty() || !blocks.isEmpty()) {
            narrateCurrentObject(false);
        }
    }

    public void clearCurrentObject() {
        currentObject = null;
    }
}
