package de.jozelot.varoX.manager;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private String locale;
    private String varoName;

    private double worldboarderCenterX;
    private double worldboarderCenterZ;
    private int startSize;

    public double getWorldboarderCenterX() {
        return worldboarderCenterX;
    }

    public double getWorldboarderCenterZ() {
        return worldboarderCenterZ;
    }

    public int getStartSize() {
        return startSize;
    }

    public void load() {
        plugin.saveDefaultConfig();

        locale = plugin.getConfig().getString("locale");
        varoName = plugin.getConfig().getString("varo-name");
        worldboarderCenterX = plugin.getConfig().getDouble("varo_center_x");
        worldboarderCenterZ = plugin.getConfig().getDouble("varo_center_z");
        startSize = plugin.getConfig().getInt("varo_start_border_size");
    }

    public String getLocale() {
        return locale;
    }

    public String getVaroName() {
        return varoName;
    }
}
