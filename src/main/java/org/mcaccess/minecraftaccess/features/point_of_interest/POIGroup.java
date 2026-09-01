package org.mcaccess.minecraftaccess.features.point_of_interest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.demonwav.mcdev.annotations.Translatable;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.mcaccess.minecraftaccess.MainClass;
import org.mcaccess.minecraftaccess.utils.i18n.Narratable;
import org.mcaccess.minecraftaccess.utils.i18n.Translation;

@Slf4j
public class POIGroup<T> {
    public final Narratable name;
    private final Sound sound;

    private final Predicate<T> predicate;
    private final List<T> items = new ArrayList<>();

    public POIGroup(Narratable name, Sound sound, Predicate<T> predicate) {
        this.name = name;
        this.sound = sound;
        this.predicate = predicate;
    }

    public POIGroup(@Translatable String name, Sound sound, Predicate<T> predicate) {
        this(new Translation(name), sound, predicate);
    }

    public POIGroup(Narratable name, Predicate<T> predicate) {
        this(name, new Sound(null, 0), predicate);
    }

    public POIGroup(@Translatable String name, Predicate<T> predicate) {
        this(new Translation(name), predicate);
    }

    /**
     * @return true if item was added
     */
    public boolean addIfQualified(T item) {
        if (!MainClass.poiManager.objectTracker.isObjectValid(item)) return false;

        if (predicate.test(item)) {
            log.debug("[{}] Add POI item [{}]", name.getString(), item);
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
            case Entity entity -> {
                assert player != null;
                yield player.distanceTo(entity);
            }
            case BlockPos blockPos -> {
                assert player != null;
                yield player.getEyePosition().distanceTo(Vec3.atCenterOf(blockPos));
            }
            default -> Double.MAX_VALUE;
        };
    }

    public void playSoundForGroupItems(Function<T, Vec3> mapper, float volume) {
        for (T item : items) {
            Vec3 pos = mapper.apply(item);
            sound.play(pos, volume);
        }
    }

    public record Sound(SoundEvent sound, float pitch) {
        static Logger log = LoggerFactory.getLogger(POIGroup.Sound.class);

        public void play(Vec3 pos, float volume) {
            if (sound == null) return;
            log.debug("Play POI sound [{}] at [x:{} y:{} z{}]", sound, pos.x, pos.y, pos.z);
            assert Minecraft.getInstance().level != null;
            Minecraft.getInstance().level.playLocalSound(pos.x, pos.y, pos.z, sound, SoundSource.BLOCKS, volume, pitch, true);
        }
    }
}
