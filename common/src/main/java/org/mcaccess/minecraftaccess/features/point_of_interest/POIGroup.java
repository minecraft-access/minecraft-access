package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.Vec3d;
import java.util.function.Supplier;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;
import org.mcaccess.minecraftaccess.utils.WorldUtils;

import java.util.ArrayList;
import java.util.Collections;
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

    public void playSound(Vec3d pos, float volume) {
        WorldUtils.playSoundAtPosition(sound, volume, soundPitch, pos);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
