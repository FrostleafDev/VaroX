package de.jozelot.varoX.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class LangManager {

    private final ConfigManager config;
    private final JavaPlugin plugin;

    private File langFile;
    private FileConfiguration langConfig;

    public LangManager(JavaPlugin plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    private String locale;

    public void load() {
        locale = config.getLocale();

        File localeFolder = new File(plugin.getDataFolder(), "locales");
        if (!localeFolder.exists()) localeFolder.mkdirs();

        langFile = new File(localeFolder, locale + ".yml");

        if (!langFile.exists()) {
            plugin.getLogger().warning("Language '" + locale + "' not found! Falling back to 'en'.");
            langFile = new File(localeFolder, "en.yml");
        } else {
            plugin.getLogger().info("Language loaded: '" + locale + "'");
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);

        loadData();
    }

    private void loadData() {

    }
}
