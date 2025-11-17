package de.jozelot.varoX.manager;

import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    private String locale;
    private String varoName;

    public void load() {
        plugin.saveDefaultConfig();

        locale = plugin.getConfig().getString("locale");
        varoName = plugin.getConfig().getString("varo-name");
    }

    public String getLocale() {
        return locale;
    }
    public String getVaroName() {
        return varoName;
    }
}
