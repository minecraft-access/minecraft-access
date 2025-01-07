package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;
import org.mcaccess.minecraftaccess.utils.WorldUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class POIGroup<T> {
    private Supplier<String> nameSupplier;
    private final SoundEvent sound;
    private final float soundPitch;

    private final Predicate<T> predicate;
    private final List<T> items = new ArrayList<>();

    public POIGroup(Supplier<String> nameSupplier, SoundEvent sound, float soundPitch, Predicate<T> predicate) {
        this.nameSupplier = nameSupplier;
        this.sound = sound;
        this.soundPitch = soundPitch;
        this.predicate = predicate;
    }

    public POIGroup(Supplier<String> nameSupplier, Predicate<T> predicate) {
        this(nameSupplier, null, 0, predicate);
    }

    public String getName() {
        return nameSupplier.get();
    }

    public boolean add(T item) {
        if (predicate.test(item)) {
            items.add(item);
            return true;
        }
        return false;
    }

    public void clear() {
        items.clear();
    }

    @Contract(pure = true)
    public @UnmodifiableView List<T> getItems() {
        return Collections.unmodifiableList(items);
    }

    public @UnmodifiableView List<T> getItems(Boolean sorted) {
        if (!sorted) return getItems();

        List<T> result = new ArrayList<>(items);

        result.sort((item1, item2) -> {
            double distance1 = getDistance(item1);
            double distance2 = getDistance(item2);
            return Double.compare(distance1, distance2);
        });

        return result;
    }

    private double getDistance(T item) {
        if (item instanceof Entity) return MinecraftClient.getInstance().player.distanceTo((Entity)item);
        if (item instanceof BlockPos) return MinecraftClient.getInstance().player.getEyePos().distanceTo(((BlockPos)item).toCenterPos());

        throw new IllegalArgumentException();
    }

    public void playSound(Vec3d pos, float volume) {
        if (sound == null) return;
        WorldUtils.playSoundAtPosition(sound, volume, soundPitch, pos);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
