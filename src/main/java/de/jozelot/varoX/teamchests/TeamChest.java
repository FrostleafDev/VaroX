package de.jozelot.varoX.teamchests;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class TeamChest {

    private final String teamName;
    private final String teamNameKey;
    private final String worldName;
    private final int x;
    private final int y;
    private final int z;
    private final int signX;
    private final int signY;
    private final int signZ;

    public TeamChest(String teamName, Location chestLoc, Location signLoc) {
        this.teamName = teamName;
        this.teamNameKey = teamName;
        this.worldName = chestLoc.getWorld().getName();
        this.x = chestLoc.getBlockX();
        this.y = chestLoc.getBlockY();
        this.z = chestLoc.getBlockZ();
        this.signX = signLoc.getBlockX();
        this.signY = signLoc.getBlockY();
        this.signZ = signLoc.getBlockZ();
    }

    public String getTeamName() {
        return teamName;
    }

    public String getTeamNameKey() {
        return teamNameKey;
    }

    public String getWorldName() {
        return worldName;
    }

    public Location getChestLocation() {
        Block chestBlock = org.bukkit.Bukkit.getWorld(worldName).getBlockAt(x, y, z);
        return chestBlock.getLocation();
    }

    public Location getSignLocation() {
        Block signBlock = org.bukkit.Bukkit.getWorld(worldName).getBlockAt(signX, signY, signZ);
        return signBlock.getLocation();
    }

    public Block getChestBlock() {
        return org.bukkit.Bukkit.getWorld(worldName).getBlockAt(x, y, z);
    }

    public Block getSignBlock() {
        return org.bukkit.Bukkit.getWorld(worldName).getBlockAt(signX, signY, signZ);
    }
}