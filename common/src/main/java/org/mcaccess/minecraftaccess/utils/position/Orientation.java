package com.github.khanshoaib3.minecraft_access.utils.position;

import com.github.khanshoaib3.minecraft_access.utils.WorldUtils;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;

import java.util.Arrays;
import java.util.Comparator;

/**
 * The {@link net.minecraft.util.math.Direction} is not enough for this mod.
 */
public enum Orientation {
    CENTER(0, 0, LAYER.MIDDLE, new Vec3i(0, 0, 0)),
    NORTH(1, 2, LAYER.MIDDLE, new Vec3i(0, 0, -1)),
    SOUTH(2, 1, LAYER.MIDDLE, new Vec3i(0, 0, 1)),
    EAST(3, 4, LAYER.MIDDLE, new Vec3i(1, 0, 0)),
    WEST(4, 3, LAYER.MIDDLE, new Vec3i(-1, 0, 0)),
    NORTH_EAST(5, 8, LAYER.MIDDLE, new Vec3i(1, 0, -1)),
    NORTH_WEST(6, 7, LAYER.MIDDLE, new Vec3i(-1, 0, -1)),
    SOUTH_EAST(7, 6, LAYER.MIDDLE, new Vec3i(1, 0, 1)),
    SOUTH_WEST(8, 5, LAYER.MIDDLE, new Vec3i(-1, 0, 1)),
    UP(9, 10, LAYER.UPPER, new Vec3i(0, 1, 0)),
    DOWN(10, 9, LAYER.LOWER, new Vec3i(0, -1, 0)),
    UPPER_NORTH(11, 20, LAYER.UPPER, new Vec3i(0, 1, -1)),
    UPPER_SOUTH(12, 19, LAYER.UPPER, new Vec3i(0, 1, 1)),
    UPPER_EAST(13, 22, LAYER.UPPER, new Vec3i(1, 1, 0)),
    UPPER_WEST(14, 21, LAYER.UPPER, new Vec3i(-1, 1, 0)),
    UPPER_NORTH_EAST(15, 26, LAYER.UPPER, new Vec3i(1, 1, -1)),
    UPPER_NORTH_WEST(16, 25, LAYER.UPPER, new Vec3i(-1, 1, -1)),
    UPPER_SOUTH_EAST(17, 24, LAYER.UPPER, new Vec3i(1, 1, 1)),
    UPPER_SOUTH_WEST(18, 23, LAYER.UPPER, new Vec3i(-1, 1, 1)),
    LOWER_NORTH(19, 12, LAYER.LOWER, new Vec3i(0, -1, -1)),
    LOWER_SOUTH(20, 11, LAYER.LOWER, new Vec3i(0, -1, 1)),
    LOWER_EAST(21, 14, LAYER.LOWER, new Vec3i(1, -1, 0)),
    LOWER_WEST(22, 13, LAYER.LOWER, new Vec3i(-1, -1, 0)),
    LOWER_NORTH_EAST(23, 18, LAYER.LOWER, new Vec3i(1, -1, -1)),
    LOWER_NORTH_WEST(24, 17, LAYER.LOWER, new Vec3i(-1, -1, -1)),
    LOWER_SOUTH_EAST(25, 16, LAYER.LOWER, new Vec3i(1, -1, 1)),
    LOWER_SOUTH_WEST(26, 15, LAYER.LOWER, new Vec3i(-1, -1, 1)),
    ;

    private final int id;
    private final int idOpposite;

    private static final Orientation[] ALL = Arrays.stream(values())
            .sorted(Comparator.comparingInt((direction) -> direction.id))
            .toArray(Orientation[]::new);

    public final Vec3i vector;
    public final LAYER layer;

    Orientation(int id, int idOpposite, Orientation.LAYER layer, Vec3i vector) {
        this.id = id;
        this.idOpposite = idOpposite;
        this.layer = layer;
        this.vector = vector;
    }

    public static Orientation of(String s) {
        try {
            return Orientation.valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CENTER;
        }
    }

    public static Orientation of(Direction direction) {
        return of(direction.asString().toUpperCase());
    }

    public static String getOppositeDirectionKey(String originalDirectionKey) {
        return of(originalDirectionKey).getOpposite().toString();
    }

    public static Orientation ofHorizontal(int angle) {
        angle = angle % 360;
        if ((angle >= -150 && angle <= -120) || (angle >= 210 && angle <= 240)) {
            return Orientation.NORTH_EAST;
        } else if ((angle >= -60 && angle <= -30) || (angle >= 300 && angle <= 330)) {
            return Orientation.SOUTH_EAST;
        } else if ((angle >= 30 && angle <= 60) || (angle >= -330 && angle <= -300)) {
            return Orientation.SOUTH_WEST;
        } else if ((angle >= 120 && angle <= 150) || (angle >= -240 && angle <= -210)) {
            return Orientation.NORTH_WEST;
        } else {
            // edge case
            return Orientation.of(WorldUtils.getClientPlayer().getHorizontalFacing());
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public Orientation getOpposite() {
        return byId(this.idOpposite);
    }

    public boolean in(LAYER layer) {
        return this.layer.equals(layer);
    }

    public static Orientation byId(int id) {
        return ALL[Math.abs(id % ALL.length)];
    }

    public enum LAYER {
        UPPER, MIDDLE, LOWER
    }
}
