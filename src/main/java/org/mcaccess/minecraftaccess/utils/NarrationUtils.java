package org.mcaccess.minecraftaccess.utils;

import java.text.DecimalFormat;
import java.time.Duration;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import org.mcaccess.minecraftaccess.utils.i18n.Narratable;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;
import org.mcaccess.minecraftaccess.utils.i18n.Untranslated;

/**
 * Translate input objects to narration text.
 */
@Slf4j
public final class NarrationUtils {
    private static final Minecraft CLIENT = Minecraft.getInstance();

    private NarrationUtils() {
    }

    public static Narratable narrateNumber(double num) {
        DecimalFormat df = new DecimalFormat();
        double rounded = Math.round(num * 10.0) / 10.0;
        return num >= 0
                ? Untranslated.FORMATTER.put(df.format(rounded))
                : new Translation("minecraft_access.other.negative").variable("number").put(df.format(-rounded));
    }

    @SuppressWarnings("ConditionalCanBePushedInsideExpression")
    public static Narratable narrateNumber(int num) {
        return num >= 0
                ? Untranslated.FORMATTER.put(String.valueOf(num))
                : new Translation("minecraft_access.other.negative").variable("number").put(String.valueOf(num));
    }

    public static String narrateRelativePositionOfPlayerAnd(BlockPos blockPos) {
        if (CLIENT.player == null) return "up";

        Direction dir = CLIENT.player.getDirection();
        Vec3 diff = new Vec3(CLIENT.player.getX(), CLIENT.player.getEyeY(), CLIENT.player.getZ()).subtract(Vec3.atCenterOf(blockPos)); // pre 1.18
        BlockPos diffBlockPos = new BlockPos((int) diff.x, (int) diff.y, (int) diff.z); // post 1.20

        String diffXBlockPos = "";
        String diffYBlockPos = "";
        String diffZBlockPos = "";

        if (diffBlockPos.getX() != 0) {
            if (dir == Direction.NORTH) {
                diffXBlockPos = getDifferenceString(diffBlockPos.getX(), "right", "left");
            } else if (dir == Direction.SOUTH) {
                diffXBlockPos = getDifferenceString(diffBlockPos.getX(), "left", "right");
            } else if (dir == Direction.EAST) {
                diffXBlockPos = getDifferenceString(diffBlockPos.getX(), "away", "behind");
            } else if (dir == Direction.WEST) {
                diffXBlockPos = getDifferenceString(diffBlockPos.getX(), "behind", "away");
            }
        }

        if (diffBlockPos.getY() != 0) {
            diffYBlockPos = getDifferenceString(diffBlockPos.getY(), "up", "down");
        }

        if (diffBlockPos.getZ() != 0) {
            if (dir == Direction.SOUTH) {
                diffZBlockPos = getDifferenceString(diffBlockPos.getZ(), "away", "behind");
            } else if (dir == Direction.NORTH) {
                diffZBlockPos = getDifferenceString(diffBlockPos.getZ(), "behind", "away");
            } else if (dir == Direction.EAST) {
                diffZBlockPos = getDifferenceString(diffBlockPos.getZ(), "right", "left");
            } else if (dir == Direction.WEST) {
                diffZBlockPos = getDifferenceString(diffBlockPos.getZ(), "left", "right");
            }
        }

        String text;
        if (dir == Direction.NORTH || dir == Direction.SOUTH) {
            text = String.format("%s  %s  %s", diffZBlockPos, diffYBlockPos, diffXBlockPos);
        } else {
            text = String.format("%s  %s  %s", diffXBlockPos, diffYBlockPos, diffZBlockPos);
        }
        return text;
    }

    public static String getDifferenceString(int blocks, String key1, String key2) {
        return new Translation("minecraft_access.util.position_difference")
                .variant(blocks < 0 ? key1 : key2)
                .variable("blocks").put(Math.abs(blocks))
                .getString();
    }

    public static String narrateCoordinatesOf(BlockPos blockPos) {
        String posX = narrateNumber(blockPos.getX()).getString();
        String posY = narrateNumber(blockPos.getY()).getString();
        String posZ = narrateNumber(blockPos.getZ()).getString();
        return String.format("%s x %s y %s z", posX, posY, posZ);
    }

    /**
     * @return {EffectName} {Amplifier} {Duration}
     */
    public static Narratable narrateEffect(MobEffectInstance effect) {
        Translation.Delimited result = new Translation.Delimited(' ')
                .put(new Translation.Vanilla(effect.getDescriptionId()));

        int amplifier = effect.getAmplifier();
        if (amplifier > 1) {
            result.put(amplifier);
        }

        if (effect.isInfiniteDuration()) {
            result.put(new Translation.Vanilla("effect.duration.infinite"));
        } else {
            // StatusEffectInstance#getDuration returns ticks, so we divide by 20 in order to convert to seconds
            // 1 second = 20 ticks
            Duration d = Duration.ofSeconds(effect.getDuration() / 20);
            // Note: In some languages (like Chinese), the formats of duration and instant are different,
            // while the formatting below is based on a clock instant.
            // It's tolerable rather than introducing several time related I18N keys.
            String fmt = d.toHoursPart() == 0 ? "mm':'ss" : "HH':'mm':'ss";
            result.put(DurationFormatUtils.formatDuration(d.toMillis(), fmt));
        }

        return result;
    }

    @Contract(pure = true)
    public static @NotNull String formattedCharSequenceToString(@NotNull FormattedCharSequence charSequence) {
        StringBuilder builder = new StringBuilder();
        charSequence.accept((_, _, codePoint) -> {
            builder.appendCodePoint(codePoint);
            return true;
        });
        return builder.toString();
    }
}
