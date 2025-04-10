package org.mcaccess.minecraftaccess.features.point_of_interest;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
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
    private final PriorityQueue<Item<T>> items;
    private final ToDoubleFunction<T> itemDistanceToPlayer;
    /**
     * Smaller number means higher priority when comparing between multiple groups.
     * Default is 0.
     */
    public final int priorityAmongGroups;

    public POIGroup(String nameTranslateKey, Sound sound, Predicate<T> whetherFitsThisGroup, ToDoubleFunction<T> itemDistanceToPlayer, int priorityAmongGroups) {
        this.nameTranslateKey = nameTranslateKey;
        this.sound = sound;
        this.whetherFitsThisGroup = whetherFitsThisGroup;
        this.items = new PriorityQueue<>(Comparator.comparingDouble(Item::distanceToPlayer));
        this.itemDistanceToPlayer = itemDistanceToPlayer;
        this.priorityAmongGroups = priorityAmongGroups;
    }

    public POIGroup(String nameTranslateKey, Sound sound, Predicate<T> whetherFitsThisGroup, ToDoubleFunction<T> itemDistanceToPlayer) {
        this(nameTranslateKey, sound, whetherFitsThisGroup, itemDistanceToPlayer, 0);
    }

    public POIGroup(String nameTranslateKey, Predicate<T> whetherFitsThisGroup, ToDoubleFunction<T> itemDistanceToPlayer, int priorityAmongGroups) {
        this(nameTranslateKey, new Sound(null, 0), whetherFitsThisGroup, itemDistanceToPlayer, priorityAmongGroups);
    }

    public POIGroup(String nameTranslateKey, Predicate<T> whetherFitsThisGroup, ToDoubleFunction<T> itemDistanceToPlayer) {
        this(nameTranslateKey, new Sound(null, 0), whetherFitsThisGroup, itemDistanceToPlayer, 0);
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
            items.add(new Item<>(item, priorityAmongGroups, itemDistanceToPlayer.applyAsDouble(item)));
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
        return items.stream().map(Item::item).toList();
    }

    @Contract(pure = true)
    public @UnmodifiableView List<Item<T>> getItemWithPriorities() {
        return items.stream().toList();
    }

    public T getNearest() {
        return items.isEmpty() ? null : items.peek().item;
    }

    public void playSoundForGroupItems(Function<T, Vec3> mapper, float volume) {
        for (Item<T> item : items) {
            Vec3 pos = mapper.apply(item.item);
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

    public record Item<T>(T item, int groupPriority, double distanceToPlayer) implements Comparable<Item<T>> {
        @Override
        public int compareTo(@NotNull POIGroup.Item<T> o) {
            int groupLevel = Integer.compare(groupPriority, o.groupPriority);
            if (groupLevel != 0) return groupLevel;
            return Double.compare(distanceToPlayer, o.distanceToPlayer);
        }
    }
}
