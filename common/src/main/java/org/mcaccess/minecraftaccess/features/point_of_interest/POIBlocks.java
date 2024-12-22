package com.github.khanshoaib3.minecraft_access.features.point_of_interest;

import com.github.khanshoaib3.minecraft_access.config.config_maps.POIBlocksConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.POIMarkingConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.PlayerUtils;
import com.github.khanshoaib3.minecraft_access.utils.condition.Interval;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.*;
import java.util.function.Predicate;

/**
 * Scans the area to find exposed ore blocks, doors, buttons, ladders, etc., groups them and plays a sound only at ore blocks.
 */
@Slf4j
public class POIBlocks {
    @Getter
    private static final POIBlocks instance;
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
            Blocks.TRIAL_SPAWNER
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

    private TreeMap<Double, Vec3d> oreBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3d> doorBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3d> buttonBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3d> ladderBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3d> leverBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3d> trapDoorBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3d> fluidBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3d> otherBlocks = new TreeMap<>();
    private TreeMap<Double, Vec3d> markedBlocks = new TreeMap<>();

    private Set<BlockPos> checkedBlocks = Set.of();
    private boolean enabled;
    private boolean detectFluidBlocks;
    private int range;
    private boolean playSound;
    private float volume;
    private boolean playSoundForOtherBlocks;
    private Interval interval;
    private Predicate<BlockState> markedBlock = state -> false;
    private boolean onPOIMarkingNow = false;

    static {
        try {
            instance = new POIBlocks();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating POIBlocks instance", e);
        }
    }

    private POIBlocks() {
        loadConfigurations();
    }

