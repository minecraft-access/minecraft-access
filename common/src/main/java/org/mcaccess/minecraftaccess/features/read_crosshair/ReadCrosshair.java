package org.mcaccess.minecraftaccess.features.read_crosshair;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.PlayerUtils;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;
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
    private Vec3d previousSoundPos = Vec3d.ZERO;
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
        MinecraftClient minecraftClient = MinecraftClient.getInstance();
        if (minecraftClient == null) return;
        if (minecraftClient.world == null) return;
        if (minecraftClient.player == null) return;
        if (minecraftClient.currentScreen != null) return;

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
            Vec3d targetPosition = switch (hit) {
                case BlockHitResult blockHitResult -> blockHitResult.getBlockPos().toCenterPos();
                case EntityHitResult entityHitResult -> entityHitResult.getEntity().getPos();
                default -> null;
            };
            if (targetPosition != null && !Objects.equals(targetPosition, previousSoundPos)) {
                WorldUtils.playRelativePositionSoundCue(targetPosition, rayCastDistance,
                        SoundEvents.BLOCK_NOTE_BLOCK_HARP, config.relativePositionSoundCue.minSoundVolume, config.relativePositionSoundCue.maxSoundVolume);
            }
            previousSoundPos = targetPosition;
        }

        if (config.partialSpeaking.enabled) {
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
        if (config.useJade) {
            try {
                Class.forName("snownee.jade.overlay.WailaTickHandler");
                return Jade.getInstance();
            } catch (ClassNotFoundException ignored) {
            }
        }
        return MCAccess.getInstance();
    }

    private boolean isIgnored(Identifier identifier) {
        if (identifier == null) return false;
        String name = identifier.getPath();
        Predicate<String> p = config.partialSpeaking.fuzzy ? name::contains : name::equals;
        return config.partialSpeaking.whitelist
                ? Arrays.stream(config.partialSpeaking.targets).noneMatch(p)
                : Arrays.stream(config.partialSpeaking.targets).anyMatch(p);
    }
}
