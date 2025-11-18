package de.jozelot.varoX.listeners;

import de.jozelot.varoX.manager.LangManager;
import de.jozelot.varoX.manager.StatesManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JoinLeaveListener implements Listener {

    private final StatesManager statesManager;
    private final LangManager lang;
    private final JavaPlugin plugin;

    private List<Player> playerInJoin = new ArrayList<>();

    public JoinLeaveListener(StatesManager statesManager, JavaPlugin plugin, LangManager lang) {
        this.statesManager = statesManager;
        this.plugin = plugin;
        this.lang = lang;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (statesManager.getGameState() == 2) {
            startJoinCooldown(event.getPlayer());
            playerInJoin.add(event.getPlayer());
        }
        if (statesManager.getGameState() != 2) {
            Map<String, String> vars = new HashMap<>();
            vars.put("player_name", String.valueOf(event.getPlayer().getName()));
            event.setJoinMessage(lang.format("player-join", vars));
        }
        if (event.getPlayer().hasPermission("varox.admin")) {
            if (statesManager.getGameState() == 0) {
                event.getPlayer().sendMessage(lang.getInfoAdminStatePrephase());
            }
            if (statesManager.getGameState() == 1) {
                event.getPlayer().sendMessage(lang.getInfoAdminStateOpen());
            }
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("varox.admin")) {
            if (statesManager.getGameState() == 0) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,lang.getPlayerJoinFailClosed());
            } else if (statesManager.getGameState() == 3) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,lang.getPlayerJoinFailEnded());
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        if (playerInJoin.contains(event.getPlayer())) {
            playerInJoin.remove(event.getPlayer());
        }
        Map<String, String> vars = new HashMap<>();
        vars.put("player_name", String.valueOf(event.getPlayer().getName()));
        event.setQuitMessage(lang.format("player-leave", vars));
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (playerInJoin.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (playerInJoin.contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onMove(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if (playerInJoin.contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (playerInJoin.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBreakBlock(BlockBreakEvent event) {
        if (playerInJoin.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onMove(PlayerInteractEvent event) {
        if (playerInJoin.contains(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityAttack(EntityDamageByEntityEvent event) {

        Entity damager = event.getDamager();
        if (!(damager instanceof Player)) {
            return;
        }
        Player player = (Player) damager;
        if (playerInJoin.contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {

        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getTarget();
        if (playerInJoin.contains(player)) {
            event.setCancelled(true);
        }
    }

    private void startJoinCooldown(Player player) {
        final int startTime = 10;

        new BukkitRunnable() {
            private int timeLeft = startTime;

            @Override
            public void run() {
                if (!playerInJoin.contains(player)) {
                    this.cancel();
                }
                if (timeLeft <= 0) {
                    player.sendMessage(lang.getLoginFinish());
                    playerInJoin.remove(player);

                    this.cancel();
                    return;
                }

                if (timeLeft == 10) {
                    Map<String, String> vars = new HashMap<>();
                    vars.put("player_name", String.valueOf(player.getName()));
                    Bukkit.broadcastMessage(lang.format("login-10-second", vars));
                }
                if (timeLeft == 3 || timeLeft == 2) {
                    Map<String, String> vars = new HashMap<>();
                    vars.put("player_name", String.valueOf(player.getName()));
                    vars.put("seconds", String.valueOf(timeLeft));
                    Bukkit.broadcastMessage(lang.format("login-3-2-second", vars));
                }
                if (timeLeft == 1) {
                    Map<String, String> vars = new HashMap<>();
                    vars.put("player_name", String.valueOf(player.getName()));
                    Bukkit.broadcastMessage(lang.format("login-1-second", vars));
                }

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
