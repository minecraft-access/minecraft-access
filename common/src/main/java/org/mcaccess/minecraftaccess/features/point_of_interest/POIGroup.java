package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class POIGroup<T> {
    private final String nameTranslateKey;
    private final Sound sound;

    private final Predicate<T> predicate;
    private final List<T> items = new ArrayList<>();

    public POIGroup(String nameTranslateKey, Sound sound, Predicate<T> predicate) {
        this.nameTranslateKey = nameTranslateKey;
        this.sound = sound;
        this.predicate = predicate;
    }

    public POIGroup(String nameTranslateKey, Predicate<T> predicate) {
        this(nameTranslateKey, new Sound(null, 0), predicate);
    }

    public String getTranslatedName() {
        return I18n.get(nameTranslateKey);
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

        return Collections.unmodifiableList(result);
    }

    private double getDistance(T item) {
        if (item instanceof Entity) return WorldUtils.getClientPlayer().distanceTo((Entity) item);
        if (item instanceof BlockPos) {
            return WorldUtils.getClientPlayer().getEyePosition().distanceTo(((BlockPos) item).getCenter());
        }
        return Double.MAX_VALUE;
    }

    public void playSoundForGroupItems(Function<T, Vec3> mapper, float volume) {
        for (T item : items) {
            Vec3 pos = mapper.apply(item);
            playSoundAt(pos, volume);
        }
    }

    public void playSoundAt(Vec3 pos, float volume) {
        sound.play(pos, volume);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public record Sound(SoundEvent tone, float pitch) {
        static Logger log = LoggerFactory.getLogger(POIGroup.Sound.class);

        public void play(Vec3 pos, float volume) {
            if (tone == null) return;
            log.debug("Play POI sound [{}] at [x:{} y:{} z{}]", tone, pos.x, pos.y, pos.z);
            WorldUtils.playSoundAtPosition(tone, volume, pitch, pos);
        }
    }
}
