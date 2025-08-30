package org.mcaccess.minecraftaccess.features.narrate_crosshair;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

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
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.mcaccess.minecraftaccess.utils.condition.Interval;

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
    private static final Config.NarrateCrosshair CONFIG = Config.getInstance().narrateCrosshair;
    private final MCAccess mcAccess;
    private final Jade jade;

    public NarrateCrosshair() {
        loadConfig();
        mcAccess = new MCAccess();
        jade = new Jade();
    }

    public void tick() {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;
        if (client.player == null) return;
        if (client.screen != null) return;

        loadConfig();
        if (!CONFIG.enabled) return;

        CrosshairNarrator narrator = getNarrator();
        Object deduplication = narrator.deduplication(CONFIG.narrateBlockFace, !CONFIG.disableNarratingConsecutiveBlocks);
        if (Objects.equals(deduplication, previous) && !repetitionInterval.isReady()) {
            return;
        }
        previous = deduplication;
        if (deduplication == null) {
            return;
        }

        HitResult hit = narrator.rayCast();

        if (CONFIG.relativePositionSoundCue.enabled) {
            double rayCastDistance = Math.min(client.player.blockInteractionRange(), client.player.entityInteractionRange());
            Vec3 targetPosition = switch (hit) {
                case BlockHitResult blockHitResult -> blockHitResult.getBlockPos().getCenter();
                case EntityHitResult entityHitResult -> entityHitResult.getEntity().position();
                default -> null;
            };
            if (targetPosition != null && !Objects.equals(targetPosition, previousSoundPos)) {
                WorldUtils.playRelativePositionSoundCue(targetPosition, rayCastDistance,
                        SoundEvents.NOTE_BLOCK_HARP,
                        CONFIG.relativePositionSoundCue.minSoundVolume,
                        CONFIG.relativePositionSoundCue.maxSoundVolume);
            }
            previousSoundPos = targetPosition;
        }

        if (CONFIG.filter.enabled) {
            ResourceLocation resourceLocation = switch (hit) {
                case BlockHitResult blockHitResult ->
                        BuiltInRegistries.BLOCK.getKey(client.level.getBlockState(blockHitResult.getBlockPos()).getBlock());
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

        MainClass.narrate(narrator.narrate(CONFIG.narrateBlockFace), true);
    }

    private void loadConfig() {
        repetitionInterval.setDelay(CONFIG.repetitionInterval, Interval.Unit.MILLISECOND);
        switch (CONFIG.filter.targetMode) {
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
        if (CONFIG.useJade && Platform.isModLoaded("jade")) {
            return jade;
        }
        return mcAccess;
    }

    private boolean isIgnored(ResourceLocation identifier) {
        if (identifier == null) return false;
        String name = identifier.getPath();
        Predicate<String> p = CONFIG.filter.fuzzy ? name::contains : name::equals;
        return CONFIG.filter.whitelist
                ? Arrays.stream(CONFIG.filter.targets).noneMatch(p)
                : Arrays.stream(CONFIG.filter.targets).anyMatch(p);
    }
}
