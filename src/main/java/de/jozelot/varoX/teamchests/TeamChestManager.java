package de.jozelot.varoX.teamchests;

import com.google.gson.reflect.TypeToken;
import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.files.FileManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.block.BlockFace;

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
        Material type = block.getType();

        if (type != Material.CHEST && type != Material.TRAPPED_CHEST) {
            return loc;
        }

        // 1. Prüfe auf DoubleChest über InventoryHolder
        BlockState state = block.getState();
        if (state instanceof InventoryHolder) {
            InventoryHolder holder = (InventoryHolder) state;

            if (holder instanceof DoubleChest) {
                DoubleChest doubleChest = (DoubleChest) holder;

                InventoryHolder left = doubleChest.getLeftSide();
                InventoryHolder right = doubleChest.getRightSide();

                Location loc1 = (left instanceof BlockState) ? ((BlockState) left).getLocation() : null;
                Location loc2 = (right instanceof BlockState) ? ((BlockState) right).getLocation() : null;

                if (loc1 != null && loc2 != null) {
                    return (loc1.getBlockX() < loc2.getBlockX() ||
                            (loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockZ() < loc2.getBlockZ()))
                            ? loc1 : loc2;
                }
            }
        }

        Location normalizedLoc = loc;

        BlockFace[] faces = new BlockFace[] {
                BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST
        };

        for (BlockFace face : faces) {
            Block neighbor = block.getRelative(face);

            if (neighbor.getType() == type) {
                Location neighborLoc = neighbor.getLocation();

                if (neighborLoc.getBlockX() < normalizedLoc.getBlockX() ||
                        (neighborLoc.getBlockX() == normalizedLoc.getBlockX() && neighborLoc.getBlockZ() < normalizedLoc.getBlockZ())) {

                    normalizedLoc = neighborLoc;
                }
            }
        }

        return normalizedLoc;
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
            if (loc != null && loc.getWorld() != null) {
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
                .collect(Collectors.toList());

        fileManager.saveList(chestsFile, chestsToSave);
    }
}