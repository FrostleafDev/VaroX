package de.jozelot.varoX.listeners;

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
import java.util.List;

public class JoinLeaveListener implements Listener {

    private final StatesManager statesManager;
    private final JavaPlugin plugin;

    private List<Player> playerInJoin = new ArrayList<>();

    public JoinLeaveListener(StatesManager statesManager, JavaPlugin plugin) {
        this.statesManager = statesManager;
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (statesManager.getGameState() == 2) {
            startJoinCooldown(event.getPlayer());
            playerInJoin.add(event.getPlayer());
        }
        if (statesManager.getGameState() != 2) {
            event.setJoinMessage("§e" + event.getPlayer().getName() + "§3 hat den Server betreten");
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPermission("varox.admin")) {
            if (statesManager.getGameState() == 0) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,"§4Varo 4 hat noch nicht begonnen.");
            } else if (statesManager.getGameState() == 3) {
                event.disallow(PlayerLoginEvent.Result.KICK_OTHER,"§4Varo 4 ist schon vorbei. §cDanke fürs Mitspielen.");
            }
        }
        else {
            if (statesManager.getGameState() == 0) {
                player.sendMessage("§eDas Varo befindet sich in der Vorbereitungsphase.");
            }
            if (statesManager.getGameState() == 1) {
                player.sendMessage("§eDas Varo ist offen und kann ab jetzt gestartet werden.");
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event) {
        if (playerInJoin.contains(event.getPlayer())) {
            playerInJoin.remove(event.getPlayer());
        }
        event.setQuitMessage("§e" + event.getPlayer().getName() + "§3 hat den Server verlassen");
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
                    player.sendMessage("§cDu bist nun verwundbar");
                    playerInJoin.remove(player);

                    this.cancel();
                    return;
                }

                if (timeLeft == 10) {
                    Bukkit.broadcastMessage("§e" + player.getName() + "§3 hat den Server betreten und ist in §e10§3 Sekunden angreifbar");
                }
                if (timeLeft == 3 || timeLeft == 2) {
                    Bukkit.broadcastMessage("§e" + player.getName() + "§3 ist in §e" + timeLeft + "§3 Sekunden angreifbar");
                }
                if (timeLeft == 1) {
                    Bukkit.broadcastMessage("§e" + player.getName() + "§3 ist in §eeiner§3 Sekunde angreifbar");
                }

                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
