package de.jozelot.varoX;

import de.jozelot.varoX.commands.VaroCommand;
import de.jozelot.varoX.files.ConfigManager;
import de.jozelot.varoX.files.FileManager;
import de.jozelot.varoX.files.LangManager;
import de.jozelot.varoX.listeners.*;
import de.jozelot.varoX.manager.*;
import de.jozelot.varoX.spawns.SpawnManager;
import de.jozelot.varoX.teamchests.TeamChestManager;
import de.jozelot.varoX.teams.TeamsManager;
import de.jozelot.varoX.user.UserManager;
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
    private TeamChestManager teamChestManager;

    @Override
    public void onEnable() {
        
        getLogger().info("Setting up Listener and Commands!");

        // Load Managers
        config = new ConfigManager(this);
        fileManager = new FileManager(this);
        lang = new LangManager(this);
        statesManager = new StatesManager(this);
        worldBorderManager = new WorldBorderManager(this);
        teamsManager = new TeamsManager(this);
        spawnManager = new SpawnManager( this);
        userManager = new UserManager( this);
        teamChestManager = new TeamChestManager(this);

        // File Management
        config.load();
        fileManager.saveFiles();
        lang.load();

        // Commands Initialize
        getCommand("varo").setExecutor(new VaroCommand( this));

        // Listener Initialize
        Bukkit.getPluginManager().registerEvents(new JoinLeaveListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PreGamePhaseEventBlock(this), this);
        Bukkit.getPluginManager().registerEvents(new DeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new StartProtection(), this);
        Bukkit.getPluginManager().registerEvents(new BannedItemsListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PingListener(this), this);
        Bukkit.getPluginManager().registerEvents(new FriendlyFire(this), this);
        Bukkit.getPluginManager().registerEvents(new SignListener(this), this);
        Bukkit.getPluginManager().registerEvents(new ProtectionListener(this), this);


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

    public ConfigManager getConfigManager() {
        return config;
    }

    public LangManager getLangManager() {
        return lang;
    }

    public StatesManager getStatesManager() {
        return statesManager;
    }

    public WorldBorderManager getWorldBorderManager() {
        return worldBorderManager;
    }

    public TeamsManager getTeamsManager() {
        return teamsManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public TeamChestManager getTeamChestManager() {
        return teamChestManager;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
