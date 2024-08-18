package com.github.khanshoaib3.minecraft_access.features.read_crosshair;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.RCPartialSpeakingConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.RCRelativePositionSoundCueConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.ReadCrosshairConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.NarrationUtils;
import com.github.khanshoaib3.minecraft_access.utils.PlayerUtils;
import com.github.khanshoaib3.minecraft_access.utils.WorldUtils;
import com.github.khanshoaib3.minecraft_access.utils.condition.Interval;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This feature reads the name of the targeted block or entity.<br>
 * It also gives feedback when a block is powered by a redstone signal or when a door is open similar cases.
 */
@Slf4j
public class ReadCrosshair {
    private double rayCastDistance = 6.0;
    private static ReadCrosshair instance;
    private boolean enabled;
    private String previousQuery = "";
    private Vec3d previousSoundPos = Vec3d.ZERO;
    private boolean speakSide;
    private boolean speakingConsecutiveBlocks;
    private Interval repeatSpeakingInterval;
    private boolean enablePartialSpeaking;
    private boolean partialSpeakingWhitelistMode;
    private boolean partialSpeakingFuzzyMode;
    private List<String> partialSpeakingTargets;
    private boolean partialSpeakingBlock;
    private boolean partialSpeakingEntity;
    private boolean enableRelativePositionSoundCue;
    private double minSoundVolume;
    private double maxSoundVolume;

    private ReadCrosshair() {
        loadConfig();
    }

