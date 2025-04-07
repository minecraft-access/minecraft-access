package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;
import org.mcaccess.minecraftaccess.utils.WorldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

public class POIGroup<T> {
    private static final Logger log = LoggerFactory.getLogger(POIGroup.class);
    private final String nameTranslateKey;
    private final Sound sound;
    private final Predicate<T> whetherFitsThisGroup;
    private final PriorityQueue<T> items;

    public POIGroup(String nameTranslateKey, Sound sound, Predicate<T> whetherFitsThisGroup, ToDoubleFunction<T> priorityCalculator) {
        this.nameTranslateKey = nameTranslateKey;
        this.sound = sound;
        this.whetherFitsThisGroup = whetherFitsThisGroup;
        this.items = new PriorityQueue<>(Comparator.comparingDouble(priorityCalculator));
    }

    public POIGroup(String nameTranslateKey, Predicate<T> whetherFitsThisGroup, ToDoubleFunction<T> priorityCalculator) {
        this(nameTranslateKey, new Sound(null, 0), whetherFitsThisGroup, priorityCalculator);
    }

    public String getTranslatedName() {
        return I18n.get(nameTranslateKey);
    }

    /**
     * @return true if item was added
     */
    public boolean addIfQualified(T item) {
        if (whetherFitsThisGroup.test(item)) {
            log.debug("[{}] Add POI item [{}]", getTranslatedName(), item);
            items.add(item);
            return true;
        }
        return false;
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Contract(pure = true)
    public @UnmodifiableView List<T> getItems() {
        return items.stream().toList();
    }

    public T getFirst() {
        return items.peek();
    }

    public void playSoundForGroupItems(Function<T, Vec3> mapper, float volume) {
        for (T item : items) {
            Vec3 pos = mapper.apply(item);
            sound.play(pos, volume);
        }
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
