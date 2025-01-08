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
    public static final Map<String, SoundEvent> REGISTERED_SOUNDS = new HashMap<>();
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(MainClass.MOD_ID,
            RegistryKeys.SOUND_EVENT);
    private static final String[] SOUND_NAMES = {
            "y_up",
            "y_down"
    };

    public CustomSounds() {
        try {
            registerSounds();
            SOUNDS.register();
        } catch (Exception E) {
            log.error("The custom sounds class couldn't be set up.", E);
        }
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

}
