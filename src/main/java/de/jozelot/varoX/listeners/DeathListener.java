package de.jozelot.varoX.listeners;

import de.jozelot.varoX.manager.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DeathListener implements Listener {

    private final Map<UUID, CombatInfo> combatLog = new HashMap<>();
    private final long TIMEOUT_SECONDS = 10; // Hinweis: Wert sollte aus Config kommen
    private final JavaPlugin plugin;
    private final Set<UUID> waitingForKick = new HashSet<>();
    private final LangManager lang;

    public DeathListener(JavaPlugin plugin, LangManager lang) {
        this.plugin = plugin;
        this.lang = lang;
    }

    private static class CombatInfo {

        private final UUID damagerUUID;
        private final long timestamp;

        public CombatInfo(UUID damagerUUID, long timestamp) {
            this.damagerUUID = damagerUUID;
            this.timestamp = timestamp;
        }

        public UUID getDamagerUUID() {
            return damagerUUID;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player victim = (Player) event.getEntity();
        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        }
        else if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) {
                damager = (Player) projectile.getShooter();
            }
        }

        if (damager != null && !victim.equals(damager)) {
            long currentTimestamp = System.currentTimeMillis();
            CombatInfo info = new CombatInfo(damager.getUniqueId(), currentTimestamp);

            combatLog.put(victim.getUniqueId(), info);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        UUID victimUUID = victim.getUniqueId();

        event.setDeathMessage(null);

        Map<String, String> deathStandart = new HashMap<>();
        deathStandart.put("player_name", String.valueOf(victim.getName()));

        String fallbackMessage = lang.format("death-standart", deathStandart);

        String finalDeathMessage = fallbackMessage;

        boolean killerFound = false;

        if (combatLog.containsKey(victimUUID)) {
            CombatInfo info = combatLog.get(victimUUID);
            long timeElapsed = System.currentTimeMillis() - info.getTimestamp();
            long timeoutMillis = TIMEOUT_SECONDS * 1000;

            if (timeElapsed <= timeoutMillis) {
                Player killer = Bukkit.getPlayer(info.getDamagerUUID());

                if (killer != null && killer.isOnline()) {

                    Map<String, String> deathByPlayer = new HashMap<>();
                    deathByPlayer.put("player_name", String.valueOf(victim.getName()));
                    deathByPlayer.put("killer_name", String.valueOf(killer.getName()));

                    finalDeathMessage = lang.format("death-by-player", deathByPlayer);
                    killerFound = true;
                }
            }

            combatLog.remove(victimUUID);
        }

        if (finalDeathMessage != null && !finalDeathMessage.isEmpty()) {
            Bukkit.broadcastMessage(finalDeathMessage);
        }

        waitingForKick.add(victimUUID);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.AMBIENCE_THUNDER, 1f, 1f);
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (waitingForKick.contains(victimUUID)) {
                    victim.kickPlayer(lang.getDeathKickMessage());
                }
            }
        }.runTaskLater(plugin, 100L);
    }
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        if (waitingForKick.contains(player.getUniqueId())) {
            waitingForKick.remove(player.getUniqueId());
            player.kickPlayer(lang.getDeathKickMessage());
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (waitingForKick.contains(event.getPlayer().getUniqueId())) {
            waitingForKick.remove(event.getPlayer().getUniqueId());
        }
    }
}