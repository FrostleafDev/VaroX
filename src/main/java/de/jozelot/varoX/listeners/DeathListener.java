package de.jozelot.varoX.listeners;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.files.ConfigManager;
import de.jozelot.varoX.files.LangManager;
import de.jozelot.varoX.teams.TeamsManager;
import de.jozelot.varoX.user.User;
import de.jozelot.varoX.user.UserManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class DeathListener implements Listener {

    private final Map<UUID, CombatInfo> combatLog = new HashMap<>();
    private final VaroX plugin;
    private final Set<UUID> waitingForKick = new HashSet<>();
    private final LangManager lang;
    private final UserManager userManager;
    private final TeamsManager teamsManager;
    private final ConfigManager configManager;

    private final long TIMEOUT_SECONDS;

    public DeathListener(VaroX plugin) {
        this.plugin = plugin;
        this.lang = plugin.getLangManager();
        this.userManager = plugin.getUserManager();
        this.teamsManager = plugin.getTeamsManager();
        this.configManager = plugin.getConfigManager();
        TIMEOUT_SECONDS = configManager.getKilledByPlayerTime();
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

        boolean isVaroXElimination = false;
        boolean killedByPlayer = false;

        if (combatLog.containsKey(victimUUID)) {
            CombatInfo info = combatLog.get(victimUUID);
            long timeElapsed = System.currentTimeMillis() - info.getTimestamp();
            long timeoutMillis = TIMEOUT_SECONDS * 1000;

            if (timeElapsed <= timeoutMillis) {
                Player killer = Bukkit.getPlayer(info.getDamagerUUID());

                if (killer != null && killer.isOnline()) {
                    isVaroXElimination = true;
                    killedByPlayer = true;

                    Map<String, String> deathByPlayer = new HashMap<>();
                    deathByPlayer.put("player_name", String.valueOf(victim.getName()));
                    deathByPlayer.put("killer_name", String.valueOf(killer.getName()));

                    Optional<User> userOptional = userManager.getUserByName(killer.getName());
                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        user.addKill();
                        userManager.updateUser(user);
                    }

                    finalDeathMessage = lang.format("death-by-player", deathByPlayer);
                }
            }

            combatLog.remove(victimUUID);
        }

        if (!killedByPlayer && configManager.isPlayerDeathNonPlayer()) {
            isVaroXElimination = true;
        }

        if (isVaroXElimination) {
            Optional<User> userOptionalVictim = userManager.getUserByName(victim.getName());

            if (userOptionalVictim.isPresent()) {
                User userVictim = userOptionalVictim.get();
                userVictim.setAlive(false);
                userManager.updateUser(userVictim);
            }

            teamsManager.checkAndSetTeamStatus(victim.getName(), userManager);

            if (finalDeathMessage != null && !finalDeathMessage.isEmpty()) {
                Bukkit.broadcastMessage(finalDeathMessage);
            }

            // Schedule the kick
            waitingForKick.add(victimUUID);

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 1f);
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
    @EventHandler
    public void onAdvancement(PlayerAdvancementCriterionGrantEvent event) { // SCHAUEN OB RECIHT
        if (configManager.isAdvancementsEnbaled()) {
            return;
        }
        event.setCancelled(true);
    }
}