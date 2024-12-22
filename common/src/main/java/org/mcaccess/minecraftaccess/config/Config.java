package com.github.khanshoaib3.minecraft_access.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class Config {
    /**
     * -- GETTER --
     *  Returns the singleton instance.
     *  It's what we'll call when we want to operate on the config
     */
    @Getter
    private static final Config instance;
    private static final String CONFIG_FILE_PATH = Paths.get("config", "minecraft-access", "config.json").toString();
    private ConfigMap configMap = null;
    private Gson gson = null;

    static {
        instance = new Config();
    }

    /**
     * To prevent constructing other instances.
     */
    protected Config() {
    }

    /**
     * Loads the configurations from the config.json<br>
     * The path for the file is: config/minecraft_access/config.json
     * If the json format is wrong or an error occurs, the config.json is reset to default
     */
    public void loadConfig() {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.setPrettyPrinting();
            gson = builder.create();

            createDefaultConfigFileIfNotExist();
            configMap = readJSON();

            resetConfigToDefaultIfNotValid();

            // update config map singleton instances reference
            ConfigMap.setInstance(configMap);

        } catch (Exception e) {
            log.error("An error occurred while reading config.json file, resetting to default", e);
            resetConfigToDefault();
        } finally {
           log.info("Loaded configurations from config.json");
        }
    }

    private void resetConfigToDefaultIfNotValid() {
        if (this.configMap == null) {
            resetConfigToDefault();
        } else {
            this.configMap.resetMissingSectionsToDefault();
            writeJSON();
        }
    }

    /**
     * Resets the config.json to default
     */
    protected void resetConfigToDefault() {
        try {
            this.configMap = ConfigMap.buildDefault();
            writeJSON();
        } catch (Exception e) {
            log.error("An error occurred while resetting config.json file to default.", e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createDefaultConfigFileIfNotExist() throws IOException {
        File configFile = new File(CONFIG_FILE_PATH);
        if (configFile.exists()) return;

        configFile.getParentFile().mkdirs();
        configFile.createNewFile();
       log.info("Created an empty config.json file at: %s".formatted(configFile.getAbsolutePath()));

        resetConfigToDefault();
    }

    public void writeJSON() {
        try (Writer writer = new FileWriter(CONFIG_FILE_PATH)) {
            writer.write(gson.toJson(configMap));
        } catch (Exception e) {
            log.error("An error occurred while saving config.json file.", e);
        }
    }

    private ConfigMap readJSON() throws IOException {
        return gson.fromJson(Files.readString(Path.of(CONFIG_FILE_PATH)), ConfigMap.class);
    }
}