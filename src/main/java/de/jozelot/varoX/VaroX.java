package de.jozelot.varoX;

import de.jozelot.varoX.commands.VaroCommand;
import de.jozelot.varoX.listeners.*;
import de.jozelot.varoX.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class VaroX extends JavaPlugin {

    private ConfigManager config;
    private FileManager fileManager;
    private LangManager lang;

    private StatesManager statesManager;
    private WorldBorderManager worldBorderManager;
    private TeamsManager teamsManager;
    private SpawnManager spawnManager;
    private UserManager userManager;

    @Override
    public void onEnable() {
        
        getLogger().info("Setting up Listener and Commands!");

        // Load Managers
        config = new ConfigManager(this);
        fileManager = new FileManager(this);
        lang = new LangManager(this, config);
        statesManager = new StatesManager(fileManager, this);
        worldBorderManager = new WorldBorderManager(this, config, statesManager);
        teamsManager = new TeamsManager(this, fileManager);
        spawnManager = new SpawnManager(fileManager, this);
        userManager = new UserManager(fileManager, this);


        // File Management
        config.load();
        fileManager.saveFiles();
        lang.load();

        // Commands Initialize
        getCommand("varo").setExecutor(new VaroCommand(config, lang, fileManager, statesManager, this, worldBorderManager, teamsManager, spawnManager, userManager));

        // Listener Initialize
        Bukkit.getPluginManager().registerEvents(new JoinLeaveListener(statesManager, this, lang, teamsManager, spawnManager, userManager), this);
        Bukkit.getPluginManager().registerEvents(new PreGamePhaseEventBlock(this, config, statesManager), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(this, lang, userManager, teamsManager), this);
        Bukkit.getPluginManager().registerEvents(new StartProtection(), this);
        Bukkit.getPluginManager().registerEvents(new BannedItemsListener(config, lang), this);
        Bukkit.getPluginManager().registerEvents(new PingListener(config), this);


        // After Plugin start
        Bukkit.getConsoleSender().sendMessage("§7========== §6[ VaroX ] §7==========");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§aPlugin version: §c" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("§aMinecraft version: §c"+ Bukkit.getBukkitVersion());
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§aPlugin started successfully!");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§7========== §6[ VaroX ] §7==========");

        // Start important schedules
        startPermanentDay();
        if (config.isEnableWorldBorder()) {
            worldBorderManager.resumeWorldBorderSystem();
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getConsoleSender().sendMessage("§7========== §6[ VaroX ] §7==========");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§aPlugin version: §c" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("§aMinecraft version: §c"+ Bukkit.getBukkitVersion());
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§aPlugin shutdown successfully!");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§7========== §6[ VaroX ] §7==========");
    }

    public void startPermanentDay() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (statesManager.getGameState() == 0 || statesManager.getGameState() == 1) {
                    for (World world : Bukkit.getWorlds()) {
                        world.setTime(1);
                    }
                } else {
                    this.cancel();
                }
            }
        }.runTaskTimer(this, 0L, 20L);
    }
}
