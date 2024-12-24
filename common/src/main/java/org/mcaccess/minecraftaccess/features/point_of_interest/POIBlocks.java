package org.mcaccess.minecraftaccess.features.point_of_interest;

import org.mcaccess.minecraftaccess.config.config_maps.POIBlocksConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.POIBlocksConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.POIMarkingConfigMap;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
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

    private Set<BlockPos> checkedBlocks = Set.of();
    private boolean enabled;
    private boolean detectFluidBlocks;
    private int range;
    private boolean playSound;
    private float volume;
    private boolean playSoundForOtherBlocks;
    private final Interval interval = Interval.defaultDelay();
    private Predicate<BlockState> markedBlock = state -> false;
    private boolean onPOIMarkingNow = false;

    public Map<String, POIGroup> builtInGroups = new LinkedHashMap<>();

    {
        builtInGroups.put("marked", new POIGroup("Marked blocks", SoundEvents.ENTITY_ITEM_PICKUP, -5f, null, (block, pos) ->
        onPOIMarkingNow && markedBlock.test(block))
        );

        builtInGroups.put("ore", new POIGroup("Ores", SoundEvents.ENTITY_ITEM_PICKUP, -5f, null, (block, pos) ->
            oreBlockPredicates.stream().anyMatch(p -> p.test(block)))
        );

        builtInGroups.put("functional", new POIGroup("Functional blocks", SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), 2f, null, (block, pos) ->
            block.getBlock() instanceof ButtonBlock || block.getBlock() instanceof LeverBlock || poiBlockPredicates.stream().anyMatch(p -> p.test(block)))
        );

        builtInGroups.put("gui", new POIGroup("Blocks with interface", SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(), 0f, null, (block, pos) ->
            block.createScreenHandlerFactory(world, pos) != null)
        );

        builtInGroups.put("fluid", new POIGroup("Fluids", SoundEvents.BLOCK_NOTE_BLOCK_BIT.value(), 2f, null, (block, pos) ->
        this.detectFluidBlocks && block.getBlock() instanceof FluidBlock && PlayerUtils.isNotInFluid() && block.getFluidState().getLevel() == 8)
    );
    }

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

    @Getter
    private List<BlockPos> lastScanResults = new ArrayList<>();

    public void update(boolean onMarking, Block markedBlock) {
        try {
            this.onPOIMarkingNow = onMarking;
            if (onPOIMarkingNow) setMarkedBlock(markedBlock);
            loadConfigurations();

            if (!this.enabled) return;
            if (!interval.isReady()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client == null) return;
            if (client.player == null) return;
            if (client.currentScreen != null) return; //Prevent running if any screen is opened
            this.player = client.player;
            this.world = client.world;

            for (POIGroup group : builtInGroups.values()) {
                group.clearBlocks();
            }

            lastScanResults = new ArrayList<>();

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

        boolean shouldPlayMarkedOnly = onPOIMarkingNow && POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled();

        for (POIGroup group : builtInGroups.values()) {
            if (group.checkAndAddBlock(blockState, blockPos)) {
                lastScanResults.add(blockPos);
                if (playSound && playSoundForOtherBlocks && !shouldPlayMarkedOnly) {
                    world.playSound(player, blockPos, group.sound, SoundCategory.BLOCKS, volume, group.soundPitch);
                }
            }
        }

        if (playSound && !playSoundForOtherBlocks && !shouldPlayMarkedOnly) {
            POIGroup oreGroup = builtInGroups.get("ore");

            for (BlockPos pos : oreGroup.getBlocks().keySet()) {
                world.playSound(player, pos, oreGroup.sound, SoundCategory.BLOCKS, volume, oreGroup.soundPitch);
            }
        }

        if (shouldPlayMarkedOnly) {
            POIGroup markedGroup = builtInGroups.get("marked");

            for (BlockPos pos : markedGroup.getBlocks().keySet()) {
                world.playSound(player, pos, markedGroup.sound, SoundCategory.BLOCKS, volume, markedGroup.soundPitch);
            }
        }
    }

    private void setMarkedBlock(Block block) {
        this.markedBlock = block == null ? s -> false : s -> s.isOf(block);
    }

    public List<TreeMap<Double, Vec3d>> getLockingCandidates() {
        List<TreeMap<Double, Vec3d>> results = new ArrayList<>();

        if (onPOIMarkingNow && POIMarkingConfigMap.getInstance().isSuppressOtherWhenEnabled()) {
            results.add(builtInGroups.get("marked").getBlocks(true));
            return results;
        }

        for (POIGroup group : builtInGroups.values()) {
            results.add(group.getBlocks(true));
        }

        return results;
   }
}
