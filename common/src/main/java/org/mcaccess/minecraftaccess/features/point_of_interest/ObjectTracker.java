package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AirBlock;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.WorldNarrator;
import org.mcaccess.minecraftaccess.utils.KeyMappingsHandler;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;
import org.mcaccess.minecraftaccess.utils.condition.Keystroke;


@Slf4j
public class ObjectTracker implements BalmClientModule {
    private final Minecraft client = Minecraft.getInstance();
    public static final String START_OF_LIST = "minecraft_access.other.start_of_list";
    public static final String END_OF_LIST = "minecraft_access.other.end_of_list";

    private final Keystroke nextItemKeyPressed = new Keystroke(() -> KeyMappingsHandler.Keys.OBJECT_TRACKER_NEXT_ITEM.mapping.isDown(), Keystroke.TriggeredAt.PRESSED);
    private final Keystroke previousItemKeyPressed = new Keystroke(() -> KeyMappingsHandler.Keys.OBJECT_TRACKER_PREVIOUS_ITEM.mapping.isDown(), Keystroke.TriggeredAt.PRESSED);
    private final Keystroke narrateCurrentObjectKeyPressed = new Keystroke(() -> KeyMappingsHandler.Keys.OBJECT_TRACKER_NARRATE_CURRENT_OBJECT.mapping.isDown(), Keystroke.TriggeredAt.PRESSED);
    private final Keystroke targetNearestObjectKeyPressed = new Keystroke(() -> KeyMappingsHandler.Keys.TARGET_NEAREST_OBJECT.mapping.isDown(), Keystroke.TriggeredAt.PRESSED);

    @Getter
    private Object currentObject = null;
    @Getter
    private POIGroup<?> currentGroup = null;

    private List<POIGroup<?>> groups = new ArrayList<>();

