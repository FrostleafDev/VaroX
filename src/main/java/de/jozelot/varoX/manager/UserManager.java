package de.jozelot.varoX.manager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import de.jozelot.varoX.VaroX; // Angenommen, VaroX ist Ihr Haupt-Plugin

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserManager {

    private final FileManager fileManager;
    private final VaroX plugin;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public UserManager(FileManager fileManager, VaroX plugin) {
        this.fileManager = fileManager;
        this.plugin = plugin;
    }

    private List<User> loadUsersFromFile() {
        File usersFile = fileManager.getPlayerFile();
        Type userListType = new TypeToken<List<User>>(){}.getType();

        try (FileReader reader = new FileReader(usersFile)) {
            List<User> loadedUsers = gson.fromJson(reader, userListType);
            return loadedUsers != null ? loadedUsers : new ArrayList<>();
        } catch (IOException e) {
            // plugin.getLogger().severe("FEHLER beim Laden der Spielerdaten: " + e.getMessage());
        } catch (Exception e) {
            // plugin.getLogger().warning("Konnte playerdata.json nicht parsen. Initialisiere mit leerer Liste: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private void saveUsersToFile(List<User> usersToSave) {
        File usersFile = fileManager.getPlayerFile();

        try (FileWriter writer = new FileWriter(usersFile)) {
            gson.toJson(usersToSave, writer);
        } catch (IOException e) {
            // plugin.getLogger().severe("FEHLER beim Speichern der Spielerdaten: " + e.getMessage());
        }
    }

    public Optional<User> getUserByName(String name) {
        return loadUsersFromFile().stream()
                .filter(user -> user.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public User registerUser(String name) {
        List<User> users = loadUsersFromFile();

        if (users.stream().anyMatch(user -> user.getName().equalsIgnoreCase(name))) {
            return null;
        }

        User newUser = new User(name);
        users.add(newUser);
        saveUsersToFile(users);

        return newUser;
    }

    public boolean updateUser(User user) {
        List<User> users = loadUsersFromFile();

        Optional<User> existingUserOpt = users.stream()
                .filter(u -> u.getName().equalsIgnoreCase(user.getName()))
                .findFirst();

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            int index = users.indexOf(existingUser);
            users.set(index, user);

            saveUsersToFile(users);
            return true;
        }
        return false;
    }

    public List<User> getAllUsers() {
        return Collections.unmodifiableList(loadUsersFromFile());
    }

    public boolean deleteUserByName(String name) {
        List<User> users = loadUsersFromFile();

        boolean removed = users.removeIf(user -> user.getName().equalsIgnoreCase(name));

        if (removed) {
            saveUsersToFile(users);
        }
        return removed;
    }

    public void deleteAllUsers() {
        saveUsersToFile(new ArrayList<>());
    }
}