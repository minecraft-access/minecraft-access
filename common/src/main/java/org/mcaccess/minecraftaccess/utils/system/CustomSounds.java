package org.mcaccess.minecraftaccess.utils.system;

import org.mcaccess.minecraftaccess.MainClass;
import lombok.extern.slf4j.Slf4j;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Identifier;
import java.util.HashMap;
import java.util.Map;

/* This class provides static methods to enable the creation and playback of custom sounds.
 * The sounds to be added should be placed in the below SOUND_NAMES array, separated by commas.
 * They must also be registered in the json file found at 
 * "common\src\main\resources\assets\minecraft_access\sounds.json"
 * and the sounds themselves should be saved in
 * "common\src\main\resources\assets\minecraft_access\sounds"
 * Currently the module only supports playing sounds on the player, but this will change. */

@Slf4j
public class CustomSounds {
    private CustomSounds() {
        // Helper class
    }

    public static final Map<String, SoundEvent> REGISTERED_SOUNDS = new HashMap<>();
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(MainClass.MOD_ID,
            RegistryKeys.SOUND_EVENT);
    private static final String[] SOUND_NAMES = {
            "y_up",
            "y_down"
    };

    public static void init() {
        try {
            _init();
        } catch (Exception E) {
            log.error("The custom sounds class was unable to be initialized", E);
        }
    }

    private static void _init() {
        registerSounds();
        SOUNDS.register();
    }

    public static void registerSounds() {
        for (String soundName : SOUND_NAMES) {
            try {
                Identifier temp_id = Identifier.of(MainClass.MOD_ID, soundName);
                SoundEvent temp_event = SoundEvent.of(temp_id);
                SOUNDS.register(temp_id, () -> temp_event);
                REGISTERED_SOUNDS.put(soundName, temp_event);
                log.info("{} has been loaded.", soundName);
            } catch (Exception e) {
                log.error("Failed to register sound: {}", soundName, e);
            }
        }
    }

    public static void playSoundOnPlayer(String soundName, float volume, float pitch) {
        SoundEvent soundEvent = REGISTERED_SOUNDS.get(soundName);
        if (soundEvent == null) {
            throw new IllegalArgumentException("Not able to find sound: " + soundName);
        }
        MinecraftClient.getInstance().player.playSound(soundEvent, volume, pitch);
    }
}