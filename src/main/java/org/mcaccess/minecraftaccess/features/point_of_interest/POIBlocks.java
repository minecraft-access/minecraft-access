package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.event.callback.ClientLifecycleCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
import org.mcaccess.minecraftaccess.utils.events.ClientPlayingTick;

/**
 * Scans the area to find exposed ore blocks, doors, buttons, ladders, etc., groups them and plays a sound only at ore blocks.
 */
@Slf4j
public class POIBlocks implements BalmClientModule {
    private static final Config.POI.Blocks CONFIG = Config.getInstance().poi.blocks;
    private final Interval interval = Interval.defaultDelay();
    private @Nullable Block markedBlock = null;

    private final POIGroup<BlockPos> markedGroup = new POIGroup<>(
            "minecraft_access.point_of_interest.group.markedBlock",
            new POIGroup.Sound(SoundEvents.ITEM_PICKUP, -5.0f),
            pos -> {
                assert Minecraft.getInstance().level != null;
                Block block = Minecraft.getInstance().level.getBlockState(pos).getBlock();
                return markedBlock != null && Objects.equals(block, markedBlock);
            }
    );

    /**
     * This group contains the closest of every type of block
     * that wasn't picked up by any other POI group around the player.
     * This is useful when trying to find something that is not considered a POI,
     * for example until we make a proper trees category, this is a decent way to find trees.
     */
    private final POIGroup<BlockPos> otherBlocksGroup = new POIGroup<>(
            "minecraft_access.point_of_interest.group.otherBlocks",
            pos -> {
                assert Minecraft.getInstance().level != null;
                BlockState state = Minecraft.getInstance().level.getBlockState(pos);
                boolean blockAlreadyInGroup = this.otherBlocksGroup.getItems().stream()
                        .map(p -> Minecraft.getInstance().level.getBlockState(p).getBlock())
                        .anyMatch(t -> t.equals(state.getBlock()));
                return !state.isAir() && !blockAlreadyInGroup;
            }
    );

    @SuppressWarnings("unchecked")
    public final POIGroup<BlockPos>[] groups = Stream.of(List.of(markedGroup), BuiltinBlockPOIGroups.ALL, List.of(otherBlocksGroup))
            .flatMap(Collection::stream).toArray(POIGroup[]::new);

    @Getter
    private List<BlockPos> lastScanResults = new ArrayList<>();

    POIBlocks() {
        interval.setDelay(CONFIG.delay, Interval.Unit.MILLISECOND);
    }

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "poi/blocks");
    }

    @Override
    public void initialize() {
        ClientPlayingTick.AFTER.register(this::tick);
        ClientLifecycleCallback.ConnectedToServer.EVENT.register(client -> {
            markedBlock = null;
            lastScanResults = new ArrayList<>();
        });
    }

    private void tick(Minecraft client, Player player, Level level) {
        Object currentMarkedObject = MainClass.poiManager.poiMarking.getMarkedObject().value;
        if (currentMarkedObject instanceof Block block) {
            markedBlock = block;
        } else {
            markedBlock = null;
        }

        interval.setDelay(CONFIG.delay, Interval.Unit.MILLISECOND);

        if (!CONFIG.enabled) return;
        if (!interval.isReady()) return;

        if (client.gui.screen() != null) return; //Prevent running if any screen is opened

        log.trace("POIBlock started");
        scanBlocksAroundPlayer();
        playerSoundAtFoundPOI(MainClass.poiManager.poiMarking.getMarkedObject().value != null);
        log.trace("POIBlock ended");
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
        assert Minecraft.getInstance().player != null;
        BlockPos pos = Minecraft.getInstance().player.blockPosition();
        scanner.scanAndQualifyBlocksExposedInAirAround(pos.below(), 0);
        scanner.scanAndQualifyBlocksExposedInAirAround(pos.above(2), 0);
        scanner.scanAndQualifyBlocksExposedInAirAround(pos, CONFIG.range);
        scanner.scanAndQualifyBlocksExposedInAirAround(pos.above(), CONFIG.range);

        lastScanResults = currentScanResults;
    }

    private void playerSoundAtFoundPOI(boolean isMarking) {
        if (CONFIG.volume == 0.0f) return;
        if (isMarking && Config.getInstance().poi.marking.suppressOtherWhenEnabled) {
            markedGroup.playSoundForGroupItems(pos -> Vec3.atCenterOf(pos), CONFIG.volume);
        } else if (CONFIG.playSound) {
            if (CONFIG.playSoundForOtherBlocks) {
                for (POIGroup<BlockPos> group : groups) {
                    group.playSoundForGroupItems(pos -> Vec3.atCenterOf(pos), CONFIG.volume);
                }
            } else {
                BuiltinBlockPOIGroups.ORE.group.playSoundForGroupItems(pos -> Vec3.atCenterOf(pos), CONFIG.volume);
            }
        }
    }
}
