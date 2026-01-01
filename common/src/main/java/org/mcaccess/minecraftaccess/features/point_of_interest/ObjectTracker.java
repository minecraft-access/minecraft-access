package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.blaze3d.platform.InputConstants;
import lombok.Getter;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.blay09.mods.kuma.api.InputBinding;
import net.blay09.mods.kuma.api.KeyModifier;
import net.blay09.mods.kuma.api.KeyModifiers;
import net.blay09.mods.kuma.api.Kuma;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.WorldNarrator;
import org.mcaccess.minecraftaccess.utils.KeyMappingCategories;
import org.mcaccess.minecraftaccess.utils.NarrationUtils;

public class ObjectTracker implements BalmClientModule {
    public static final String START_OF_LIST = "minecraft_access.other.start_of_list";
    public static final String END_OF_LIST = "minecraft_access.other.end_of_list";

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
        ClientTickCallback.ClientLevelTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            currentObject = null;
            currentGroup = null;
            groups = new ArrayList<>();
        });

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "object_tracker.narrate_object"))
                .withDefault(InputBinding.key(InputConstants.KEY_HOME))
                .overrideCategory(KeyMappingCategories.OBJECT_TRACKER)
                .handleWorldInput(event -> {
                    narrateCurrentObject(true);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "object_tracker.target_nearest/any"))
                .withDefault(InputBinding.key(InputConstants.KEY_END))
                .overrideCategory(KeyMappingCategories.OBJECT_TRACKER)
                .handleWorldInput(event -> {
                    targetNearestAny();
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "object_tracker.target_nearest/block"))
                .withDefault(InputBinding.key(InputConstants.KEY_END, KeyModifiers.of(KeyModifier.SHIFT)))
                .overrideCategory(KeyMappingCategories.OBJECT_TRACKER)
                .handleWorldInput(event -> {
                    targetNearestBlock();
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "object_tracker.target_nearest/entity"))
                .withDefault(InputBinding.key(InputConstants.KEY_END, KeyModifiers.of(KeyModifier.CONTROL)))
                .overrideCategory(KeyMappingCategories.OBJECT_TRACKER)
                .handleWorldInput(event -> {
                    targetNearestEntity();
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "object_tracker.move_item/previous"))
                .withDefault(InputBinding.key(InputConstants.KEY_PAGEUP))
                .overrideCategory(KeyMappingCategories.OBJECT_TRACKER)
                .handleWorldInput(event -> {
                    moveObject(-1);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "object_tracker.move_item/next"))
                .withDefault(InputBinding.key(InputConstants.KEY_PAGEDOWN))
                .overrideCategory(KeyMappingCategories.OBJECT_TRACKER)
                .handleWorldInput(event -> {
                    moveObject(1);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "object_tracker.move_group/previous"))
                .withDefault(InputBinding.key(InputConstants.KEY_PAGEUP, KeyModifiers.of(KeyModifier.CONTROL)))
                .overrideCategory(KeyMappingCategories.OBJECT_TRACKER)
                .handleWorldInput(event -> {
                    moveGroup(-1);
                    return true;
                })
                .build();

        Kuma.createKeyMapping(Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "object_tracker.move_group/next"))
                .withDefault(InputBinding.key(InputConstants.KEY_PAGEDOWN, KeyModifiers.of(KeyModifier.CONTROL)))
                .overrideCategory(KeyMappingCategories.OBJECT_TRACKER)
                .handleWorldInput(event -> {
                    moveGroup(1);
                    return true;
                })
                .build();
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

    private void tick(Level level) {
        if (Minecraft.getInstance().screen != null) return;

        updateGroups();
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
            assert Minecraft.getInstance().level != null;
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
            assert Minecraft.getInstance().level != null;
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
            if (Minecraft.getInstance().level == null) return false;
            return !(Minecraft.getInstance().level.getBlockState(pos).getBlock() instanceof AirBlock);
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

    private void targetNearestAny() {
        Minecraft client = Minecraft.getInstance();
        assert client.player != null;

        List<Entity> entities = MainClass.poiManager.poiEntities.getLastScanResults()
                .stream()
                .sorted(Comparator.comparingDouble(a -> a.distanceTo(client.player)))
                .toList();

        List<BlockPos> blocks = MainClass.poiManager.poiBlocks.getLastScanResults()
                .stream()
                .sorted(Comparator.comparingDouble(a -> client.player.getEyePosition().distanceTo(a.getCenter())))
                .toList();

        if (entities.isEmpty() && blocks.isEmpty()) {
            MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_found"), true);
            return;
        }

        if (entities.isEmpty()) {
            currentObject = blocks.getFirst();
        } else if (blocks.isEmpty()) {
            currentObject = entities.getFirst();
        } else if (client.player.getEyePosition().distanceTo(blocks.getFirst().getCenter())
                < client.player.distanceTo(entities.getFirst())) {
            currentObject = blocks.getFirst();
        } else {
            currentObject = entities.getFirst();
        }

        MainClass.narrate(I18n.get("minecraft_access.point_of_interest.targeting_nearest"), true);
        narrateCurrentObject(false);
    }

    private void targetNearestBlock() {
        Minecraft client = Minecraft.getInstance();
        assert client.player != null;

        List<BlockPos> blocks = MainClass.poiManager.poiBlocks.getLastScanResults()
                .stream()
                .sorted(Comparator.comparingDouble(a -> client.player.getEyePosition().distanceTo(a.getCenter())))
                .toList();

        if (blocks.isEmpty()) {
            MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_found.block"), true);
            return;
        }

        currentObject = blocks.getFirst();
        MainClass.narrate(I18n.get("minecraft_access.point_of_interest.targeting_nearest.block"), true);
        narrateCurrentObject(false);
    }

    private void targetNearestEntity() {
        Minecraft client = Minecraft.getInstance();
        assert client.player != null;

        List<Entity> entities = MainClass.poiManager.poiEntities.getLastScanResults()
                .stream()
                .sorted(Comparator.comparingDouble(a -> a.distanceTo(client.player)))
                .toList();

        if (entities.isEmpty()) {
            MainClass.narrate(I18n.get("minecraft_access.point_of_interest.not_found.entity"), true);
            return;
        }

        currentObject = entities.getFirst();
        MainClass.narrate(I18n.get("minecraft_access.point_of_interest.targeting_nearest.entity"), true);
        narrateCurrentObject(false);
    }

    public void clearCurrentObject() {
        currentObject = null;
        updateGroups();
    }
}
