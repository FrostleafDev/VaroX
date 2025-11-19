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
import java.util.*;
import java.util.stream.Collectors;

public class TeamsManager {

    private final JavaPlugin plugin;
    private final FileManager fileManager;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public TeamsManager(JavaPlugin plugin, FileManager fileManager) {
        this.plugin = plugin;
        this.fileManager = fileManager;
    }

    private List<Team> loadTeamsFromFile() {
        File teamsFile = fileManager.getTeamsFile();
        Type teamListType = new TypeToken<List<Team>>(){}.getType();

        try (FileReader reader = new FileReader(teamsFile)) {
            List<Team> loadedTeams = gson.fromJson(reader, teamListType);
            return loadedTeams != null ? loadedTeams : new ArrayList<>();
        } catch (IOException e) {
            plugin.getLogger().severe("ERROR while loading teams: " + e.getMessage());
        } catch (Exception e) {
            plugin.getLogger().warning("Could not parse teams.json. Initializing with empty list: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    private void saveTeamsToFile(List<Team> teamsToSave) {
        File teamsFile = fileManager.getTeamsFile();

        try (FileWriter writer = new FileWriter(teamsFile)) {
            gson.toJson(teamsToSave, writer);
        } catch (IOException e) {
            plugin.getLogger().severe("ERROR while saving teams: " + e.getMessage());
        }
    }


    public Team createTeam(String name, List<String> initialMembers) {
        List<Team> teams = loadTeamsFromFile();

        if (teams.stream().anyMatch(team -> team.getName().equalsIgnoreCase(name))) {
            plugin.getLogger().warning("Team konnte nicht erstellt werden. Name '" + name + "' existiert bereits.");
            return null;
        }

        int newId = teams.stream().mapToInt(Team::getId).max().orElse(0) + 1;

        Team newTeam = new Team(newId, name, initialMembers);

        teams.add(newTeam);
        saveTeamsToFile(teams);

        return newTeam;
    }

    public List<Team> getAllTeams() {
        return Collections.unmodifiableList(loadTeamsFromFile());
    }

    public Optional<Team> getTeamById(int id) {
        return loadTeamsFromFile().stream()
                .filter(team -> team.getId() == id)
                .findFirst();
    }

    public Optional<Team> getTeamByName(String name) {
        return loadTeamsFromFile().stream()
                .filter(team -> team.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public void updateTeam(Team updatedTeam) {
        List<Team> teams = loadTeamsFromFile();

        teams.replaceAll(team -> team.getId() == updatedTeam.getId() ? updatedTeam : team);

        saveTeamsToFile(teams);
    }

    public boolean deleteTeamById(int id) {
        List<Team> teams = loadTeamsFromFile();

        boolean removed = teams.removeIf(team -> team.getId() == id);

        if (removed) {
            saveTeamsToFile(teams);
        }
        return removed;
    }

    public void checkAndSetTeamStatus(String playerName, UserManager userManager) {
        Optional<Team> teamOptional = loadTeamsFromFile().stream()
                .filter(team -> team.getMembers().stream()
                        .anyMatch(member -> member.equalsIgnoreCase(playerName)))
                .findFirst();

        if (!teamOptional.isPresent()) {
          //  plugin.getLogger().warning("Spieler " + playerName + " ist gestorben, aber keinem Team zugeordnet.");
            return;
        }

        Team team = teamOptional.get();

        if (!team.isAlive()) {
            return;
        }

        boolean allMembersDead = true;

        for (String memberName : team.getMembers()) {
            Optional<User> userOpt = userManager.getUserByName(memberName);

            if (userOpt.isPresent()) {
                if (userOpt.get().isAlive()) {
                    allMembersDead = false;
                    break;
                }
            }
        }

        if (allMembersDead) {
            team.setAlive(false);
            updateTeam(team);

            //plugin.getLogger().info("Alle Mitglieder von Team " + team.getName() + " sind ausgeschieden. Teamstatus auf TOT gesetzt.");
        }
    }
    public void deleteAllTeams() {
        saveTeamsToFile(new ArrayList<>());
    }
}