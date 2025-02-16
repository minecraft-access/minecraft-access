package org.mcaccess.minecraftaccess.features;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import org.mcaccess.minecraftaccess.config.config_maps.FallDetectorConfigMap;

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

    private boolean enabled;
    private int range;
    private int depth;
    private float volume;
    private int delayInMilliseconds;

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

        loadConfigurations();
    }

    public static synchronized FallDetector getInstance() {
        return instance;
    }

    public void update() {
        try {
            loadConfigurations();

            if (!enabled) return;

            if (minecraftClient == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.level == null) return;
            if (minecraftClient.screen != null) return;
            if (minecraftClient.player.isUnderWater()) return;
            if (minecraftClient.player.isSwimming()) return;
            if (minecraftClient.player.isVisuallySwimming()) return;
            if (!minecraftClient.player.onGround()) return;

            long currentTimeInMillis = clock.millis();
            if (currentTimeInMillis - previousTimeInMillis < delayInMilliseconds) return;
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
        if (Math.abs(dir.getX() - center.getX()) > range)
            return false;

        if (Math.abs(dir.getZ() - center.getZ()) > range)
            return false;

        //noinspection RedundantIfStatement
        if (searched.contains(dir))
            return false;

        return true;
    }

    private void checkForFall(BlockPos toCheck) {

        if (minecraftClient.level == null) return;
        if (!(minecraftClient.level.getBlockState(toCheck).isAir())) return;

        if (getDepth(toCheck, depth) < depth) return;

       log.debug("%d) Found qualified fall position: x:%d y:%d z:%d".formatted(++count, toCheck.getX(), toCheck.getY(), toCheck.getZ()));
        minecraftClient.level.playLocalSound(toCheck, SoundEvents.ANVIL_HIT, SoundSource.BLOCKS, volume, 1f, true);
    }

    private int getDepth(BlockPos blockPos, int maxDepth) {
        if (maxDepth <= 0)
            return 0;

        if (minecraftClient.level == null) return 0;
        if (!(minecraftClient.level.getBlockState(blockPos).isAir())) return 0;

        return 1 + getDepth(blockPos.below(), --maxDepth);
    }

    private void loadConfigurations() {
        FallDetectorConfigMap fallDetectorConfigMap = FallDetectorConfigMap.getInstance();
        enabled = fallDetectorConfigMap.isEnabled();
        range = fallDetectorConfigMap.getRange();
        depth = fallDetectorConfigMap.getDepth();
//        playSound = fallDetectorConfigMap.isPlayAlternateSound();
        volume = fallDetectorConfigMap.getVolume();
        delayInMilliseconds = fallDetectorConfigMap.getDelay();
    }
}