    ObjectTracker() {
    }

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "poi/object_tracker");
    }

    @Override
    public void initialize() {
        ClientTickCallback.ClientPlayerTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            currentObject = null;
            currentGroup = null;
            groups = new ArrayList<>();
        });
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

    private void tick(Player player) {
        if (client.level == null) return;
        if (client.screen != null) return;

        updateGroups();

        if (narrateCurrentObjectKeyPressed.canBeTriggered()) narrateCurrentObject(true);

        if (nextItemKeyPressed.canBeTriggered() && client.hasControlDown()) moveGroup(1);
        if (previousItemKeyPressed.canBeTriggered() && client.hasControlDown()) moveGroup(-1);

        if (nextItemKeyPressed.canBeTriggered() && !client.hasControlDown()) moveObject(1);
        if (previousItemKeyPressed.canBeTriggered() && !client.hasControlDown()) moveObject(-1);

        if (targetNearestObjectKeyPressed.canBeTriggered()) targetNearestObject();
    }

    private void updateGroups() {
        groups = getPOIGroups();

        int currentGroupIndex = groups.indexOf(currentGroup);

        if (currentGroup != null && currentGroup.isEmpty()) currentGroupIndex = -1;
        if (!groups.isEmpty() && currentGroupIndex == -1) currentGroup = groups.getFirst();
        if (groups.isEmpty() && currentGroupIndex != -1) currentGroup = null;
    }

    private void narrateCurrentObject(boolean interrupt) {
        if (checkAndNarrateIfAllGroupsEmpty()) return;

        if (!isObjectValid(currentObject)) {
            clearCurrentObject();
            MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_selected"), true);
            return;
        }

        boolean narrateDistance = Config.getInstance().poi.narrateDistance;

        if (currentObject instanceof Entity entity) {
            StringBuilder narration = new StringBuilder(
                    MainClass.registry(WorldNarrator.class).get(Config.getInstance().narrateCrosshair.narrator).narrate(entity)
            );
            if (narrateDistance) {
                narration.append(' ')
                        .append(NarrationUtils.narrateRelativePositionOfPlayerAnd(entity.blockPosition()));
            }
            MainClass.narrate(narration.toString(), interrupt);
            assert client.level != null;
            client.level.playLocalSound(
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    SoundEvents.NOTE_BLOCK_BELL.value(),
                    SoundSource.BLOCKS,
                    1,
                    1.0f,
                    true
            );
            return;
        }

        if (currentObject instanceof BlockPos blockPos) {
            StringBuilder narration = new StringBuilder(
                    MainClass.registry(WorldNarrator.class).get(Config.getInstance().narrateCrosshair.narrator).narrate(blockPos)
            );
            if (narrateDistance) {
                narration.append(' ')
                        .append(NarrationUtils.narrateRelativePositionOfPlayerAnd(blockPos));
            }
            MainClass.narrate(narration.toString(), interrupt);
            assert client.level != null;
            client.level.playLocalSound(
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
        int newIndex = currentGroupIndex + step;

        boolean atBoundary = false;
        if (newIndex < 0) {
            newIndex = 0;
            atBoundary = true;
            MainClass.narrate(I18n.get(START_OF_LIST), true);
        } else if (newIndex >= groups.size()) {
            newIndex = groups.size() - 1;
            atBoundary = true;
            MainClass.narrate(I18n.get(END_OF_LIST), true);
        }

        POIGroup<?> nextGroup = groups.get(newIndex);

        while ((nextGroup.isEmpty()
            || nextGroup.sortByDistance().stream().noneMatch(this::isObjectValid))
                && newIndex + step >= 0
                && newIndex + step < groups.size()) {
            newIndex += step;
            nextGroup = groups.get(newIndex);
        }

        if (nextGroup.isEmpty()
                || nextGroup.sortByDistance().stream().noneMatch(this::isObjectValid)) {
            MainClass.narrate(I18n.get(step > 0 ? END_OF_LIST : START_OF_LIST), true);
            return;
        }

        currentGroup = nextGroup;

        List<?> validObjects = currentGroup.sortByDistance().stream()
                .filter(this::isObjectValid)
                .toList();

        if (validObjects.isEmpty()) {
            clearCurrentObject();
            MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_selected"), true);
            return;
        }

        currentObject = validObjects.getFirst();

        if (!atBoundary) {
            MainClass.narrate(currentGroup.getTranslatedName(), true);
        } else {
            MainClass.narrate(currentGroup.getTranslatedName(), false);
        }

        narrateCurrentObject(false);
    }

    public boolean isObjectValid(Object object) {
        if (object == null) return false;

        if (object instanceof Entity entity) {
            return entity.isAlive();
        }

        if (object instanceof BlockPos pos) {
            if (client.level == null) return false;
            return !(client.level.getBlockState(pos).getBlock() instanceof AirBlock);
        }

        return false;
    }

    private void moveObject(int step) {
        if (checkAndNarrateIfAllGroupsEmpty()) return;
        if (currentGroup != null && currentGroup.isEmpty()) clearCurrentObject();

        List<?> objects = currentGroup.sortByDistance();
        int currentObjectIndex = objects.indexOf(currentObject);

        if (currentObjectIndex == -1) {
            MainClass.narrate(I18n.get(START_OF_LIST), true);
            currentObject = objects.getFirst();
        } else {
            int newIndex = currentObjectIndex + step;
            if (newIndex < 0) {
                MainClass.narrate(I18n.get(START_OF_LIST), true);
                currentObject = objects.getFirst();
            } else if (newIndex >= objects.size()) {
                MainClass.narrate(I18n.get(END_OF_LIST), true);
                currentObject = objects.getLast();
            } else {
                currentObject = objects.get(newIndex);
            }
        }

        while (!isObjectValid(currentObject)) {
            int nextIndex = objects.indexOf(currentObject) + step;
            if (nextIndex < 0 || nextIndex >= objects.size()) {
                MainClass.narrate(I18n.get(step > 0 ? END_OF_LIST : START_OF_LIST), true);
                clearCurrentObject();
                return;
            }
            currentObject = objects.get(nextIndex);
        }

        narrateCurrentObject(false);
    }

    private boolean checkAndNarrateIfAllGroupsEmpty() {
        if (groups.isEmpty()) {
            clearCurrentObject();
            MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_found"), true);
            return true;
        } else {
            return false;
        }
    }

    private void targetNearestObject() {
        LocalPlayer player = client.player;

        List<Entity> entities = MainClass.poiManager.poiEntities.getLastScanResults()
                .stream()
                .sorted(Comparator.comparingDouble(a -> a.distanceTo(player)))
                .toList();

        List<BlockPos> blocks = MainClass.poiManager.poiBlocks.getLastScanResults()
                .stream()
                .sorted(Comparator.comparingDouble(a -> {
                    assert player != null;
                    return player.getEyePosition().distanceTo(a.getCenter());
                }))
                .toList();

        if (client.hasControlDown() && !client.hasShiftDown()) {
            if (entities.isEmpty()) {
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_found.entity"), true);
            } else {
                currentObject = entities.getFirst();
                MainClass.narrate(I18n.get("minecraft_access.point_of_interest.targeting_nearest.entity"), true);
            }
        } else if (client.hasShiftDown() && !client.hasControlDown()) {
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
        updateGroups();
    }
}
