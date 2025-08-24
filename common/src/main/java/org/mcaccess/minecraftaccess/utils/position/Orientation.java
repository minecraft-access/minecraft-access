package org.mcaccess.minecraftaccess.utils.position;

import java.util.Arrays;
import java.util.Comparator;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;

/**
 * The {@link Direction} is not enough for this mod.
 */
public enum Orientation {
    CENTER(0, 0, Layer.MIDDLE, new Vec3i(0, 0, 0)),
    NORTH(1, 2, Layer.MIDDLE, new Vec3i(0, 0, -1)),
    SOUTH(2, 1, Layer.MIDDLE, new Vec3i(0, 0, 1)),
    EAST(3, 4, Layer.MIDDLE, new Vec3i(1, 0, 0)),
    WEST(4, 3, Layer.MIDDLE, new Vec3i(-1, 0, 0)),
    NORTH_EAST(5, 8, Layer.MIDDLE, new Vec3i(1, 0, -1)),
    NORTH_WEST(6, 7, Layer.MIDDLE, new Vec3i(-1, 0, -1)),
    SOUTH_EAST(7, 6, Layer.MIDDLE, new Vec3i(1, 0, 1)),
    SOUTH_WEST(8, 5, Layer.MIDDLE, new Vec3i(-1, 0, 1)),
    UP(9, 10, Layer.UPPER, new Vec3i(0, 1, 0)),
    DOWN(10, 9, Layer.LOWER, new Vec3i(0, -1, 0)),
    UPPER_NORTH(11, 20, Layer.UPPER, new Vec3i(0, 1, -1)),
    UPPER_SOUTH(12, 19, Layer.UPPER, new Vec3i(0, 1, 1)),
    UPPER_EAST(13, 22, Layer.UPPER, new Vec3i(1, 1, 0)),
    UPPER_WEST(14, 21, Layer.UPPER, new Vec3i(-1, 1, 0)),
    UPPER_NORTH_EAST(15, 26, Layer.UPPER, new Vec3i(1, 1, -1)),
    UPPER_NORTH_WEST(16, 25, Layer.UPPER, new Vec3i(-1, 1, -1)),
    UPPER_SOUTH_EAST(17, 24, Layer.UPPER, new Vec3i(1, 1, 1)),
    UPPER_SOUTH_WEST(18, 23, Layer.UPPER, new Vec3i(-1, 1, 1)),
    LOWER_NORTH(19, 12, Layer.LOWER, new Vec3i(0, -1, -1)),
    LOWER_SOUTH(20, 11, Layer.LOWER, new Vec3i(0, -1, 1)),
    LOWER_EAST(21, 14, Layer.LOWER, new Vec3i(1, -1, 0)),
    LOWER_WEST(22, 13, Layer.LOWER, new Vec3i(-1, -1, 0)),
    LOWER_NORTH_EAST(23, 18, Layer.LOWER, new Vec3i(1, -1, -1)),
    LOWER_NORTH_WEST(24, 17, Layer.LOWER, new Vec3i(-1, -1, -1)),
    LOWER_SOUTH_EAST(25, 16, Layer.LOWER, new Vec3i(1, -1, 1)),
    LOWER_SOUTH_WEST(26, 15, Layer.LOWER, new Vec3i(-1, -1, 1));

    private final int id;
    private final int idOpposite;

    private static final Orientation[] ALL = Arrays.stream(values())
            .sorted(Comparator.comparingInt((direction) -> direction.id))
            .toArray(Orientation[]::new);

    public final Vec3i vector;
    public final Layer layer;

    Orientation(int id, int idOpposite, Layer layer, Vec3i vector) {
        this.id = id;
        this.idOpposite = idOpposite;
        this.layer = layer;
        this.vector = vector;
    }

    public static Orientation of(String s) {
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException e) {
            return CENTER;
        }
    }

    public static Orientation of(Direction direction) {
        return of(direction.getSerializedName().toUpperCase());
    }

    public static String getOppositeDirectionKey(String originalDirectionKey) {
        return of(originalDirectionKey).getOpposite().toString();
    }

    public static Orientation ofHorizontal(int angle) {
        int normalised = angle % 360;
        if (normalised < 0) {
            normalised += 360;
        }

        // Ordinal Directions
        if (normalised >= 11.25 && normalised < 78.75) {        // SW quadrant
            return SOUTH_WEST;
        } else if (normalised >= 101.25 && normalised < 168.75) { // NW quadrant
            return NORTH_WEST;
        } else if (normalised >= 191.25 && normalised < 258.75) { // NE quadrant
            return NORTH_EAST;
        } else if (normalised >= 281.25 && normalised < 348.75) { // SE quadrant
            return SOUTH_EAST;
        } else {
            // Cardinal Directions
            return of(Minecraft.getInstance().player.getDirection());
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }

    public Orientation getOpposite() {
        return byId(idOpposite);
    }

    public boolean in(Layer layer) {
        return this.layer == layer;
    }

    public static Orientation byId(int id) {
        return ALL[Math.abs(id % ALL.length)];
    }

    public enum Layer {
        UPPER,
        MIDDLE,
        LOWER,
    }
}
