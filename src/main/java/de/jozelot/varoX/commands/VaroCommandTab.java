package de.jozelot.varoX.commands;

import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.spawns.Spawn;
import de.jozelot.varoX.spawns.SpawnManager;
import de.jozelot.varoX.teams.Team;
import de.jozelot.varoX.teams.TeamsManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VaroCommandTab implements TabCompleter {

    private final TeamsManager teamsManager;
    private final SpawnManager spawnManager;

    public VaroCommandTab(VaroX plugin) {
        this.teamsManager = plugin.getTeamsManager();
        this.spawnManager = plugin.getSpawnManager();
    }

    private static final List<String> MAIN_COMMANDS = Arrays.asList(
            "start", "end", "open", "close", "reset", "reload", "team", "spawn", "help"
    );

    private static final List<String> TEAM_SUB_COMMANDS = Arrays.asList(
            "add", "remove", "addMember", "removeMember", "list"
    );

    private static final List<String> SPAWN_SUB_COMMANDS = Arrays.asList(
            "add", "remove", "list"
    );

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NotNull [] args) {

        if (!sender.hasPermission("varox.admin")) { return List.of(); }

        List<String> completions = new ArrayList<>();
        String currentArg = args[args.length - 1].toLowerCase();

        if (args.length == 1) {
            completions.addAll(MAIN_COMMANDS.stream()
                    .filter(s -> s.startsWith(currentArg))
                    .collect(Collectors.toList()));
        } else if (args.length >= 2) {
            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("team")) {
                if (args.length == 2) {
                    completions.addAll(TEAM_SUB_COMMANDS.stream()
                            .filter(s -> s.startsWith(currentArg))
                            .collect(Collectors.toList()));
                } else if (args.length >= 3) {
                    String teamSubCommand = args[1].toLowerCase();

                    if (teamSubCommand.equals("list")) {
                        return List.of();
                    }

                    if (teamSubCommand.equals("remove") || teamSubCommand.equals("addmember") || teamSubCommand.equals("removemember")) {
                        if (args.length == 3) {
                            completions.addAll(teamsManager.getAllTeams().stream()
                                    .map(Team::getName)
                                    .filter(s -> s.toLowerCase().startsWith(currentArg))
                                    .collect(Collectors.toList()));
                        }
                    }

                    if (teamSubCommand.equals("add")) {
                        if (args.length == 3) {
                            completions.add("<teamname>");
                        } else if (args.length >= 4) {
                            completions.addAll(getOnlinePlayers(currentArg));
                        }
                    } else if (teamSubCommand.equals("addmember")) {
                        if (args.length == 3) {

                        } else if (args.length == 4) {
                            completions.addAll(getOnlinePlayers(currentArg));
                        }
                    } else if (teamSubCommand.equals("removemember")) {
                        if (args.length == 3) {

                        } else if (args.length == 4) {
                            Optional<Team> teamOpt = teamsManager.getTeamByName(args[2]);
                            if (teamOpt.isPresent()) {
                                completions.addAll(teamOpt.get().getMembers().stream()
                                        .filter(s -> s.toLowerCase().startsWith(currentArg))
                                        .collect(Collectors.toList()));
                            } else {
                                completions.addAll(getOnlinePlayers(currentArg));
                            }
                        }
                    }
                }
            } else if (subCommand.equals("spawn")) {
                if (args.length == 2) {
                    completions.addAll(SPAWN_SUB_COMMANDS.stream()
                            .filter(s -> s.startsWith(currentArg))
                            .collect(Collectors.toList()));
                } else if (args.length == 3) {
                    String spawnSubCommand = args[1].toLowerCase();
                    if (spawnSubCommand.equals("add")) {
                        int nextId = spawnManager.getAllSpawns().stream().mapToInt(Spawn::getId).max().orElse(0) + 1;
                        completions.add(String.valueOf(nextId));
                    } else if (spawnSubCommand.equals("remove")) {
                        completions.addAll(spawnManager.getAllSpawns().stream()
                                .map(Spawn::getId)
                                .map(String::valueOf)
                                .filter(s -> s.startsWith(currentArg))
                                .collect(Collectors.toList()));
                    } else if (spawnSubCommand.equals("list")) {
                        return List.of();
                    }
                }
            } else {
                return List.of();
            }
        }

        if (!completions.isEmpty() && args.length > 1) {
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(currentArg))
                    .collect(Collectors.toList());
        }

        return completions;
    }

    private List<String> getOnlinePlayers(String filter) {
        return Bukkit.getOnlinePlayers().stream()
                .map(Player::getName)
                .filter(s -> s.toLowerCase().startsWith(filter))
                .collect(Collectors.toList());
    }
}