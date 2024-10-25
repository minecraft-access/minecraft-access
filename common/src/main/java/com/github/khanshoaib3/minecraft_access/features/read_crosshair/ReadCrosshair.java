package com.github.khanshoaib3.minecraft_access.features.read_crosshair;

import com.github.khanshoaib3.minecraft_access.MainClass;
import com.github.khanshoaib3.minecraft_access.config.config_maps.RCPartialSpeakingConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.RCRelativePositionSoundCueConfigMap;
import com.github.khanshoaib3.minecraft_access.config.config_maps.ReadCrosshairConfigMap;
import com.github.khanshoaib3.minecraft_access.utils.PlayerUtils;
import com.github.khanshoaib3.minecraft_access.utils.WorldUtils;
import com.github.khanshoaib3.minecraft_access.utils.condition.Interval;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityType;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This feature reads the name of the targeted block or entity.<br>
 * It also gives feedback when a block is powered by a redstone signal or when a door is open similar cases.
 */
public class ReadCrosshair {
    private static ReadCrosshair instance;
    private boolean enabled;
    private boolean useJade;
    private @Nullable Object previous = null;
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

    public void tick() {
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.world == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.currentScreen != null) return;

        loadConfig();
        if (!enabled) return;

        CrosshairNarrator narrator = getNarrator();
        Object deduplication = narrator.deduplication(speakSide, speakingConsecutiveBlocks);
        if (Objects.equals(deduplication, previous) && !repeatSpeakingInterval.isReady()) {
            return;
        }
        previous = deduplication;
        if (deduplication == null) {
            return;
        }

        HitResult hit = narrator.rayCast();

        if (enableRelativePositionSoundCue) {
            double rayCastDistance = PlayerUtils.getInteractionRange();
            Vec3d targetPosition = switch (hit) {
                case BlockHitResult blockHitResult -> blockHitResult.getBlockPos().toCenterPos();
                case EntityHitResult entityHitResult -> entityHitResult.getEntity().getPos();
                default -> null;
            };
            if (targetPosition != null && !Objects.equals(targetPosition, previousSoundPos)) {
                WorldUtils.playRelativePositionSoundCue(targetPosition, rayCastDistance,
                        SoundEvents.BLOCK_NOTE_BLOCK_HARP, this.minSoundVolume, this.maxSoundVolume);
            }
            previousSoundPos = targetPosition;
        }

        if (enablePartialSpeaking) {
            Identifier identifier = switch (hit) {
                case BlockHitResult blockHitResult -> Registries.BLOCK.getId(minecraftClient.world.getBlockState(blockHitResult.getBlockPos()).getBlock());
                case EntityHitResult entityHitResult -> EntityType.getId(entityHitResult.getEntity().getType());
                default -> null;
            };
            if (partialSpeakingBlock && hit.getType() == HitResult.Type.BLOCK && isIgnored(identifier)) {
                return;
            }
            if (partialSpeakingEntity && hit.getType() == HitResult.Type.ENTITY && isIgnored(identifier)) {
                return;
            }
        }

        MainClass.speakWithNarrator(narrator.narrate(speakSide), true);
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
        useJade = rcMap.isUseJade();
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
        if (useJade) {
            try {
                return Jade.getInstance();
            } catch (NoClassDefFoundError ignored) {
            }
        }
        return MCAccess.getInstance();
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
