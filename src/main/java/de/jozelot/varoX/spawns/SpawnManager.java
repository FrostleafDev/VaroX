package de.jozelot.varoX.spawns;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.jozelot.varoX.VaroX; // Angenommen, VaroX ist Ihr Haupt-Plugin
import de.jozelot.varoX.files.FileManager;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SpawnManager {

    private final FileManager fileManager;
    private final VaroX plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public SpawnManager(VaroX plugin) {
        this.plugin = plugin;
        this.fileManager = plugin.getFileManager();
    }

    private List<Spawn> loadSpawnsFromFile() {
        File spawnsFile = fileManager.getSpawnsFile();
        Type spawnListType = new TypeToken<List<Spawn>>(){}.getType();

        try (FileReader reader = new FileReader(spawnsFile)) {
            List<Spawn> loadedSpawns = gson.fromJson(reader, spawnListType);
            return loadedSpawns != null ? loadedSpawns : new ArrayList<>();
        } catch (IOException e) {
            plugin.getLogger().severe("ERROR while loading spawns: " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().warning("Could not parse spawns.json. Initializing with empty list: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private void saveSpawnsToFile(List<Spawn> spawnsToSave) {
        File spawnsFile = fileManager.getSpawnsFile();

        try (FileWriter writer = new FileWriter(spawnsFile)) {
            gson.toJson(spawnsToSave, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("ERROR while saving spawns: " + e.getMessage());
        }
    }

    public Spawn createSpawn(int id, double[] positionData) { // <-- ID als neuen Parameter hinzugefügt
        List<Spawn> spawns = loadSpawnsFromFile();

        if (spawns.stream().anyMatch(spawn -> spawn.getId() == id)) {
            return null;
        }

        Spawn newSpawn = new Spawn(id, positionData);

        spawns.add(newSpawn);
        saveSpawnsToFile(spawns);

        return newSpawn;
    }

    public List<Spawn> getAllSpawns() {
        return Collections.unmodifiableList(loadSpawnsFromFile());
    }

    public Optional<Spawn> getSpawnById(int id) {
        return loadSpawnsFromFile().stream()
                .filter(spawn -> spawn.getId() == id)
                .findFirst();
    }

    public boolean deleteSpawnById(int id) {
        List<Spawn> spawns = loadSpawnsFromFile();

        boolean removed = spawns.removeIf(spawn -> spawn.getId() == id);

        if (removed) {
            saveSpawnsToFile(spawns);
        }
        return removed;
    }

    public void deleteAllSpawns() {
        saveSpawnsToFile(new ArrayList<>());
    }
}