    public void update(boolean onMarking, Block markedBlock) {
        try {
            this.onPOIMarkingNow = onMarking;
            if (onPOIMarkingNow) setMarkedBlock(markedBlock);
            loadConfigurations();

            if (!this.enabled) return;
            if (interval != null && !interval.isReady()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return;
            if (client.player == null) return;
            if (client.currentScreen != null) return; //Prevent running if any screen is opened
            this.player = client.player;
            this.world = client.world;

            oreBlocks = new TreeMap<>();
            doorBlocks = new TreeMap<>();
            buttonBlocks = new TreeMap<>();
            ladderBlocks = new TreeMap<>();
            leverBlocks = new TreeMap<>();
            trapDoorBlocks = new TreeMap<>();
            fluidBlocks = new TreeMap<>();
            otherBlocks = new TreeMap<>();
            markedBlocks = new TreeMap<>();

            // Player position is where player's leg be
            checkedBlocks = new HashSet<>();
            BlockPos pos = this.player.getBlockPos();
            log.debug("POIBlock started.");
            // Scan blocks exposed in the space around player
            checkBlock(pos.down(), 0);
            checkBlock(pos.up(2), 0);
            checkBlock(pos, this.range);
            checkBlock(pos.up(), this.range);
            log.debug("POIBlock ended.");

        } catch (Exception e) {
            log.error("Error encountered in poi blocks feature.", e);
        }
    }

    private void loadConfigurations() {
        POIBlocksConfigMap poiBlocksConfigMap = POIBlocksConfigMap.getInstance();
        this.enabled = poiBlocksConfigMap.isEnabled();
        this.detectFluidBlocks = poiBlocksConfigMap.isDetectFluidBlocks();
        this.range = poiBlocksConfigMap.getRange();
        this.playSound = poiBlocksConfigMap.isPlaySound();
        this.volume = poiBlocksConfigMap.getVolume();
        this.playSoundForOtherBlocks = poiBlocksConfigMap.isPlaySoundForOtherBlocks();
        this.interval = Interval.inMilliseconds(poiBlocksConfigMap.getDelay(), this.interval);
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

        String soundType = checkAndPutIntoMap(blockPos, blockState);
        playSoundAtBlock(blockPos, soundType);
    }

    private String checkAndPutIntoMap(BlockPos blockPos, BlockState blockState) {
        String soundType = "";
        Block block = blockState.getBlock();
        Vec3d blockVec3dPos = blockPos.toCenterPos();
        Vec3d playerEyePos = this.player.getEyePos(); // post 1.17
//        Vec3d playerVec3dPos = (new Vec3d(client.player.getX(), client.player.getEyeY(), client.player.getZ())); // pre 1.17

        double diff = playerEyePos.distanceTo(blockVec3dPos);

        if (markedBlock.test(blockState)) {
            markedBlocks.put(diff, blockVec3dPos);
            soundType = "mark";
        } else if (this.detectFluidBlocks && block instanceof FluidBlock && PlayerUtils.isNotInFluid()) {
            if (this.world.getFluidState(blockPos).getLevel() == 8) {
                fluidBlocks.put(diff, blockVec3dPos);
                soundType = "blocks";
            }
        } else if (oreBlockPredicates.stream().anyMatch($ -> $.test(blockState))) {
            oreBlocks.put(diff, blockVec3dPos);
            soundType = "ore";
        } else if (block instanceof ButtonBlock) {
            buttonBlocks.put(diff, blockVec3dPos);
            soundType = "blocks";
        } else if (block instanceof LadderBlock) {
            ladderBlocks.put(diff, blockVec3dPos);
            soundType = "blocks";
        } else if (block instanceof TrapdoorBlock) {
            trapDoorBlocks.put(diff, blockVec3dPos);
            soundType = "blocks";
        } else if (block instanceof LeverBlock) {
            leverBlocks.put(diff, blockVec3dPos);
            soundType = "blocks";
        } else if (block instanceof DoorBlock) {
            Set<Map.Entry<Property<?>, Comparable<?>>> entries = blockState.getEntries().entrySet();
            for (Map.Entry<Property<?>, Comparable<?>> i : entries) {

                if (i.getKey().getName().equals("half")) {
                    if (i.getValue().toString().equals("upper")) {
                        doorBlocks.put(diff, blockVec3dPos);
                        soundType = "blocks";
                    }
                    break;
                }

            }
        } else if (poiBlockPredicates.stream().anyMatch($ -> $.test(blockState))) {
            otherBlocks.put(diff, blockVec3dPos);
            soundType = "blocks";
        } else if (blockState.createScreenHandlerFactory(this.world, blockPos) != null) {
            otherBlocks.put(diff, blockVec3dPos);
            soundType = "blocksWithInterface";
        }
        return soundType;
    }

    private void playSoundAtBlock(BlockPos blockPos, String soundType) {
        boolean playSound = this.playSound && !soundType.isEmpty() && this.volume != 0;
        if (!playSound) return;

        String coordinates = "x:%d y:%d z%d".formatted(blockPos.getX(), blockPos.getY(), blockPos.getZ());

        if (onPOIMarkingNow) {
            if (soundType.equalsIgnoreCase("mark")) {
                log.debug("Play sound at [{}]", coordinates);
                this.world.playSound(this.player, blockPos, SoundEvents.ENTITY_ITEM_PICKUP,
                        SoundCategory.BLOCKS, volume, -5f);
                return;
            } else if (POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled()) {
                log.debug("Suppress sound at [{}]", coordinates);
                return;
            }
        }

        log.debug("Play sound at [{}]", coordinates);

        if (soundType.equalsIgnoreCase("ore"))
            this.world.playSound(this.player, blockPos, SoundEvents.ENTITY_ITEM_PICKUP,
                    SoundCategory.BLOCKS, volume, -5f);
        else if (this.playSoundForOtherBlocks && soundType.equalsIgnoreCase("blocks"))
            this.world.playSound(this.player, blockPos, SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(),
                    SoundCategory.BLOCKS, volume, 2f);
        else if (this.playSoundForOtherBlocks && soundType.equalsIgnoreCase("blocksWithInterface"))
            this.world.playSound(this.player, blockPos, SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(),
                    SoundCategory.BLOCKS, volume, 0f);
    }

    private void setMarkedBlock(Block block) {
        this.markedBlock = block == null ? s -> false : s -> s.isOf(block);
    }

    public List<TreeMap<Double, Vec3d>> getLockingCandidates() {
        if (onPOIMarkingNow) {
            if (POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled()) {
                return List.of(markedBlocks);
            } else {
                return List.of(markedBlocks, doorBlocks, buttonBlocks, ladderBlocks, leverBlocks, trapDoorBlocks, otherBlocks, oreBlocks, fluidBlocks);
            }
        } else {
            return List.of(doorBlocks, buttonBlocks, ladderBlocks, leverBlocks, trapDoorBlocks, otherBlocks, oreBlocks, fluidBlocks);
        }
    }
}