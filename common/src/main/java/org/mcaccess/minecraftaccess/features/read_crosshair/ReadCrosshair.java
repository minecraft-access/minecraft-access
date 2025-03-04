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
import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * This feature reads the name of the targeted block or entity.<br>
 * It also gives feedback when a block is powered by a redstone signal or when a door is open similar cases.
 */
public class ReadCrosshair {
    private static ReadCrosshair instance;
    private @Nullable Object previous = null;
    private Vec3 previousSoundPos = Vec3.ZERO;
    private final Interval repeatSpeakingInterval = Interval.defaultDelay();
    private boolean partialSpeakingBlock;
    private boolean partialSpeakingEntity;
    private static final Config.ReadCrosshair config = Config.getInstance().readCrosshair;

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
        if (!config.enabled) return;

        CrosshairNarrator narrator = getNarrator();
        Object deduplication = narrator.deduplication(config.speakSide, !config.disableSpeakingConsecutiveBlocks);
        if (Objects.equals(deduplication, previous) && !repeatSpeakingInterval.isReady()) {
            return;
        }
        previous = deduplication;
        if (deduplication == null) {
            return;
        }

        HitResult hit = narrator.rayCast();

        if (config.relativePositionSoundCue.enabled) {
            double rayCastDistance = PlayerUtils.getInteractionRange();
            Vec3 targetPosition = switch (hit) {
                case BlockHitResult blockHitResult -> blockHitResult.getBlockPos().getCenter();
                case EntityHitResult entityHitResult -> entityHitResult.getEntity().position();
                default -> null;
            };
            if (targetPosition != null && !Objects.equals(targetPosition, previousSoundPos)) {
                WorldUtils.playRelativePositionSoundCue(targetPosition, rayCastDistance,
                        SoundEvents.NOTE_BLOCK_HARP,
                        config.relativePositionSoundCue.minSoundVolume,
                        config.relativePositionSoundCue.maxSoundVolume);
            }
            previousSoundPos = targetPosition;
        }

        if (config.partialSpeaking.enabled) {
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

        MainClass.speakWithNarrator(narrator.narrate(config.speakSide), true);
    }

    private void loadConfig() {
        repeatSpeakingInterval.setDelay(config.repeatSpeakingInterval, Interval.Unit.Millisecond);
        switch (config.partialSpeaking.targetMode) {
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
        if (config.useJade && Platform.isModLoaded("jade")) {
            return Jade.getInstance();
        }
        return MCAccess.getInstance();
    }

    private boolean isIgnored(ResourceLocation identifier) {
        if (identifier == null) return false;
        String name = identifier.getPath();
        Predicate<String> p = config.partialSpeaking.fuzzy ? name::contains : name::equals;
        return config.partialSpeaking.whitelist
                ? Arrays.stream(config.partialSpeaking.targets).noneMatch(p)
                : Arrays.stream(config.partialSpeaking.targets).anyMatch(p);
    }
}
