package org.mcaccess.minecraftaccess.features.read_crosshair;

import dev.architectury.platform.Platform;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.config.config_maps.RCPartialSpeakingConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.RCRelativePositionSoundCueConfigMap;
import org.mcaccess.minecraftaccess.config.config_maps.ReadCrosshairConfigMap;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;

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
    private Vec3 previousSoundPos = Vec3.ZERO;
    private boolean speakSide;
    private boolean speakingConsecutiveBlocks;
    private final Interval repeatSpeakingInterval = Interval.defaultDelay();
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
        Minecraft minecraftClient = Minecraft.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.level == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.screen != null) return;

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
            Vec3 targetPosition = switch (hit) {
                case BlockHitResult blockHitResult -> blockHitResult.getBlockPos().getCenter();
                case EntityHitResult entityHitResult -> entityHitResult.getEntity().position();
                default -> null;
            };
            if (targetPosition != null && !Objects.equals(targetPosition, previousSoundPos)) {
                WorldUtils.playRelativePositionSoundCue(targetPosition, rayCastDistance,
                        SoundEvents.NOTE_BLOCK_HARP, this.minSoundVolume, this.maxSoundVolume);
            }
            previousSoundPos = targetPosition;
        }

        if (enablePartialSpeaking) {
            ResourceLocation resourceLocation = switch (hit) {
                case BlockHitResult blockHitResult ->
                        BuiltInRegistries.BLOCK.getKey(minecraftClient.level.getBlockState(blockHitResult.getBlockPos()).getBlock());
                case EntityHitResult entityHitResult -> EntityType.getKey(entityHitResult.getEntity().getType());
                default -> null;
            };
            if (partialSpeakingBlock && hit.getType() == HitResult.Type.BLOCK && isIgnored(resourceLocation)) {
                return;
            }
            if (partialSpeakingEntity && hit.getType() == HitResult.Type.ENTITY && isIgnored(resourceLocation)) {
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
        this.repeatSpeakingInterval.setDelay(rcMap.getRepeatSpeakingInterval(), Interval.Unit.Millisecond);
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
        if (useJade && Platform.isModLoaded("jade")) {
                return Jade.getInstance();
        }
        return MCAccess.getInstance();
    }

    private boolean isIgnored(ResourceLocation identifier) {
        if (identifier == null) return false;
        String name = identifier.getPath();
        Predicate<String> p = partialSpeakingFuzzyMode ? name::contains : name::equals;
        return partialSpeakingWhitelistMode ?
                partialSpeakingTargets.stream().noneMatch(p) :
                partialSpeakingTargets.stream().anyMatch(p);
    }
}
