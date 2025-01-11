package org.mcaccess.minecraftaccess.features.point_of_interest;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;
import org.mcaccess.minecraftaccess.config.config_maps.POIBlocksConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.POIMarkingConfigMap;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.*;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;

import java.util.function.Predicate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scans the area to find exposed ore blocks, doors, buttons, ladders, etc., groups them and plays a sound only at ore blocks.
 */
@Slf4j
public class POIBlocks {
    @Getter
    private static final POIBlocks instance = new POIBlocks();
    private ClientPlayerEntity player;
    private ClientWorld world;

    private static final Block[] POI_BLOCKS = new Block[]{
            Blocks.PISTON,
            Blocks.STICKY_PISTON,
            Blocks.RESPAWN_ANCHOR,
            Blocks.BELL,
            Blocks.OBSERVER,
            Blocks.DAYLIGHT_DETECTOR,
            Blocks.JUKEBOX,
            Blocks.LODESTONE,
            Blocks.BEE_NEST,
            Blocks.COMPOSTER,
            Blocks.OBSERVER,
            Blocks.SCULK_SHRIEKER,
            Blocks.SCULK_CATALYST,
            Blocks.CALIBRATED_SCULK_SENSOR,
            Blocks.SCULK_SENSOR,
            Blocks.VAULT,
            Blocks.TRIAL_SPAWNER,
            Blocks.SPAWNER,
            Blocks.CREAKING_HEART,
    };

    private static final List<Predicate<BlockState>> poiBlockPredicates = Arrays.stream(POI_BLOCKS)
            .map(b -> (Predicate<BlockState>) state -> state.isOf(b))
            .toList();

    private static final Block[] ORE_BLOCKS = new Block[]{
            Blocks.COAL_ORE,
            Blocks.DEEPSLATE_COAL_ORE,
            Blocks.COPPER_ORE,
            Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.DIAMOND_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.EMERALD_ORE,
            Blocks.DEEPSLATE_EMERALD_ORE,
            Blocks.GOLD_ORE,
            Blocks.DEEPSLATE_GOLD_ORE,
            Blocks.NETHER_GOLD_ORE,
            Blocks.IRON_ORE,
            Blocks.DEEPSLATE_IRON_ORE,
            Blocks.LAPIS_ORE,
            Blocks.DEEPSLATE_LAPIS_ORE,
            Blocks.REDSTONE_ORE,
            Blocks.DEEPSLATE_REDSTONE_ORE,
            Blocks.NETHER_QUARTZ_ORE,
            Blocks.ANCIENT_DEBRIS
    };

    private static final List<Predicate<BlockState>> oreBlockPredicates = Arrays.stream(ORE_BLOCKS)
            .map(b -> (Predicate<BlockState>) state -> state.isOf(b))
            .toList();

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
    () -> I18n.translate("minecraft_access.point_of_interest.group.markedBlock"),
    SoundEvents.ENTITY_ITEM_PICKUP,
            -5f,
            pos -> isMarking && world.getBlockState(pos).isOf(markedBlock)
    );

    private final POIGroup<BlockPos> oreGroup = new POIGroup<>(
        () -> I18n.translate("minecraft_access.point_of_interest.group.ore"),
    SoundEvents.ENTITY_ITEM_PICKUP,
            -5f,
            pos -> oreBlockPredicates.stream().anyMatch(p -> p.test(world.getBlockState(pos)))
    );

    private final POIGroup<BlockPos> otherBlocksGroup = new POIGroup<>(
        () -> I18n.translate("minecraft_access.point_of_interest.group.otherBlocks"),
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
            new POIGroup<BlockPos>(
                () -> I18n.translate("minecraft_access.point_of_interest.group.door"),
                SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(),
                2f,
                pos -> {
                    if (world.getBlockState(pos).getBlock() instanceof DoorBlock) return world.getBlockState(pos).get(DoorBlock.HALF).equals(DoubleBlockHalf.UPPER);
                    else if (world.getBlockState(pos).getBlock() instanceof TrapdoorBlock) return true;

                    return false;
                }
            ),
            new POIGroup<BlockPos>(// Fluids
            () -> I18n.translate("minecraft_access.point_of_interest.group.fluid"),
            SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(),
                    2f,
                    pos -> this.detectFluidBlocks && world.getBlockState(pos).getBlock() instanceof FluidBlock && PlayerUtils.isNotInFluid() && world.getFluidState(pos).getLevel() == 8
            ),
            new POIGroup<BlockPos>(// Functional blocks
            () -> I18n.translate("minecraft_access.point_of_interest.group.functional"),
            SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(),
                    2f,
                    pos -> world.getBlockState(pos).getBlock() instanceof ButtonBlock || world.getBlockState(pos).getBlock() instanceof LeverBlock || poiBlockPredicates.stream().anyMatch(p -> p.test(world.getBlockState(pos)))
            ),
            new POIGroup<BlockPos>(// Blocks with interface
            () -> I18n.translate("minecraft_access.point_of_interest.group.gui"),
            SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(),
                    0f,
                    pos -> world.getBlockState(pos).createScreenHandlerFactory(world, pos) != null
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

        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        if (client.player == null) return;
        if (client.currentScreen != null) return; //Prevent running if any screen is opened
        player = client.player;
        world = client.world;

        for (POIGroup<BlockPos> group : groups) {
            group.clear();
        }

        currentScanResults = new ArrayList<>();

        // Player position is where player's leg be
        checkedBlocks = new HashSet<>();
        BlockPos pos = player.getBlockPos();
        log.debug("POIBlock started.");
        // Scan blocks exposed in the space around player
        checkBlock(pos.down(), 0);
        checkBlock(pos.up(2), 0);
        checkBlock(pos, this.range);
        checkBlock(pos.up(), this.range);

        lastScanResults = currentScanResults;

        if (isMarking && POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled()) {
            for (BlockPos blockPos : markedGroup.getItems()) {
                markedGroup.playSound(blockPos.toCenterPos(), volume);
            }
        } else if (playSound && !playSoundForOtherBlocks) {
            for (BlockPos blockPos : oreGroup.getItems()) {
                oreGroup.playSound(blockPos.toCenterPos(), volume);
            }
        } else if (playSound) {
            for (POIGroup<BlockPos> group : groups) {
                for (BlockPos blockPos : group.getItems()) {
                    group.playSound(blockPos.toCenterPos(), volume);
                }
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
            checkBlock(blockPos.up(), vSubOne);
            checkBlock(blockPos.down(), vSubOne);
            // Air block is not a valid POI block, so return early
            return;
        }

        for (POIGroup<BlockPos> group : groups) {
            if (group.add(blockPos) && group != otherBlocksGroup) {
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
