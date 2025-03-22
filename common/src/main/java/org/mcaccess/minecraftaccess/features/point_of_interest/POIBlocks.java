package org.mcaccess.minecraftaccess.features.point_of_interest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
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
import java.util.List;

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

    private static final POIGroup<BlockPos> ORE_GROUP = BuiltinBlockPOIGroups.ORE.group;

    private final POIGroup<BlockPos> markedGroup = new POIGroup<>(
        "minecraft_access.point_of_interest.group.markedBlock",
        new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5f),
            pos -> WorldUtils.getBlockState(pos).is(markedBlock)
    );

    private final POIGroup<BlockPos> otherBlocksGroup = new POIGroup<>(
        "minecraft_access.point_of_interest.group.otherBlocks",
            this::otherBlocksFilter
    );

    private boolean otherBlocksFilter(BlockPos pos) {
        boolean blockAlreadyInGroup = false;

        BlockState state = WorldUtils.getBlockState(pos);
        for (BlockPos posInGroup : otherBlocksGroup.getItems()) {
            blockAlreadyInGroup = state.getBlock() == WorldUtils.getBlockState(posInGroup).getBlock();
            if (blockAlreadyInGroup) break;
        }

        return !state.isAir() && !blockAlreadyInGroup;
    }

    @SuppressWarnings("unchecked")
    public final POIGroup<BlockPos>[] groups = new POIGroup[] {
            markedGroup,
            ORE_GROUP,
            BuiltinBlockPOIGroups.DOOR.group,
            BuiltinBlockPOIGroups.FLUID.group,
            BuiltinBlockPOIGroups.FUNCTIONAL.group,
            BuiltinBlockPOIGroups.HAVE_INTERFACE.group,
            otherBlocksGroup, // This group should always be at the end of this list
    };

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

        scanBlocksAround(client.player);

        if (isMarking && POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled()) {
            markedGroup.playSoundForGroupItems(BlockPos::getCenter, volume);
        } else if (playSound && !playSoundForOtherNonOreBlocks) {
            ORE_GROUP.playSoundForGroupItems(BlockPos::getCenter, volume);
        } else if (playSound) {
            for (POIGroup<BlockPos> group : groups) {
                group.playSoundForGroupItems(BlockPos::getCenter, volume);
            }
        }

        log.debug("POIBlock ended.");
    }

    private void scanBlocksAround(LocalPlayer player) {
        log.debug("POIBlock started.");

        // Player position is where player's leg be
        BlockPos pos = player.blockPosition();

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
        scanner.scanAndQualifyBlocksExposedInAirAround(pos.below(), 0);
        scanner.scanAndQualifyBlocksExposedInAirAround(pos.above(2), 0);
        scanner.scanAndQualifyBlocksExposedInAirAround(pos, this.range);
        scanner.scanAndQualifyBlocksExposedInAirAround(pos.above(), this.range);

        lastScanResults = currentScanResults;
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
