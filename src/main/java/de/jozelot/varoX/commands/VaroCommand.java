package de.jozelot.varoX.commands;

import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class VaroCommand implements CommandExecutor {

    private final ConfigManager config;
    private final LangManager lang;
    private final FileManager fileManager;
    private final StatesManager statesManager;
    private final VaroX plugin;
    private final WorldBorderManager worldBorderManager;
    private final TeamsManager teamsManager;
    private final SpawnManager spawnManager;
    private final UserManager userManager;

    public VaroCommand(ConfigManager config, LangManager lang, FileManager fileManager, StatesManager statesManager, VaroX plugin, WorldBorderManager worldBorderManager, TeamsManager teamsManager, SpawnManager spawnManager, UserManager userManager) {
        this.config = config;
        this.lang = lang;
        this.fileManager = fileManager;
        this.statesManager = statesManager;
        this.plugin = plugin;
        this.worldBorderManager = worldBorderManager;
        this.teamsManager = teamsManager;
        this.spawnManager = spawnManager;
        this.userManager = userManager;
    }

    private boolean varoStarting = false;
    public static boolean varoStartProtection = false;

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
                plugin.getLogger().info("Varo started by " + sender.getName());
                varoStarting = true;
                if (config.isEnableWorldBorder()) { worldBorderManager.startWorldBorderSystem();}
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
                plugin.getLogger().info("Varo ended by " + sender.getName());
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
                plugin.getLogger().info("Varo opened by " + sender.getName());
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
                plugin.getLogger().info("Varo closed by " + sender.getName());
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
                plugin.getLogger().info("Varo reseted by " + sender.getName());
                userManager.deleteAllUsers();
                teamsManager.deleteAllTeams();
                spawnManager.deleteAllSpawns();
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
                plugin.getLogger().info("Varo plugin reloaded by " + sender.getName());
                return true;
            }
            if (args[0].equalsIgnoreCase("team")) {
                Map<String, String> vars = new HashMap<>();
                vars.put("command_usage", "/varo team <add|remove|addMember|removeMember|list>");
                sender.sendMessage(lang.format("usage", vars));
                return true;
            }
        }

        if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("team")) {
                if (args[1].equalsIgnoreCase("add")) {

                    if (args.length < 3) {
                        Map<String, String> vars = new HashMap<>();
                        vars.put("command_usage", "/varo team add <teamname> <playername1> [playername2...]");
                        sender.sendMessage(lang.format("usage", vars));
                        return true;
                    }

                    String teamName = args[2];
                    List<String> memberNames = new ArrayList<>();

                    List<String> notCheckedNames = new ArrayList<>();

                    for (int i = 3; i < args.length; i++) {
                        String playerName = args[i];

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

                        if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
                            memberNames.add(playerName);
                        } else {
                            memberNames.add(playerName);
                            notCheckedNames.add(playerName);
                        }
                    }

                    Team createdTeam = addTeam(teamName, memberNames);

                    if (createdTeam != null) {
                        Map<String, String> vars = new HashMap<>();
                        vars.put("team_name", teamName);
                        vars.put("team_size", String.valueOf(memberNames.size()));
                        sender.sendMessage(lang.format("command-varo-team-add-created", vars));

                        if (!notCheckedNames.isEmpty()) {
                            String membersPutTogether = String.join(", ", notCheckedNames);
                            Map<String, String> warning = new HashMap<>();
                            warning.put("player_names", membersPutTogether);
                            sender.sendMessage(lang.format("command-varo-team-add-warning", warning));
                        }
                    } else {
                        Map<String, String> exists = new HashMap<>();
                        exists.put("team_name", teamName);
                        sender.sendMessage(lang.format("command-varo-team-add-exists", exists));
                    }
                    return true;
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length < 3) {
                        Map<String, String> vars = new HashMap<>();
                        vars.put("command_usage", "/varo team remove <teamname>");
                        sender.sendMessage(lang.format("usage", vars));
                        return true;
                    }
                    removeTeam(args[2], sender);
                    return true;
                }
                if (args[1].equalsIgnoreCase("addMember")) {
                    if (args.length == 4) {
                        String teamName = args[2];
                        String memberName = args[3];

                        Optional<Team> teamOptional = teamsManager.getTeamByName(teamName);

                        if (teamOptional.isPresent()) {
                            Team team = teamOptional.get();

                            if (team.getMembers().stream().anyMatch(m -> m.equalsIgnoreCase(memberName))) {
                                Map<String, String> vars = new HashMap<>();
                                vars.put("team_name", teamName);
                                vars.put("player_name", memberName);
                                sender.sendMessage(lang.format("command-varo-team-addmember-already", vars));
                                return true;
                            }

                            team.addMember(memberName);
                            teamsManager.updateTeam(team);

                            Map<String, String> vars = new HashMap<>();
                            vars.put("team_name", teamName);
                            vars.put("player_name", memberName);
                            sender.sendMessage(lang.format("command-varo-team-addmember-success", vars));
                        } else {
                            Map<String, String> vars = new HashMap<>();
                            vars.put("team_name", teamName);
                            vars.put("player_name", memberName);
                            sender.sendMessage(lang.format("command-varo-team-addmember-not-found", vars));
                        }
                        return true;
                    } else {
                        Map<String, String> vars = new HashMap<>();
                        vars.put("command_usage", "/varo team addMember <teamname> <playername>");
                        sender.sendMessage(lang.format("usage", vars));
                        return true;
                    }
                }
                if (args[1].equalsIgnoreCase("removeMember")) {
                    if (args.length == 4) {
                        String teamName = args[2];
                        String memberName = args[3];

                        Optional<Team> teamOptional = teamsManager.getTeamByName(teamName);

                        if (teamOptional.isPresent()) {
                            Team team = teamOptional.get();

                            int initialSize = team.getMembers().size();
                            team.removeMember(memberName);
                            int newSize = team.getMembers().size();

                            if (newSize < initialSize) {
                                teamsManager.updateTeam(team);
                                Map<String, String> vars = new HashMap<>();
                                vars.put("team_name", teamName);
                                vars.put("player_name", memberName);
                                sender.sendMessage(lang.format("command-varo-team-removemember-success", vars));
                            } else {
                                Map<String, String> vars = new HashMap<>();
                                vars.put("team_name", teamName);
                                vars.put("player_name", memberName);
                                sender.sendMessage(lang.format("command-varo-team-removemember-not-a-member", vars));
                            }
                        } else {
                            Map<String, String> vars = new HashMap<>();
                            vars.put("team_name", teamName);
                            vars.put("player_name", memberName);
                            sender.sendMessage(lang.format("command-varo-team-removemember-not-found", vars));
                        }
                        return true;
                    } else {
                        Map<String, String> vars = new HashMap<>();
                        vars.put("command_usage", "/varo team removeMember <teamname> <playername>");
                        sender.sendMessage(lang.format("usage", vars));
                        return true;
                    }
                }
                if (args[1].equalsIgnoreCase("list")) {
                    if (args.length == 2) {

                        List<Team> teams = teamsManager.getAllTeams();

                        if (teams.isEmpty()) {
                            sender.sendMessage(lang.getCommandVaroTeamListNoTeam());
                            return true;
                        }

                        Map<String, String> vars = new HashMap<>();
                        vars.put("team_count", String.valueOf(teams.size()));
                        sender.sendMessage(lang.format("command-varo-team-list-success-header", vars));

                        for (Team team : teams) {
                            String memberCount = String.valueOf(team.getMembers().size());
                            String status = team.isAlive() ? lang.getCommandVaroTeamListSuccessAlive() : lang.getCommandVaroTeamListSuccessDeath();

                            sender.sendMessage("§7- ID " + team.getId() + ": §b" + team.getName() +
                                    " §7[" + memberCount + " Mitglieder, Kills: " + team.getKills() + ", Status: " + status + "§7]");
                            Map<String, String> teamRow = new HashMap<>();
                            teamRow.put("team_id", String.valueOf(team.getId()));
                            teamRow.put("team_name", String.valueOf(team.getName()));
                            teamRow.put("team_member_count", memberCount);
                            teamRow.put("team_kills", String.valueOf(team.getKills()));
                            teamRow.put("status", status);

                        }
                        sender.sendMessage(lang.getCommandVaroTeamListSuccessFooter());

                        return true;
                    } else {
                            Map<String, String> vars = new HashMap<>();
                            vars.put("command_usage", "/varo team list");
                            sender.sendMessage(lang.format("usage", vars));
                            return true;
                    }
                }
                Map<String, String> vars = new HashMap<>();
                vars.put("command_usage", "/varo team <add|remove|addMember|removeMember|list>");
                sender.sendMessage(lang.format("usage", vars));
                return true;

            } if (args[0].equalsIgnoreCase("spawn")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(lang.getOnlyPlayer());
                    return true;
                }
                Player player = (Player) sender;
                if (args[1].equalsIgnoreCase("add")) {
                    if (args.length < 3) {
                        Map<String, String> vars = new HashMap<>();
                        vars.put("command_usage", "/varo spawn add <id>");
                        sender.sendMessage(lang.format("usage", vars));
                        return true;
                    }

                    int spawnId = Integer.parseInt(args[2]);

                    if (spawnManager.getSpawnById(spawnId).isPresent()) {
                        Map<String, String> vars = new HashMap<>();
                        vars.put("spawn_id", String.valueOf(spawnId));
                        sender.sendMessage(lang.format("command-varo-spawn-add-exists", vars));
                        return true;
                    }

                    double x = player.getLocation().getX();
                    double y = player.getLocation().getY();
                    double z = player.getLocation().getZ();

                    double yaw = player.getLocation().getYaw();
                    double pitch = player.getLocation().getPitch();

                    double[] positionData = {x, y, z, yaw, pitch};

                    spawnManager.createSpawn(spawnId, positionData);

                    Map<String, String> vars = new HashMap<>();
                    vars.put("spawn_id", String.valueOf(spawnId));
                    sender.sendMessage(lang.format("command-varo-spawn-add-success", vars));

                    return true;
                }
                if (args[1].equalsIgnoreCase("remove")) {
                    if (args.length < 3) {
                        Map<String, String> vars = new HashMap<>();
                        vars.put("command_usage", "/varo spawn remove <id>");
                        sender.sendMessage(lang.format("usage", vars));
                        return true;
                    }

                    int spawnId = Integer.parseInt(args[2]);

                    if (spawnManager.deleteSpawnById(spawnId)){
                        Map<String, String> vars = new HashMap<>();
                        vars.put("spawn_id", String.valueOf(spawnId));
                        sender.sendMessage(lang.format("command-varo-spawn-remove-success", vars));
                        return true;
                    }
                    Map<String, String> vars = new HashMap<>();
                    vars.put("spawn_id", String.valueOf(spawnId));
                    sender.sendMessage(lang.format("command-varo-spawn-remove-not-found", vars));
                    return true;
                }
                if (args[1].equalsIgnoreCase("list")) {
                    List<Spawn> spawns = spawnManager.getAllSpawns();

                    if (spawns.isEmpty()) {
                        sender.sendMessage(lang.getCommandVaroSpawnListNoSpawn());
                        return true;
                    }

                    Map<String, String> vars = new HashMap<>();
                    vars.put("spawn_count", String.valueOf(spawns.size()));
                    sender.sendMessage(lang.format("command-varo-spawn-list-success-header", vars));

                    DecimalFormat df = new DecimalFormat("#.00");

                    for (Spawn spawn : spawns) {

                        double[] positionData = spawn.getPositionData();

                        Map<String, String> spawnsList = new HashMap<>();
                        spawnsList.put("spawn_id", String.valueOf(spawn.getId()));
                        spawnsList.put("spawn_x", String.valueOf(df.format(positionData[0])));
                        spawnsList.put("spawn_y", String.valueOf(df.format(positionData[1])));
                        spawnsList.put("spawn_z", String.valueOf(df.format(positionData[2])));
                        spawnsList.put("spawn_yaw", String.valueOf(df.format(positionData[3])));
                        spawnsList.put("spawn_pitch", String.valueOf(df.format(positionData[4])));
                        sender.sendMessage(lang.format("command-varo-spawn-list-success-team", spawnsList));
                    }
                    sender.sendMessage(lang.getCommandVaroSpawnListSuccessFooter());

                    return true;
                }
            }
        }
        sendHelpMessage(sender);
        return true;
    }

    private Team addTeam(String name, List<String> members) {
        Team team = teamsManager.createTeam(name, members);
        if (team != null) {
            return team;
        }
        return null;
    }
    private void removeTeam(String name, CommandSender sender) {
        Optional<Team> teamOptional = teamsManager.getTeamByName(name);

        if (teamOptional.isPresent()) {
            Team team = teamOptional.get();
            int teamId = team.getId();

            boolean successfullyRemoved = teamsManager.deleteTeamById(teamId);

            if (successfullyRemoved) {
                Map<String, String> vars = new HashMap<>();
                vars.put("team_name", name);
                vars.put("team_id", String.valueOf(teamId));
                sender.sendMessage(lang.format("command-varo-team-remove-success", vars));
            } else {
                Map<String, String> vars = new HashMap<>();
                vars.put("team_name", name);
                sender.sendMessage(lang.format("command-varo-team-remove-error", vars));
            }
        } else {
            Map<String, String> vars = new HashMap<>();
            vars.put("team_name", name);
            sender.sendMessage(lang.format("command-varo-team-remove-not-found", vars));
        }
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

        final UserManager finalUserManager = this.userManager;

        new BukkitRunnable() {
            private int timeLeft = startTime;

            @Override
            public void run() {
                if (timeLeft <= 0) {

                    for (Team team : teamsManager.getAllTeams()) {

                        List<String> membersToRemove = new ArrayList<>();

                        for (String memberName : team.getMembers()) {
                            if (Bukkit.getPlayerExact(memberName) == null) {

                                membersToRemove.add(memberName);

                                finalUserManager.deleteUserByName(memberName);

                                // plugin.getLogger().info(memberName + " wurde vom Varo-Start ausgeschlossen (nicht online).");
                            }
                        }

                        if (!membersToRemove.isEmpty()) {
                            for (String member : membersToRemove) {
                                team.removeMember(member);
                            }
                            teamsManager.updateTeam(team);
                        }
                        if (team.getMembers().isEmpty()) {
                            team.setAlive(false);
                            //plugin.getLogger().info("Team " + team.getName() + " ist leer und wurde auf TOT gesetzt.");
                        }
                        if (team.getMembers().isEmpty()) {
                            team.setAlive(false);
                            plugin.getLogger().info("Team " + team.getName() + " ist leer und wurde auf TOT gesetzt.");
                        }
                    }

                    Bukkit.broadcastMessage(lang.getVaroStart());

                    for (Player player : Bukkit.getOnlinePlayers()) {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
                    }

                    varoStarting = false;
                    if (config.isEnableStartProtection()) { startProtectionTimer(); }
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

    private void startProtectionTimer() {

        varoStartProtection = true;

        new BukkitRunnable() {
            private int timeLeft = config.getStartProtectionTime();

            @Override
            public void run() {
                if (timeLeft <= 0) {
                    varoStartProtection = false;
                }
                timeLeft--;
            }
        }.runTaskTimer(plugin, 0L, 20L);
    }
}