    public static ReadCrosshair getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ReadCrosshair();
        }
        return instance;
    }

    public String getPreviousQuery() {
        if (repeatSpeakingInterval.isReady()) {
            this.previousQuery = "";
        }
        return this.previousQuery;
    }

    public void tick() {
        try {
            MinecraftClient minecraftClient = MinecraftClient.getInstance();
            if (minecraftClient == null) return;
            if (minecraftClient.world == null) return;
            if (minecraftClient.player == null) return;
            if (minecraftClient.currentScreen != null) return;

            loadConfig();
            if (!enabled) return;

            this.rayCastDistance = PlayerUtils.getInteractionRange();
            HitResult hit = PlayerUtils.crosshairTarget(rayCastDistance);
            if (hit == null) return;
            narrate(hit, minecraftClient.world);
        } catch (Exception e) {
            log.error("Error occurred in read block feature.", e);
        }
    }

    private void loadConfig() {
        // It is best to get the config map from instance of Config class rather than directly from
        // the ReadCrosshairConfigMap class because in the case of an error in the config.json,
        // while it does get reset to default but the mod crashes as well. So to avoid the mod from crashing,
        // use the instance of Config class to get instances of other config maps.
        ReadCrosshairConfigMap rcMap = ReadCrosshairConfigMap.getInstance();
        RCPartialSpeakingConfigMap rcpMap = RCPartialSpeakingConfigMap.getInstance();
        RCRelativePositionSoundCueConfigMap rcrMap = RCRelativePositionSoundCueConfigMap.getInstance();

        this.enabled = rcMap.isEnabled();
        this.speakSide = rcMap.isSpeakSide();
        // affirmation for easier use
        this.speakingConsecutiveBlocks = !rcMap.isDisableSpeakingConsecutiveBlocks();
        long interval = rcMap.getRepeatSpeakingInterval();
        this.repeatSpeakingInterval = Interval.inMilliseconds(interval, this.repeatSpeakingInterval);
        this.enableRelativePositionSoundCue = rcrMap.isEnabled();
        this.minSoundVolume = rcrMap.getMinSoundVolume();
        this.maxSoundVolume = rcrMap.getMaxSoundVolume();

        this.enablePartialSpeaking = rcpMap.isEnabled();
        this.partialSpeakingFuzzyMode = rcpMap.isPartialSpeakingFuzzyMode();
        this.partialSpeakingWhitelistMode = rcpMap.isPartialSpeakingWhitelistMode();
        this.partialSpeakingTargets = rcpMap.getPartialSpeakingTargets();
        switch (rcpMap.getPartialSpeakingTargetMode()) {
            case ALL -> {
                partialSpeakingBlock = true;
                partialSpeakingEntity = true;
            }
            case BLOCK -> {
                partialSpeakingBlock = true;
                partialSpeakingEntity = false;
            }
            case ENTITY -> {
                partialSpeakingBlock = false;
                partialSpeakingEntity = true;
            }
        }
    }

    private CrosshairNarrator getNarrator() {
        try {
            return Jade.getInstance();
        } catch (NoClassDefFoundError e) {
            return MCAccess.getInstance();
        }
    }

    private void narrate(HitResult blockHit, ClientWorld world) {
        switch (blockHit.getType()) {
            case MISS -> {}
            case BLOCK -> narrateBlock((BlockHitResult) blockHit, world);
            case ENTITY -> narrateEntity((EntityHitResult) blockHit);
        }
    }

    private void narrateEntity(EntityHitResult hit) {
        try {
            Entity entity = hit.getEntity();

            if (enablePartialSpeaking && partialSpeakingEntity
                    && isIgnored(EntityType.getId(entity.getType()))) {
                return;
            }

            String narration = getNarrator().narrate(hit);
            speakIfFocusChanged(narration, narration, entity.getPos());
        } catch (Exception e) {
            log.error("Error occurred in ReadCrosshair, reading entity", e);
        }
    }

    /**
     * @param currentQuery for checking if focus is changed
     * @param toSpeak      text will be narrated (if focus has changed)
     */
    private void speakIfFocusChanged(String currentQuery, String toSpeak, Vec3d targetPosition) {
        boolean focusChanged = !getPreviousQuery().equalsIgnoreCase(currentQuery);
        if (focusChanged) {
            if (this.enableRelativePositionSoundCue && !this.previousSoundPos.equals(targetPosition)) {
                WorldUtils.playRelativePositionSoundCue(targetPosition, rayCastDistance,
                        SoundEvents.BLOCK_NOTE_BLOCK_HARP, this.minSoundVolume, this.maxSoundVolume);
                this.previousSoundPos = targetPosition;
            }
            this.previousQuery = currentQuery;
            MainClass.speakWithNarrator(toSpeak, true);
        }
    }

    private void narrateBlock(BlockHitResult hit, ClientWorld world) {
        BlockPos blockPos = hit.getBlockPos();
        WorldUtils.BlockInfo blockInfo = WorldUtils.getBlockInfo(blockPos);
        // In Minecraft resource location format, for example, "oak_door" for Oak Door.
        // ref: https://minecraft.wiki/w/Java_Edition_data_values#Blocks
        Identifier blockId = Registries.BLOCK.getId(blockInfo.type());
        if (enablePartialSpeaking && partialSpeakingBlock && isIgnored(blockId)) {
            return;
        }

        String side = this.speakSide ? hit.getSide().getName() : "";
        String currentQuery = NarrationUtils.narrateBlockForContentChecking(blockPos, side).getRight();
        // If "speakingConsecutiveBlocks" config is enabled, add position info to currentQuery,
        // so same blocks at different positions will be regard as different one then trigger the narrator.
        // Class name in production environment can be different
        if (this.speakingConsecutiveBlocks) currentQuery += blockPos.toString();

        String narration = getNarrator().narrate(hit, world, this.speakSide);
        speakIfFocusChanged(currentQuery, narration, Vec3d.of(blockPos));
    }

    private boolean isIgnored(Identifier identifier) {
        if (identifier == null) return false;
        String name = identifier.getPath();
        Predicate<String> p = partialSpeakingFuzzyMode ? name::contains : name::equals;
        return partialSpeakingWhitelistMode ?
                partialSpeakingTargets.stream().noneMatch(p) :
                partialSpeakingTargets.stream().anyMatch(p);
    }
}
