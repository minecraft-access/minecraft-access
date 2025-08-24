package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mcaccess.minecraftaccess.utils.WorldUtils;

@Slf4j
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

    /**
     * @return true if item was added
     */
    public boolean addIfQualified(T item) {
        if (predicate.test(item)) {
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
        return Collections.unmodifiableList(items);
    }

    public @UnmodifiableView List<T> sortByDistance() {
        List<T> result = new ArrayList<>(items);
        Map<T, Double> cache = result.stream().collect(Collectors.toMap(k -> k, this::distanceBetweenPlayerAnd));
        result.sort(Comparator.comparing(cache::get));
        return Collections.unmodifiableList(result);
    }

    private double distanceBetweenPlayerAnd(T item) {
        LocalPlayer player = Minecraft.getInstance().player;
        return switch (item) {
            case Entity entity -> player.distanceTo(entity);
            case BlockPos blockPos -> player.getEyePosition().distanceTo(blockPos.getCenter());
            default -> Double.MAX_VALUE;
        };
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
