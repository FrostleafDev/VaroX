package de.jozelot.varoX.commands;

import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.manager.ConfigManager;
import de.jozelot.varoX.manager.FileManager;
import de.jozelot.varoX.manager.LangManager;
import de.jozelot.varoX.manager.StatesManager;
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

public class VaroCommand implements CommandExecutor {

    private final ConfigManager config;
    private final LangManager lang;
    private final FileManager fileManager;
    private final StatesManager statesManager;
    private final VaroX plugin;

    public VaroCommand(ConfigManager config, LangManager lang, FileManager fileManager, StatesManager statesManager, VaroX plugin) {
        this.config = config;
        this.lang = lang;
        this.fileManager = fileManager;
        this.statesManager = statesManager;
        this.plugin = plugin;
    }

    private boolean varoStarting = false;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("varox.admin")) {
            sender.sendMessage("§cDu hast dazu keine Rechte!");
            return true;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("start")) {
                if (varoStarting) {
                    sender.sendMessage("§cDas Varo ist schon am starten!");
                    return true;
                }
                if (statesManager.getGameState() == 0) {
                    sender.sendMessage("§cDas Varo befindet sich noch in der Vorbereitung! Nutze /varo open und danach start!");
                    return true;
                }
                if (statesManager.getGameState() == 2) {
                    sender.sendMessage("§cDas Varo läuft bereits!");
                    return true;
                }
                if (statesManager.getGameState() == 3) {
                    sender.sendMessage("§cDas Varo wurde beendet!");
                    return true;
                }
                sender.sendMessage("§aDas Varo wurde in den Start Modus versätzt!");
                varoStarting = true;
                startVaro();
            }
            if (args[0].equalsIgnoreCase("end")) {
                if (statesManager.getGameState() == 3) {
                    sender.sendMessage("§cDas Varo wurde bereits beendet!");
                    return true;
                }
                if (statesManager.getGameState() == 0 || statesManager.getGameState() == 1) {
                    sender.sendMessage("§cDu kannst das Varo nicht beenden bevor es begonnen hat!");
                    return true;
                }
                statesManager.setGameState(3);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.kickPlayer("§4Varo 4 wurde frühzeitig beendet.");
                }
            }
            if (args[0].equalsIgnoreCase("open")) {
                if (statesManager.getGameState() == 3) {
                    sender.sendMessage("§cDas Varo wurde bereits beendet!");
                    return true;
                }
                if (statesManager.getGameState() == 1 || statesManager.getGameState() == 2) {
                    sender.sendMessage("§cDas Varo ist bereits geöffnet!");
                    return true;
                }
                statesManager.setGameState(1);
                sender.sendMessage("§aVaro 4 wurde geöffnet!");
            }
            if (args[0].equalsIgnoreCase("close")) {
                if (statesManager.getGameState() == 3) {
                    sender.sendMessage("§cDas Varo wurde bereits beendet!");
                    return true;
                }
                if (statesManager.getGameState() == 0) {
                    sender.sendMessage("§cDas Varo ist nicht offen!");
                    return true;
                }
                if (statesManager.getGameState() == 2) {
                    sender.sendMessage("§cDas Varo hat bereits gestartet!");
                    return true;
                }
                statesManager.setGameState(0);
                sender.sendMessage("§aVaro 4 wurde geschlossen!");
            }
            if (args[0].equalsIgnoreCase("reset")) {
                statesManager.setGameState(0);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!player.hasPermission("varox.admin")) {
                        player.kickPlayer("§4Varo 4 wurde frühzeitig beendet.");
                    }
                    else { player.kickPlayer("§cVaro 4 wurde frühzeitig beendet und zurückgesetzt.\n §4Du musst jetzt die 3 Welten Ordner löschen!");}
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.shutdown();
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
        return true;
    }

    private void startVaro() {
        final int startTime = 60;

        new BukkitRunnable() {
            private int timeLeft = startTime;

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    Bukkit.broadcastMessage(ChatColor.GREEN + "Mögen die Spiele beginnen!");

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
                    }

                    varoStarting = false;
                    statesManager.setGameState(2);
                    this.cancel();
                    return;
                }

                if (timeLeft == 60 || timeLeft == 30 || timeLeft == 20 || timeLeft == 15 || timeLeft == 10 || timeLeft <= 5) {
                    Bukkit.broadcastMessage(ChatColor.YELLOW + config.getVaroName() + " beginnt in " + timeLeft + " Sekunden");
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1f, 1f);
                    }
                }

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
