package de.jozelot.varoX.teamchests;

import com.google.gson.reflect.TypeToken;
import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.files.FileManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.block.BlockState; // Fügen Sie diesen Import hinzu

import java.io.File;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class TeamChestManager {

    private final VaroX plugin;
    private final FileManager fileManager;

    private final Map<String, Map<String, TeamChest>> registeredChests = new HashMap<>();

    private static final Type TEAM_CHEST_LIST_TYPE = new TypeToken<List<TeamChest>>(){}.getType();

    public TeamChestManager(VaroX plugin) {
        this.plugin = plugin;
        this.fileManager = plugin.getFileManager();
        loadChestsFromFile();
    }

    private String getLocationKey(Location loc) {
        return loc.getBlockX() + "_" + loc.getBlockY() + "_" + loc.getBlockZ();
    }

    public Location normalizeChestLocation(Location loc) {
        Block block = loc.getBlock();

        if (block.getState() instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) block.getState();

            if (holder instanceof DoubleChest) {
                DoubleChest dc = (DoubleChest) holder;

                InventoryHolder left = dc.getLeftSide();
                InventoryHolder right = dc.getRightSide();

                if (left instanceof BlockState && right instanceof BlockState) {
                    Location locA = ((BlockState) left).getLocation();
                    Location locB = ((BlockState) right).getLocation();

                    if (locA.getBlockX() < locB.getBlockX() ||
                            (locA.getBlockX() == locB.getBlockX() && locA.getBlockZ() < locB.getBlockZ())) {

                        return locA;
                    } else {
                        return locB;
                    }
                }
            }
        }

        return loc; // Einzelkiste oder kein Kasten
    }

    public void registerChest(TeamChest chest) {
        Location normalizedLoc = normalizeChestLocation(chest.getChestLocation());

        String world = normalizedLoc.getWorld().getName();

        registeredChests.computeIfAbsent(world, k -> new HashMap<>())
                .put(getLocationKey(normalizedLoc), chest);

        saveChestsToFile();
    }

    public TeamChest getChestAt(Location loc) {

        Location normalizedLoc = normalizeChestLocation(loc);
        String world = normalizedLoc.getWorld().getName();

        if (!registeredChests.containsKey(world)) {
            return null;
        }

        return registeredChests.get(world).get(getLocationKey(normalizedLoc));
    }

    public void removeChest(Location loc) {
        // 1. Eingangs-Location normalisieren
        Location normalizedLoc = normalizeChestLocation(loc);
        String world = normalizedLoc.getWorld().getName();

        if (registeredChests.containsKey(world)) {
            registeredChests.get(world).remove(getLocationKey(normalizedLoc));
            saveChestsToFile();
        }
    }
    private void loadChestsFromFile() {
        File chestsFile = fileManager.getTeamChestsFile();
        List<TeamChest> loadedList = fileManager.loadList(chestsFile, TEAM_CHEST_LIST_TYPE);

        registeredChests.clear();

        for (TeamChest chest : loadedList) {
            Location loc = chest.getChestLocation();
            if (loc != null) {
                String world = loc.getWorld().getName();

                registeredChests.computeIfAbsent(world, k -> new HashMap<>())
                        .put(getLocationKey(loc), chest);
            }
        }
        plugin.getLogger().info("TeamChestManager loaded " + loadedList.size() + " chests.");
    }

    private void saveChestsToFile() {
        File chestsFile = fileManager.getTeamChestsFile();

        List<TeamChest> chestsToSave = registeredChests.values().stream()
                .flatMap(map -> map.values().stream())
                .map(chest -> {
                    return chest;
                })
                .collect(Collectors.toList());

        fileManager.saveList(chestsFile, chestsToSave);
    }
}