package org.mcaccess.minecraftaccess.features.point_of_interest;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.mcaccess.minecraftaccess.config.config_maps.POIBlocksConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.POIMarkingConfigMap;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import java.util.*;

/**
 * Scans the area to find exposed ore blocks, doors, buttons, ladders, etc., groups them and plays a sound only at ore blocks.
 */
@Slf4j
public class POIBlocks {
    @Getter
    private static final POIBlocks instance = new POIBlocks();
    private LocalPlayer player;
    private ClientLevel world;

    private Set<BlockPos> checkedBlocks = Set.of();
    private boolean enabled;
    private boolean detectFluidBlocks;
    private int range;
    private boolean playSound;
    private float volume;
    private boolean playSoundForOtherBlocks;
    private final Interval interval = Interval.defaultDelay();
    private @Nullable Block markedBlock = null;
    private boolean isMarking = false;

    private final POIGroup<BlockPos> markedGroup = new POIGroup<>(
        "minecraft_access.point_of_interest.group.markedBlock",
        new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5f),
        pos -> isMarking && world.getBlockState(pos).is(markedBlock)
    );

    private final POIGroup<BlockPos> oreGroup = BuiltinBlockPOIGroups.ORE.group;

    private final POIGroup<BlockPos> otherBlocksGroup = new POIGroup<>(
        "minecraft_access.point_of_interest.group.otherBlocks",
        pos -> otherBlocksFilter(pos)
    );

    private boolean otherBlocksFilter(BlockPos pos) {
        boolean blockAlreadyInGroup = false;

        for (BlockPos posInGroup : otherBlocksGroup.getItems()) {
            blockAlreadyInGroup = world.getBlockState(pos).getBlock() == world.getBlockState(posInGroup).getBlock();
            if (blockAlreadyInGroup) break;
        }

        return !world.getBlockState(pos).isAir() && !blockAlreadyInGroup;
    }

    @SuppressWarnings("unchecked")
    public final POIGroup<BlockPos>[] groups = new POIGroup[] {
        markedGroup,
        oreGroup,
        new POIGroup<BlockPos>(// Doors
            "minecraft_access.point_of_interest.group.door",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2f),
            pos -> {
                if (world.getBlockState(pos).getBlock() instanceof DoorBlock) {
                    return world.getBlockState(pos).getValue(DoorBlock.HALF).equals(DoubleBlockHalf.UPPER);
                } else if (world.getBlockState(pos).getBlock() instanceof TrapDoorBlock) return true;

                return false;
            }
        ),
        new POIGroup<BlockPos>(// Fluids
            "minecraft_access.point_of_interest.group.fluid",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BIT.value(), 2f),
            pos -> this.detectFluidBlocks && world.getBlockState(pos).getBlock() instanceof LiquidBlock &&
                PlayerUtils.isNotInFluid() && world.getFluidState(pos).getAmount() == 8
        ),
        BuiltinBlockPOIGroups.FUNCTIONAL.group,
        new POIGroup<BlockPos>(// Blocks with interface
            "minecraft_access.point_of_interest.group.gui",
            new POIGroup.Sound(SoundEvents.NOTE_BLOCK_BANJO.value(), 0f),
            pos -> {
                if (world.getBlockState(pos).getBlock() instanceof ChestBlock) {
                    return world.getBlockState(pos).getValue(ChestBlock.TYPE).equals(ChestType.SINGLE) ||
                        world.getBlockState(pos).getValue(ChestBlock.TYPE).equals(ChestType.RIGHT);
                } else {
                    return world.getBlockState(pos).getMenuProvider(world, pos) != null;
                }
            }
        ),
        otherBlocksGroup, // This group should always be at the end of this list
    };

    private POIBlocks() {
        loadConfigurations();
    }

    @Getter
    private List<BlockPos> lastScanResults = new ArrayList<>();

    private List<BlockPos> currentScanResults = new ArrayList<>();

    public void update(boolean isMarking, Block markedBlock) {
        this.isMarking = isMarking;
        if (isMarking) setMarkedBlock(markedBlock);
        loadConfigurations();

        if (!enabled) return;
        if (!interval.isReady()) return;

        Minecraft client = Minecraft.getInstance();
        if (client == null) return;
        if (client.player == null) return;
        if (client.screen != null) return; //Prevent running if any screen is opened
        player = client.player;
        world = client.level;

        for (POIGroup<BlockPos> group : groups) {
            group.clear();
        }

        currentScanResults = new ArrayList<>();

        // Player position is where player's leg be
        checkedBlocks = new HashSet<>();
        BlockPos pos = player.blockPosition();
        log.debug("POIBlock started.");
        // Scan blocks exposed in the space around player
        checkBlock(pos.below(), 0);
        checkBlock(pos.above(2), 0);
        checkBlock(pos, this.range);
        checkBlock(pos.above(), this.range);

        lastScanResults = currentScanResults;

        if (isMarking && POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled()) {
            markedGroup.playSoundForGroupItems(BlockPos::getCenter, volume);
        } else if (playSound && !playSoundForOtherBlocks) {
            oreGroup.playSoundForGroupItems(BlockPos::getCenter, volume);
        } else if (playSound) {
            for (POIGroup<BlockPos> group : groups) {
                group.playSoundForGroupItems(BlockPos::getCenter, volume);
            }
        }

        log.debug("POIBlock ended.");
    }

    private void loadConfigurations() {
        POIBlocksConfigMap poiBlocksConfigMap = POIBlocksConfigMap.getInstance();
        this.enabled = poiBlocksConfigMap.isEnabled();
        this.detectFluidBlocks = poiBlocksConfigMap.isDetectFluidBlocks();
        this.range = poiBlocksConfigMap.getRange();
        this.playSound = poiBlocksConfigMap.isPlaySound();
        this.volume = poiBlocksConfigMap.getVolume();
        this.playSoundForOtherBlocks = poiBlocksConfigMap.isPlaySoundForOtherBlocks();
        this.interval.setDelay(poiBlocksConfigMap.getDelay(), Interval.Unit.Millisecond);
    }

    private void checkBlock(BlockPos blockPos, int val) {
        if (checkedBlocks.contains(blockPos)) return;
        checkedBlocks.add(blockPos);

        BlockState blockState = this.world.getBlockState(blockPos);

        // This checkBlock method is a DFS method.
        // In fact this isAir() condition makes the scan scope become dynamic and flexible,
        // it always fits into space (filled with Air Block) around the player.
        int vSubOne = val - 1;
        if (blockState.isAir() && vSubOne >= 0) {
            checkBlock(blockPos.north(), vSubOne);
            checkBlock(blockPos.south(), vSubOne);
            checkBlock(blockPos.west(), vSubOne);
            checkBlock(blockPos.east(), vSubOne);
            checkBlock(blockPos.above(), vSubOne);
            checkBlock(blockPos.below(), vSubOne);
            // Air block is not a valid POI block, so return early
            return;
        }

        for (POIGroup<BlockPos> group : groups) {
            if (group.addIfQualified(blockPos) && group != otherBlocksGroup) {
                currentScanResults.add(blockPos);
                break;
            }
        }
    }

    private void setMarkedBlock(@Nullable Block block) {
        markedBlock = block;
    }

    public @UnmodifiableView List<BlockPos> getLockingCandidates() {
        if (isMarking && POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled()) {
            return markedGroup.getItems();
        }

        return Arrays.stream(groups).flatMap(group -> group.getItems().stream()).toList();
    }
}
