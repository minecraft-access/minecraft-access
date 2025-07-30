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
import net.minecraft.world.entity.Entity;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.KeyBindingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;

@Slf4j
public class ObjectTracker {
    public static final String START_OF_LIST = "minecraft_access.other.start_of_list";
    public static final String END_OF_LIST = "minecraft_access.other.end_of_list";

    private boolean isNextItemKeyDown = false;
    private boolean isPreviousItemKeyDown = false;
    private boolean isNarrateCurrentObjectKeyDown = false;
    private boolean isTargetNearestObjectKeyDown = false;
    private boolean wasControlDownLastTick = false;

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
        Minecraft client = Minecraft.getInstance();

        if (client.player == null) return;
        if (client.level == null) return;
        if (client.screen != null) return;

        updateGroups();

        boolean isControlDown = Screen.hasControlDown() && wasControlDownLastTick;
        if (KeyBindingsHandler.OBJECT_TRACKER_NARRATE_CURRENT_OBJECT.mapping.consumeClick()) {
            if (!isNarrateCurrentObjectKeyDown) {
                isNarrateCurrentObjectKeyDown = true;
                narrateCurrentObject(true);
            }
        } else if (!KeyBindingsHandler.OBJECT_TRACKER_NARRATE_CURRENT_OBJECT.mapping.isDown()) {
            isNarrateCurrentObjectKeyDown = false;
        }

        if (KeyBindingsHandler.OBJECT_TRACKER_NEXT_ITEM.mapping.consumeClick()) {
            if (!isNextItemKeyDown) {
                isNextItemKeyDown = true;
                if (isControlDown) {
                    moveGroup(1);
                } else {
                    moveObject(1);
                }
            }
        } else if (!KeyBindingsHandler.OBJECT_TRACKER_NEXT_ITEM.mapping.isDown()) {
            isNextItemKeyDown = false;
        }

        if (KeyBindingsHandler.OBJECT_TRACKER_PREVIOUS_ITEM.mapping.consumeClick()) {
            if (!isPreviousItemKeyDown) {
                isPreviousItemKeyDown = true;
                if (isControlDown) {
                    moveGroup(-1);
                } else {
                    moveObject(-1);
                }
            }
        } else if (!KeyBindingsHandler.OBJECT_TRACKER_PREVIOUS_ITEM.mapping.isDown()) {
            isPreviousItemKeyDown = false;
        }

        if (KeyBindingsHandler.TARGET_NEAREST_OBJECT.mapping.consumeClick()) {
            if (!isTargetNearestObjectKeyDown) {
                isTargetNearestObjectKeyDown = true;
                targetNearestObject();
            }
        } else if (!KeyBindingsHandler.TARGET_NEAREST_OBJECT.mapping.isDown()) {
            isTargetNearestObjectKeyDown = false;
        }

        wasControlDownLastTick = Screen.hasControlDown();
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
            WorldUtils.playSoundAtPosition(SoundEvents.NOTE_BLOCK_BELL, 1, 1.0f, entity.position());
        }

        if (currentObject instanceof BlockPos blockPos) {
            StringBuilder narration = new StringBuilder(NarrationUtils.narrateBlock(blockPos, null));
            if (narrateDistance) {
                narration.append(' ')
                        .append(NarrationUtils.narrateRelativePositionOfPlayerAnd(blockPos));
            }
            MainClass.narrate(narration.toString(), interrupt);
            WorldUtils.playSoundAtPosition(SoundEvents.NOTE_BLOCK_BELL, 1, 1.0f, blockPos.getCenter());
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
        LocalPlayer player = WorldUtils.getClientPlayer();

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
