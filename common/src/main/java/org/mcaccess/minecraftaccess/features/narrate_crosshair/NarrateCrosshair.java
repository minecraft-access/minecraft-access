package org.mcaccess.minecraftaccess.features.narrate_crosshair;

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
public class NarrateCrosshair {
    private @Nullable Object previous = null;
    private Vec3 previousSoundPos = Vec3.ZERO;
    private final Interval repetitionInterval = Interval.defaultDelay();
    private boolean filterBlocks;
    private boolean filterEntities;
    private static final Config.NarrateCrosshair config = Config.getInstance().narrateCrosshair;
    private MCAccess mcAccess;
    private Jade jade;

    public NarrateCrosshair() {
        loadConfig();
        mcAccess = new MCAccess();
        jade = new Jade();
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
        Object deduplication = narrator.deduplication(config.narrateBlockFace, !config.disableNarratingConsecutiveBlocks);
        if (Objects.equals(deduplication, previous) && !repetitionInterval.isReady()) {
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

        if (config.filter.enabled) {
            ResourceLocation resourceLocation = switch (hit) {
                case BlockHitResult blockHitResult ->
                        BuiltInRegistries.BLOCK.getKey(minecraftClient.level.getBlockState(blockHitResult.getBlockPos()).getBlock());
                case EntityHitResult entityHitResult -> EntityType.getKey(entityHitResult.getEntity().getType());
                default -> null;
            };
            if (filterBlocks && hit.getType() == HitResult.Type.BLOCK && isIgnored(resourceLocation)) {
                return;
            }
            if (filterEntities && hit.getType() == HitResult.Type.ENTITY && isIgnored(resourceLocation)) {
                return;
            }
        }

        MainClass.narrate(narrator.narrate(config.narrateBlockFace), true);
    }

    private void loadConfig() {
        repetitionInterval.setDelay(config.repetitionInterval, Interval.Unit.Millisecond);
        switch (config.filter.targetMode) {
            case ALL -> {
                filterBlocks = true;
                filterEntities = true;
            }
            case BLOCK -> {
                filterBlocks = true;
                filterEntities = false;
            }
            case ENTITY -> {
                filterBlocks = false;
                filterEntities = true;
            }
        }
    }

    private CrosshairNarrator getNarrator() {
        if (config.useJade && Platform.isModLoaded("jade")) {
            return jade;
        }
        return mcAccess;
    }

    private boolean isIgnored(ResourceLocation identifier) {
        if (identifier == null) return false;
        String name = identifier.getPath();
        Predicate<String> p = config.filter.fuzzy ? name::contains : name::equals;
        return config.filter.whitelist
                ? Arrays.stream(config.filter.targets).noneMatch(p)
                : Arrays.stream(config.filter.targets).anyMatch(p);
    }
}
