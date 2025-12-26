package org.mcaccess.minecraftaccess.features;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import org.mcaccess.minecraftaccess.Config;
import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.api.WorldNarrator;
import org.mcaccess.minecraftaccess.utils.condition.Interval;

/**
 * This feature reads the name of the targeted block or entity.<br>
 * It also gives feedback when a block is powered by a redstone signal or when a door is open similar cases.
 */
@Slf4j
public class NarrateCrosshair {
    private static final Minecraft CLIENT = Minecraft.getInstance();
    private @Nullable Object previousTarget = null;
    private @Nullable String previousNarration = null;
    private Vec3 previousSoundPos = Vec3.ZERO;
    private final Interval repetitionInterval = Interval.defaultDelay();
    private static final Config.NarrateCrosshair CONFIG = Config.getInstance().narrateCrosshair;

    public void tick() {
        if (CLIENT.screen != null) return;
        if (!CONFIG.enabled) return;
        repetitionInterval.setDelay(CONFIG.repetitionInterval, Interval.Unit.MILLISECOND);

        WorldNarrator narrator = MainClass.registry(WorldNarrator.class).get(CONFIG.narrator);
        HitResult rayCast = narrator.rayCast();
        if (rayCast == null || rayCast.getType() == HitResult.Type.MISS) {
            previousTarget = null;
            previousNarration = null;
            return;
        }

        String narration = narrator.narrate(rayCast);
        Object target = switch (rayCast) {
            case BlockHitResult blockHitResult -> CONFIG.disableNarratingConsecutiveBlocks ? null : blockHitResult.getBlockPos();
            case EntityHitResult entityHitResult -> entityHitResult.getEntity();
            default -> rayCast;
        };

        if (Objects.equals(target, previousTarget) && Objects.equals(narration, previousNarration) && !repetitionInterval.isReady()) {
            previousTarget = target;
            previousNarration = narration;
            return;
        }
        previousTarget = target;
        previousNarration = narration;

        if (narration == null) {
            return;
        }

        if (CONFIG.relativePositionSoundCue.enabled) {
            assert CLIENT.player != null;
            double rayCastDistance = Math.min(CLIENT.player.blockInteractionRange(), CLIENT.player.entityInteractionRange());
            Vec3 targetPosition = switch (rayCast) {
                case BlockHitResult blockHitResult -> blockHitResult.getBlockPos().getCenter();
                case EntityHitResult entityHitResult -> entityHitResult.getEntity().position();
                default -> rayCast.getLocation();
            };
            if (!Objects.equals(targetPosition, previousSoundPos)) {
                playRelativePositionSoundCue(targetPosition, rayCastDistance,
                        SoundEvents.NOTE_BLOCK_HARP,
                        CONFIG.relativePositionSoundCue.minSoundVolume,
                        CONFIG.relativePositionSoundCue.maxSoundVolume);
            }
            previousSoundPos = targetPosition;
        }

        if (!(rayCast instanceof BlockHitResult || rayCast instanceof EntityHitResult)) {
            log.warn("Filtering only works on BlockHitResult and EntityHitResult. Using narrator {}", CONFIG.narrator);
        } else if (CONFIG.filter.enabled) {
            switch (rayCast) {
                case BlockHitResult blockHitResult when CONFIG.filter.targetMode.filterBlocks() -> {
                    assert CLIENT.level != null;
                    Identifier key = BuiltInRegistries.BLOCK.getKey(CLIENT.level.getBlockState(blockHitResult.getBlockPos()).getBlock());
                    if (isIgnored(key)) {
                        return;
                    }
                }
                case EntityHitResult entityHitResult when CONFIG.filter.targetMode.filterEntities() -> {
                    Identifier key = EntityType.getKey(entityHitResult.getEntity().getType());
                    if (isIgnored(key)) {
                        return;
                    }
                }
                default -> {
                }
            }
        }

        MainClass.narrate(narration, true);
    }

    private boolean isIgnored(Identifier identifier) {
        if (identifier == null) return false;
        String name = identifier.getPath();
        Predicate<String> p = CONFIG.filter.fuzzy ? name::contains : name::equals;
        return CONFIG.filter.whitelist
                ? Arrays.stream(CONFIG.filter.targets).noneMatch(p)
                : Arrays.stream(CONFIG.filter.targets).anyMatch(p);
    }

    // To indicate relative location between player and target.
    private static void playRelativePositionSoundCue(Vec3 targetPosition, double maxDistance, Holder.Reference<SoundEvent> sound, double minVolume, double maxVolume) {
        assert CLIENT.player != null;
        Vec3 playerPos = CLIENT.player.position();

        // Use pitch to represent relative elevation, the higher the sound the higher the target.
        // The range of pitch is [0.5, 2.0], calculated as: 2 ^ (x / 12), where x is [-12, 12].
        // ref: https://minecraft.wiki/w/Note_Block#Notes
        //
        // Since we have a custom distance,
        // the range of (targetY - playerY) is [-maxDistance, maxDistance],
        // so let the maxDistance be the denominator to map to the original range.
        float pitch = (float) Math.pow(2, (targetPosition.y() - playerPos.y) / maxDistance);

        // Use volume to represent distance, the louder the sound the closer the distance.
        double distance = Math.sqrt(targetPosition.distanceToSqr(playerPos.x, playerPos.y, playerPos.z));
        // = base volume (minVolume) + the volume delta per block ((maxVolume - minVolume) / maxDistance)
        double volumeDeltaPerBlock = (maxVolume - minVolume) / maxDistance;
        float volume = (float) (minVolume + (maxDistance - distance) * volumeDeltaPerBlock);

        assert CLIENT.level != null;
        CLIENT.level.playLocalSound(targetPosition.x, targetPosition.y, targetPosition.z, sound.value(), SoundSource.BLOCKS, volume, pitch, true);
    }
}
