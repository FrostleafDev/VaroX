package de.jozelot.varoX.commands;

import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.util.HashMap;
import java.util.Map;

public class VaroCommand implements CommandExecutor {

    private final ConfigManager config;
    private final LangManager lang;
    private final FileManager fileManager;
    private final StatesManager statesManager;
    private final VaroX plugin;
    private final WorldBorderManager worldBorderManager;

    public VaroCommand(ConfigManager config, LangManager lang, FileManager fileManager, StatesManager statesManager, VaroX plugin, WorldBorderManager worldBorderManager) {
        this.config = config;
        this.lang = lang;
        this.fileManager = fileManager;
        this.statesManager = statesManager;
        this.plugin = plugin;
        this.worldBorderManager = worldBorderManager;
    }

    private boolean varoStarting = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("varox.admin")) {
            sender.sendMessage(lang.getNoPermission());
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("start")) {
                if (varoStarting) {
                    sender.sendMessage(lang.getCommandVaroStartAlready());
                    return true;
                }
                if (statesManager.getGameState() == 0) {
                    sender.sendMessage(lang.getCommandVaroStartPrephase());
                    return true;
                }
                if (statesManager.getGameState() == 2) {
                    sender.sendMessage(lang.getCommandVaroStartRunning());
                    return true;
                }
                if (statesManager.getGameState() == 3) {
                    sender.sendMessage(lang.getCommandVaroStartStopped());
                    return true;
                }
                sender.sendMessage(lang.getCommandVaroStartSuccess());
                varoStarting = true;
                worldBorderManager.startWorldBorderSystem();
                startVaro();
                return true;
            }
            if (args[0].equalsIgnoreCase("end")) {
                if (statesManager.getGameState() == 3) {
                    sender.sendMessage(lang.getCommandVaroEndAlready());
                    return true;
                }
                if (statesManager.getGameState() == 0 || statesManager.getGameState() == 1) {
                    sender.sendMessage(lang.getCommandVaroEndFail());
                    return true;
                }
                statesManager.setGameState(3);
                worldBorderManager.getBorderTask().cancel();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.kickPlayer(lang.getCommandVaroEndKick());
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("open")) {
                if (statesManager.getGameState() == 3) {
                    sender.sendMessage(lang.getCommandVaroOpenAlreadyEnd());
                    return true;
                }
                if (statesManager.getGameState() == 1 || statesManager.getGameState() == 2) {
                    sender.sendMessage(lang.getCommandVaroOpenAlready());
                    return true;
                }
                statesManager.setGameState(1);
                sender.sendMessage(lang.getCommandVaroOpen());
                return true;
            }
            if (args[0].equalsIgnoreCase("close")) {
                if (statesManager.getGameState() == 3) {
                    sender.sendMessage(lang.getCommandVaroCloseEnded());
                    return true;
                }
                if (statesManager.getGameState() == 0) {
                    sender.sendMessage(lang.getCommandVaroCloseClosed());
                    return true;
                }
                if (statesManager.getGameState() == 2) {
                    sender.sendMessage(lang.getCommandVaroCloseStarted());
                    return true;
                }
                statesManager.setGameState(0);
                sender.sendMessage(lang.getCommandVaroClose());
                return true;
            }
            if (args[0].equalsIgnoreCase("reset")) {
                statesManager.setGameState(0);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.hasPermission("varox.admin")) {
                        player.kickPlayer(lang.getCommandVaroResetUser());
                    }
                    else { player.kickPlayer(lang.getCommandVaroResetAdminFirst() + "\n" + lang.getCommandVaroResetAdminSecond());}
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.shutdown();
                    }
                }.runTaskLater(plugin, 1L);
                return true;
            }
            if (args[0].equalsIgnoreCase("reload")) {
                reloadPlugin();
                sender.sendMessage(lang.getCommandVaroReloadSuccess());
            }
        }
        sendHelpMessage(sender);
        return true;
    }

    private void reloadPlugin() {
        config.load();
        lang.load();
        fileManager.saveFiles();
    }

    private void sendHelpMessage(CommandSender sender) {
        sender.sendMessage("Herlp");
    }

    private void startVaro() {
        final int startTime = 60;

        new BukkitRunnable() {
            private int timeLeft = startTime;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    Bukkit.broadcastMessage(lang.getVaroStart());

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
                    }

                    varoStarting = false;
                    statesManager.setGameState(2);
                    this.cancel();
                    return;
                }

                if (timeLeft == 60 || timeLeft == 30 || timeLeft == 20 || timeLeft == 15 || timeLeft == 10 || timeLeft <= 5) {

                    Map<String, String> vars = new HashMap<>();
                    vars.put("seconds", String.valueOf(timeLeft));

                    Bukkit.broadcastMessage(lang.format("varo-starting", vars));

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1f, 1f);
                    }
                }

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
