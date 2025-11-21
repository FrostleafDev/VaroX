package de.jozelot.varoX.listeners;

import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.files.ConfigManager;
import de.jozelot.varoX.teams.Team;
import de.jozelot.varoX.teams.TeamsManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.List;
import java.util.Optional;

public class FriendlyFire implements Listener {

    private final VaroX plugin;
     private final TeamsManager teamsManager;
     private final ConfigManager configManager;

     public FriendlyFire(VaroX plugin) {
         this.plugin = plugin;
         this.teamsManager = plugin.getTeamsManager();
         this.configManager = plugin.getConfigManager();
     }

     @EventHandler
     public void onPlayerDamage(EntityDamageByEntityEvent event) {
         if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
             return;
         }

         Player victim = (Player) event.getEntity();
         Player damager = (Player) event.getDamager();

         boolean friendlyFireDisabled = configManager.isFriendlyFireDisabled();

         if (friendlyFireDisabled) {
             if (arePlayersInSameTeam(victim.getName(), damager.getName())) {

                 event.setCancelled(true);

                 //damager.sendMessage("§cFriendly Fire ist deaktiviert.");
                 return;
             }
         }
     }

    public boolean arePlayersInSameTeam(String player1Name, String player2Name) {
        List<Team> allTeams = teamsManager.loadTeamsFromFile();

        Optional<Team> team1Optional = allTeams.stream()
                .filter(team -> team.getMembers().stream()
                        .anyMatch(member -> member.equalsIgnoreCase(player1Name)))
                .findFirst();

        if (!team1Optional.isPresent()) {
            return false;
        }

        Team team1 = team1Optional.get();
        return team1.getMembers().stream()
                .anyMatch(member -> member.equalsIgnoreCase(player2Name));
    }
}
