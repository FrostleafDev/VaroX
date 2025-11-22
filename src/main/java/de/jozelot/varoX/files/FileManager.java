package de.jozelot.varoX.files;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.jozelot.varoX.VaroX;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileManager {

    private final VaroX plugin;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public FileManager(VaroX plugin) {
        this.plugin = plugin;
    }

    public void saveFiles() {
        saveDefaultLocales();

        File file = new File(plugin.getDataFolder(), "data");
        if (!file.exists()) file.mkdirs();

        getSpawnsFile();
        getTeamsFile();
        getGameStateFile();
        getTeamChestsFile();
    }

    private void saveDefaultLocales() {
        plugin.saveResource("locales/de.yml", false);
        plugin.saveResource("locales/en.yml", false);
        plugin.saveResource("locales/fr.yml", false);
        plugin.saveResource("locales/es.yml", false);
        plugin.saveResource("locales/ru.yml", false);
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


    public <T> List<T> loadList(File file, Type listType) {
        try (FileReader reader = new FileReader(file)) {
            List<T> loadedList = gson.fromJson(reader, listType);
            return loadedList != null ? loadedList : new ArrayList<>();
        } catch (IOException e) {
            plugin.getLogger().warning("File not found or IO error for: " + file.getName() + " -> " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().warning("Could not parse JSON list for: " + file.getName() + ". Initializing with empty list: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public void saveList(File file, Object list) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(list, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("ERROR while saving list to file: " + file.getName() + " -> " + e.getMessage());
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
        File teamsFile = new File(dataDir, "teams.json");
        if (!teamsFile.exists()) {
            try {
                teamsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("ERROR while trying to create 'teams.json': " + e.getMessage()); // Korrigiert
            }
        }
        return teamsFile;
    }

    public File getTeamChestsFile() {
        File dataDir = new File(plugin.getDataFolder(), "data");
        File chestsFile = new File(dataDir, "team_chests.json");
        if (!chestsFile.exists()) {
            try {
                chestsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("ERROR while trying to create 'team_chests.json': " + e.getMessage());
            }
        }
        return chestsFile;
    }

    public File getSpawnsFile() {
        File dataDir = new File(plugin.getDataFolder(), "data");
        File spawnsFile = new File(dataDir, "spawns.json");
        if (!spawnsFile.exists()) {
            try {
                spawnsFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("ERROR while trying to create 'spawns.json': " + e.getMessage());
            }
        }
        return spawnsFile;
    }

    public File getPlayerFile() {
        File dataDir = new File(plugin.getDataFolder(), "data");
        File playerFile = new File(dataDir, "playerdata.json");
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("ERROR while trying to create 'playerdata.json': " + e.getMessage());
            }
        }
        return playerFile;
    }

    private void initializeGameStateFile(File file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Map<String, Integer> initialState = new HashMap<>();
        initialState.put("game_state", 0);

        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(initialState, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("ERROR while trying to write initial content to 'states.json': " + e.getMessage());
        }
    }
}