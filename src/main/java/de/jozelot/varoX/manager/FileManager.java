package de.jozelot.varoX.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class FileManager {

    private final JavaPlugin plugin;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public FileManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void saveFiles() {
        saveDefaultLocales();

        File file = new File(plugin.getDataFolder(), "data");
        if (!file.exists()) file.mkdirs();

        getSpawnsFile();
        getTeamsFile();
        getGameStateFile();
    }

    private void saveDefaultLocales() {
        plugin.saveResource("locales/de.yml", false);
        plugin.saveResource("locales/en.yml", false);
    }

    public <T> T load(File file, String key, Class<T> type) {
        try (FileReader reader = new FileReader(file)) {
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> data = gson.fromJson(reader, mapType);

            if (data != null && data.containsKey(key)) {
                return gson.fromJson(gson.toJson(data.get(key)), type);
            }
        } catch (IOException e) {
        }
        return null;
    }

    public void save(File file, String key, Object value) {
        Map<String, Object> data = new HashMap<>();

        try (FileReader reader = new FileReader(file)) {
            Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
            Map<String, Object> existingData = gson.fromJson(reader, mapType);
            if (existingData != null) {
                data.putAll(existingData);
            }
        } catch (IOException e) {
        }

        data.put(key, value);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("ERROR while saving data to file: " + e.getMessage());
        }
    }

    public File getGameStateFile() {
        File dataDir = new File(plugin.getDataFolder(), "data");

        File stateFile = new File(dataDir, "states.json");

        if (!stateFile.exists()) {
            try {
                if (stateFile.createNewFile()) {
                    initializeGameStateFile(stateFile);
                }
                } catch (IOException e) {
                plugin.getLogger().severe("ERROR while trying to create 'states.json': " + e.getMessage());
            }
        }

        return stateFile;
    }
    public File getTeamsFile() {
        File dataDir = new File(plugin.getDataFolder(), "data");

        File stateFile = new File(dataDir, "teams.json");

        if (!stateFile.exists()) {
            try {
                stateFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("ERROR while trying to create 'states.json': " + e.getMessage());
            }
        }

        return stateFile;
    }
    public File getSpawnsFile() {
        File dataDir = new File(plugin.getDataFolder(), "data");

        File stateFile = new File(dataDir, "spawns.json");

        if (!stateFile.exists()) {
            try {
                stateFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("ERROR while trying to create 'states.json': " + e.getMessage());
            }
        }

        return stateFile;
    }
    public File getPlayerFile() {
        File dataDir = new File(plugin.getDataFolder(), "data");

        File stateFile = new File(dataDir, "playerdata.json");

        if (!stateFile.exists()) {
            try {
                stateFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("ERROR while trying to create 'states.json': " + e.getMessage());
            }
        }

        return stateFile;
    }

    private void initializeGameStateFile(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // {"game_state": 0}
        Map<String, Integer> initialState = new HashMap<>();
        initialState.put("game_state", 0);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(initialState, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("ERROR while trying to write initial content to 'states.json': " + e.getMessage());
        }
    }
}
