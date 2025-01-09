package org.mcaccess.minecraftaccess.utils.system;

import org.mcaccess.minecraftaccess.MainClass;
import lombok.extern.slf4j.Slf4j;
import dev.architectury.registry.registries.DeferredRegister;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import com.google.gson.*;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * This class reads the sounds.json file, parses it, and loads custom sounds
 * into memory.
 * All sound entries can be registered in the JSON file located at:
 * "common/src/main/resources/assets/minecraft_access/sounds.json"
 * The actual sound files should be placed in:
 * "common/src/main/resources/assets/minecraft_access/sounds/"
 */
@Slf4j
public class CustomSounds {
    public static final Map<String, SoundEvent> REGISTERED_SOUNDS = new HashMap<>();
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(MainClass.MOD_ID,
            RegistryKeys.SOUND_EVENT);
    private static List<String> soundNames = new ArrayList<>();

    public CustomSounds() {
        loadSoundNames();
        registerSounds();
        SOUNDS.register();
    }

    private void loadSoundNames() {
        Gson gson = new Gson();

        InputStream inputStream = MainClass.class.getResourceAsStream("/assets/minecraft_access/sounds.json");
        if (inputStream == null) {
            log.error("Failed to locate sounds.json file in resources.");
            return;
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        JsonObject jsonObject = gson.fromJson(reader, JsonObject.class);

        // Extract the sound names from the keys of the JSON object
        for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            soundNames.add(entry.getKey());
        }
        log.info("Loaded sound names: {}", soundNames);
    }

    private void registerSounds() {
        for (String soundName : soundNames) {
            Identifier temp_id = Identifier.of(MainClass.MOD_ID, soundName);
            SoundEvent temp_event = SoundEvent.of(temp_id);
            SOUNDS.register(temp_id, () -> temp_event);
            REGISTERED_SOUNDS.put(soundName, temp_event);
            log.info("{} has been loaded.", soundName);

        }
    }
}
