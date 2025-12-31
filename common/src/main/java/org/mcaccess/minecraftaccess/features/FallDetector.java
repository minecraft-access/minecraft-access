package org.mcaccess.minecraftaccess.features;

import java.time.Clock;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import net.blay09.mods.balm.client.platform.event.callback.ClientTickCallback;
import net.blay09.mods.balm.client.platform.module.BalmClientModule;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;

@Slf4j
public class FallDetector implements BalmClientModule {
    private final Clock clock;
    private long previousTimeInMillis;
    private final Minecraft client = Minecraft.getInstance();
    private int count;
    private final Config.FallDetector config;

    public FallDetector() {
        clock = Clock.systemDefaultZone();
        previousTimeInMillis = clock.millis();
        config = Config.getInstance().fallDetector;
    }

    @Override
    public @NotNull Identifier getId() {
        return Identifier.fromNamespaceAndPath(MainClass.MOD_ID, "fall_detector");
    }

    @Override
    public void initialize() {
        ClientTickCallback.ClientPlayerTick.AFTER.register(this::tick);
    }

    private void tick(Player player) {
        if (!config.enabled) return;

        if (client.screen != null) return;
        if (!player.onGround()) return;
        if (player.isUnderWater()) return;
        if (player.isSwimming()) return;
        if (player.isVisuallySwimming()) return;

        long currentTimeInMillis = clock.millis();
        if (currentTimeInMillis - previousTimeInMillis < config.delay) return;
        previousTimeInMillis = currentTimeInMillis;

        log.trace("Searching for fall in nearby area...");
        searchNearbyPositions();
        log.trace("Searching ended");
    }

    private void searchNearbyPositions() {
        if (client.level == null) return;
        assert client.player != null;
        BlockPos center = client.player.blockPosition();

        Queue<BlockPos> toSearch = new LinkedList<>();
        Set<BlockPos> searched = new HashSet<>();
        int[] dirX = new int[]{-1, 0, 1, 0};
        int[] dirZ = new int[]{0, 1, 0, -1};
        count = 0;

        toSearch.add(center);
        searched.add(center);

        while (!toSearch.isEmpty()) {
            BlockPos item = toSearch.poll();
            checkForFall(item);

            for (int i = 0; i < 4; i++) {
                BlockPos dir = new BlockPos(item.getX() + dirX[i], item.getY(), item.getZ() + dirZ[i]);

                if (isValid(dir, center, searched)) {
                    toSearch.add(dir);
                    searched.add(dir);
                }
            }
        }
    }

    private boolean isValid(BlockPos dir, BlockPos center, Set<BlockPos> searched) {
        if (Math.abs(dir.getX() - center.getX()) > config.range) {
            return false;
        }

        if (Math.abs(dir.getZ() - center.getZ()) > config.range) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (searched.contains(dir)) {
            return false;
        }

        return true;
    }

    private void checkForFall(BlockPos toCheck) {
        assert client.level != null;
        if (!(client.level.getBlockState(toCheck).isAir())) return;

        if (getDepth(toCheck, config.depth) < config.depth) return;

        ++count;
        log.debug("{}) Found qualified fall position: x:{} y:{} z:{}", count, toCheck.getX(), toCheck.getY(), toCheck.getZ());
        client.level.playLocalSound(toCheck, SoundEvents.ANVIL_HIT, SoundSource.BLOCKS, config.volume, 1.0f, true);
    }

    private int getDepth(BlockPos blockPos, int maxDepth) {
        if (maxDepth <= 0) {
            return 0;
        }

        assert client.level != null;
        if (!(client.level.getBlockState(blockPos).isAir())) return 0;

        return 1 + getDepth(blockPos.below(), maxDepth - 1);
    }
}
