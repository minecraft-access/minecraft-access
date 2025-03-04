package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.mcaccess.minecraftaccess.Config;

import java.time.Clock;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

@Slf4j
public class FallDetector {
    private static final FallDetector instance;
    private final Clock clock;
    private long previousTimeInMillis;
    Minecraft minecraftClient;
    private int count;
    private Config.FallDetector config;

    static {
        try {
            instance = new FallDetector();
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred in creating FallDetector instance");
        }
    }

    private FallDetector() {
        clock = Clock.systemDefaultZone();
        minecraftClient = Minecraft.getInstance();
        previousTimeInMillis = clock.millis();
        config = Config.getInstance().fallDetector;
    }

    public static synchronized FallDetector getInstance() {
        return instance;
    }

    public void update() {
        try {
            config = Config.getInstance().fallDetector;

            if (!config.enabled) return;

            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.level == null) return;
            if (minecraftClient.screen != null) return;
            if (minecraftClient.player.isUnderWater()) return;
            if (minecraftClient.player.isSwimming()) return;
            if (minecraftClient.player.isVisuallySwimming()) return;
            if (!minecraftClient.player.onGround()) return;

            long currentTimeInMillis = clock.millis();
            if (currentTimeInMillis - previousTimeInMillis < config.delay) return;
            previousTimeInMillis = currentTimeInMillis;

            log.debug("Searching for fall in nearby area...");
            SearchNearbyPositions();
            log.debug("Searching ended.");
        } catch (Exception e) {
            log.error("An error occurred in fall detector.", e);
        }
    }

    private void SearchNearbyPositions() {
        if (minecraftClient.player == null) return;
        BlockPos center = minecraftClient.player.blockPosition();

        Queue<BlockPos> toSearch = new LinkedList<>();
        HashSet<BlockPos> searched = new HashSet<>();
        int[] dirX = {-1, 0, 1, 0};
        int[] dirZ = {0, 1, 0, -1};
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

    private boolean isValid(BlockPos dir, BlockPos center, HashSet<BlockPos> searched) {
        if (Math.abs(dir.getX() - center.getX()) > config.range)
            return false;

        if (Math.abs(dir.getZ() - center.getZ()) > config.range)
            return false;

        //noinspection RedundantIfStatement
        if (searched.contains(dir))
            return false;

        return true;
    }

    private void checkForFall(BlockPos toCheck) {

        if (minecraftClient.level == null) return;
        if (!(minecraftClient.level.getBlockState(toCheck).isAir())) return;

        if (getDepth(toCheck, config.depth) < config.depth) return;

        log.debug("{}) Found qualified fall position: x:{} y:{} z:{}", ++count, toCheck.getX(), toCheck.getY(), toCheck.getZ());
        minecraftClient.level.playLocalSound(toCheck, SoundEvents.ANVIL_HIT, SoundSource.BLOCKS, config.volume, 1f, true);
    }

    private int getDepth(BlockPos blockPos, int maxDepth) {
        if (maxDepth <= 0)
            return 0;

        if (minecraftClient.level == null) return 0;
        if (!(minecraftClient.level.getBlockState(blockPos).isAir())) return 0;

        return 1 + getDepth(blockPos.below(), --maxDepth);
    }
}
