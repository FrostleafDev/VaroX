package de.jozelot.varoX.listeners;

import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.files.ConfigManager;
import de.jozelot.varoX.files.LangManager;
import de.jozelot.varoX.manager.*;
import de.jozelot.varoX.spawns.Spawn;
import de.jozelot.varoX.spawns.SpawnManager;
import de.jozelot.varoX.teams.Team;
import de.jozelot.varoX.teams.TeamsManager;
import de.jozelot.varoX.user.User;
import de.jozelot.varoX.user.UserManager;
import de.jozelot.varoX.utils.TabListUtil;
import org.bukkit.*;
import org.bukkit.entity.Entity;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.DoubleConsumer;

public class JoinLeaveListener implements Listener {

    private final StatesManager statesManager;
    private final LangManager lang;
    private final VaroX plugin;
    private final TeamsManager teamsManager;
    private final SpawnManager spawnManager;
    private final UserManager userManager;
    private final ConfigManager config;

    private List<Player> playerInJoin = new ArrayList<>();

    public JoinLeaveListener(VaroX plugin) {
        this.plugin = plugin;
        this.statesManager = plugin.getStatesManager();
        this.lang = plugin.getLangManager();
        this.teamsManager = plugin.getTeamsManager();
        this.spawnManager = plugin.getSpawnManager();
        this.userManager = plugin.getUserManager();
        this.config = plugin.getConfigManager();
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
        if (statesManager.getGameState() == 1) {
            assignSpawnToPlayer(event.getPlayer());
        }

        userManager.registerUser(event.getPlayer().getName());
        if (config.isTabEnabled()) {
            TabListUtil.setHeaderFooter(event.getPlayer(), config.getTabHeader(), config.getTabFooter());
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();
        String playerName = player.getName();

        if (player.hasPermission("varox.admin")) {
            return;
        }

        if (statesManager.getGameState() == 0) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, lang.getPlayerJoinFailClosed());
            return;
        } else if (statesManager.getGameState() == 3) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, lang.getPlayerJoinFailEnded());
            return;
        }

        boolean playerFoundInTeam = false;

        for (Team team : teamsManager.getAllTeams()) {
            for (String member : team.getMembers()) {
                if (member.equalsIgnoreCase(playerName)) {

                    playerFoundInTeam = true;

                    Optional<User> userOptional = userManager.getUserByName(playerName);

                    if (userOptional.isPresent()) {
                        User user = userOptional.get();
                        if (user.isAlive()) {
                            return;
                        }
                        event.disallow(PlayerLoginEvent.Result.KICK_OTHER, lang.getDeathKickMessage());
                        return;
                    }
                    return;
                }
            }
        }
        if (!playerFoundInTeam) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, lang.getPlayerJoinFailNotInATeam());
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

        Optional<User> userOptional = userManager.getUserByName(event.getPlayer().getName());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.addDisconnect();
            userManager.updateUser(user);
        }
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

    private void assignSpawnToPlayer(Player player) {
        List<Spawn> allSpawns = new ArrayList<>(spawnManager.getAllSpawns());
        allSpawns.sort(Comparator.comparingInt(Spawn::getId));

        if (allSpawns.isEmpty()) {
            player.sendMessage(lang.getNoSpawnAvailable());
            return;
        }

        List<Team> allTeams = teamsManager.getAllTeams();

        if (allTeams.isEmpty()) {
            player.sendMessage(lang.getNoTeamAvailable());
            return;
        }

        int maxTeamSize = allTeams.stream()
                .mapToInt(team -> team.getMembers().size())
                .max()
                .orElse(0);

        List<String> playerRotation = new ArrayList<>();

        for (int i = 0; i < maxTeamSize; i++) {
            for (Team team : allTeams) {
                List<String> members = team.getMembers();
                if (i < members.size()) {
                    playerRotation.add(members.get(i));
                }
            }
        }

        String playerName = player.getName();
        int spawnIndex = playerRotation.indexOf(playerName);

        if (spawnIndex == -1) {
            player.sendMessage(lang.getNotInATeam());
            player.setGameMode(GameMode.SPECTATOR);
            return;
        }
        player.setGameMode(GameMode.SURVIVAL);
        if (spawnIndex < allSpawns.size()) {
            Spawn assignedSpawn = allSpawns.get(spawnIndex);

            World world = player.getWorld();
            Location spawnLocation = assignedSpawn.toLocation(world);
            player.teleport(spawnLocation);


            plugin.getLogger().info("Spieler: " + player.getName() + " kriegt Spawn Nummer " + assignedSpawn.getId());
        } else {
            player.sendMessage(lang.getNotEnoughSpawns());
        }
    }
}
