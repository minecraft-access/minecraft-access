package org.mcaccess.minecraftaccess.features.point_of_interest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.config.config_maps.POIBlocksConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.POIMarkingConfigMap;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

/**
 * Scans the area to find exposed ore blocks, doors, buttons, ladders, etc., groups them and plays a sound only at ore blocks.
 */
@Slf4j
public class POIBlocks {
    @Getter
    private static final POIBlocks instance = new POIBlocks();
    private boolean enabled;
    private int range;
    private boolean playSound;
    private float volume;
    private boolean playSoundForOtherNonOreBlocks;
    private final Interval interval = Interval.defaultDelay();
    private @Nullable Block markedBlock = null;

    private final POIGroup<BlockPos> markedGroup = new POIGroup<>(
        "minecraft_access.point_of_interest.group.markedBlock",
        new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5f),
            pos -> WorldUtils.getBlockState(pos).is(markedBlock)
    );

    /**
     * This group contains the closest of every type of block
     * that wasn't picked up by any other POI group around the player.
     * This is useful when trying to find something that is not considered a POI,
     * for example until we make a proper trees category, this is a decent way to find trees.
     */
    private final POIGroup<BlockPos> otherBlocksGroup = new POIGroup<>(
        "minecraft_access.point_of_interest.group.otherBlocks",
            this::blockIsNotAirAndNotContainedInGroupYet
    );

    private boolean blockIsNotAirAndNotContainedInGroupYet(BlockPos pos) {
        BlockState state = WorldUtils.getBlockState(pos);
        boolean blockAlreadyInGroup = otherBlocksGroup.getItems().stream()
                .map(p -> WorldUtils.getBlockState(p).getBlock())
                .anyMatch(t -> t.equals(state.getBlock()));
        return !state.isAir() && !blockAlreadyInGroup;
    }

    @SuppressWarnings("unchecked")
    public final POIGroup<BlockPos>[] groups = Stream.of(List.of(markedGroup), BuiltinBlockPOIGroups.ALL, List.of(otherBlocksGroup))
            .flatMap(Collection::stream).toArray(POIGroup[]::new);

    private POIBlocks() {
        loadConfigurations();
    }

    @Getter
    private List<BlockPos> lastScanResults = new ArrayList<>();

    public void update(boolean isMarking, Block markedBlock) {
        if (isMarking) setMarkedBlock(markedBlock);
        loadConfigurations();

        if (!enabled) return;
        if (!interval.isReady()) return;

        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        if (client.screen != null) return; //Prevent running if any screen is opened

        log.debug("POIBlock started.");
        scanBlocksAroundPlayer();
        playerSoundAtFoundPOI(isMarking);
        log.debug("POIBlock ended.");
    }

    private void scanBlocksAroundPlayer() {
        // initialize
        List<BlockPos> currentScanResults = new ArrayList<>();
        for (POIGroup<BlockPos> group : groups) {
            group.clear();
        }

        // Scan blocks exposed in the space around player, add them into qualified groups
        BlockScanner scanner = new BlockScanner(blockPos -> {
            for (POIGroup<BlockPos> group : groups) {
                if (group.addIfQualified(blockPos) && group != otherBlocksGroup) {
                    currentScanResults.add(blockPos);
                    break;
                }
            }
        });

        // where player's leg be
        BlockPos pos = WorldUtils.getClientPlayer().blockPosition();
        scanner.scanAndQualifyBlocksExposedInAirAround(pos.below(), 0);
        scanner.scanAndQualifyBlocksExposedInAirAround(pos.above(2), 0);
        scanner.scanAndQualifyBlocksExposedInAirAround(pos, this.range);
        scanner.scanAndQualifyBlocksExposedInAirAround(pos.above(), this.range);

        lastScanResults = currentScanResults;
    }

    private void playerSoundAtFoundPOI(boolean isMarking) {
        if (volume == 0f) return;
        if (isMarking && POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled()) {
            markedGroup.playSoundForGroupItems(BlockPos::getCenter, volume);
        } else if (playSound) {
            if (playSoundForOtherNonOreBlocks) {
                for (POIGroup<BlockPos> group : groups) {
                    group.playSoundForGroupItems(BlockPos::getCenter, volume);
                }
            } else {
                BuiltinBlockPOIGroups.ORE.group.playSoundForGroupItems(BlockPos::getCenter, volume);
            }
        }
    }

    private void loadConfigurations() {
        POIBlocksConfigMap poiBlocksConfigMap = POIBlocksConfigMap.getInstance();
        this.enabled = poiBlocksConfigMap.isEnabled();
        this.range = poiBlocksConfigMap.getRange();
        this.playSound = poiBlocksConfigMap.isPlaySound();
        this.volume = poiBlocksConfigMap.getVolume();
        this.playSoundForOtherNonOreBlocks = poiBlocksConfigMap.isPlaySoundForOtherBlocks();
        this.interval.setDelay(poiBlocksConfigMap.getDelay(), Interval.Unit.Millisecond);
    }

    private void setMarkedBlock(@Nullable Block block) {
        markedBlock = block;
    }
}
