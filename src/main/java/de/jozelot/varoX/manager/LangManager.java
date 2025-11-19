package de.jozelot.varoX.manager;

import jdk.nashorn.internal.objects.annotations.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Map;

public class LangManager {

    private final ConfigManager config;
    private final JavaPlugin plugin;

    private File langFile;
    private FileConfiguration langConfig;

    public LangManager(JavaPlugin plugin, ConfigManager config) {
        this.plugin = plugin;
        this.config = config;
    }

    private String locale;
    private String noPermission;
    private String onlyPlayer;

    private String commandVaroStartAlready;
    private String commandVaroStartPrephase;
    private String commandVaroStartRunning;
    private String commandVaroStartStopped;
    private String commandVaroStartSuccess;

    private String commandVaroEndAlready;
    private String commandVaroEndFail;
    private String commandVaroEndKick;

    private String commandVaroOpenAlreadyEnd;
    private String commandVaroOpenAlready;
    private String commandVaroOpen;

    private String commandVaroCloseEnded;
    private String commandVaroCloseClosed;
    private String commandVaroCloseStarted;
    private String commandVaroClose;

    private String commandVaroResetUser;
    private String commandVaroResetAdminFirst;
    private String commandVaroResetAdminSecond;

    private String commandVaroReloadSuccess;

    private String deathKickMessage;

    private String playerJoinFailClosed;
    private String playerJoinFailEnded;
    private String playerJoinFailNotInATeam;
    private String infoAdminStatePrephase;
    private String infoAdminStateOpen;

    private String loginFinish;
    private String varoStart;
    private String bannedItemAlert;

    private String commandVaroTeamListNoTeam;
    private String commandVaroTeamListSuccessFooter;
    private String commandVaroTeamListSuccessDeath;
    private String commandVaroTeamListSuccessAlive;

    private String commandVaroSpawnListNoSpawn;
    private String commandVaroSpawnListSuccessFooter;

    private String noSpawnAvailable;
    private String noTeamAvailable;
    private String notInATeam;
    private String notEnoughSpawns;

    public void load() {
        locale = config.getLocale();

        File localeFolder = new File(plugin.getDataFolder(), "locales");
        if (!localeFolder.exists()) localeFolder.mkdirs();

        langFile = new File(localeFolder, locale + ".yml");

        if (!langFile.exists()) {
            plugin.getLogger().warning("Language '" + locale + "' not found! Falling back to 'en'.");
            langFile = new File(localeFolder, "en.yml");
        } else {
            plugin.getLogger().info("Language loaded: '" + locale + "'");
        }
        langConfig = YamlConfiguration.loadConfiguration(langFile);

        loadData();

    }


    public String getBannedItemAlert() {
        return bannedItemAlert;
    }

    public String getCommandVaroTeamListNoTeam() {
        return commandVaroTeamListNoTeam;
    }

    public String getCommandVaroTeamListSuccessFooter() {
        return commandVaroTeamListSuccessFooter;
    }

    public String getCommandVaroTeamListSuccessDeath() {
        return commandVaroTeamListSuccessDeath;
    }

    public String getCommandVaroTeamListSuccessAlive() {
        return commandVaroTeamListSuccessAlive;
    }

    public String getCommandVaroSpawnListNoSpawn() {
        return commandVaroSpawnListNoSpawn;
    }

    public String getCommandVaroSpawnListSuccessFooter() {
        return commandVaroSpawnListSuccessFooter;
    }

    public String getPlayerJoinFailNotInATeam() {
        return playerJoinFailNotInATeam;
    }

    public String getNoSpawnAvailable() {
        return noSpawnAvailable;
    }

    public String getNoTeamAvailable() {
        return noTeamAvailable;
    }

    public String getNotInATeam() {
        return notInATeam;
    }

    public String getNotEnoughSpawns() {
        return notEnoughSpawns;
    }

    private void loadData() {
        noPermission = format("no-permission", null);
        onlyPlayer = format("only-player", null);

        commandVaroStartAlready = format("command-varo-start-already", null);
        commandVaroStartPrephase = format("command-varo-start-prephase", null);
        commandVaroStartRunning = format("command-varo-start-running", null);
        commandVaroStartStopped = format("command-varo-start-stopped", null);
        commandVaroStartSuccess = format("command-varo-start-success", null);

        commandVaroEndAlready = format("command-varo-end-already", null);
        commandVaroEndFail = format("command-varo-end-fail", null);
        commandVaroEndKick = format("command-varo-end-kick", null);

        commandVaroOpenAlreadyEnd = format("command-varo-open-already-end", null);
        commandVaroOpenAlready = format("command-varo-open-already", null);
        commandVaroOpen = format("command-varo-open", null);

        commandVaroCloseEnded = format("command-varo-close-ended", null);
        commandVaroCloseClosed = format("command-varo-close-closed", null);
        commandVaroCloseStarted = format("command-varo-close-started", null);
        commandVaroClose = format("command-varo-close", null);

        commandVaroResetUser = format("command-varo-reset-user", null);
        commandVaroResetAdminFirst = format("command-varo-reset-admin-firstline", null);
        commandVaroResetAdminSecond = format("command-varo-reset-admin-secondline", null);

        commandVaroReloadSuccess = format("command-varo-reload-success", null);

        deathKickMessage = format("death-kick-message", null);

        playerJoinFailClosed = format("player-join-fail-closed", null);
        playerJoinFailEnded = format("player-join-fail-ended", null);
        playerJoinFailNotInATeam = format("player-join-fail-not-in-a-team", null);
        infoAdminStatePrephase = format("info-admin-state-prephase", null);
        infoAdminStateOpen = format("info-admin-state-open", null);

        loginFinish = format("login-finish", null);
        varoStart = format("varo-start", null);
        bannedItemAlert = format("banned-item-alert", null);

        commandVaroTeamListNoTeam = format("command-varo-team-list-no-team", null);
        commandVaroTeamListSuccessFooter = format("command-varo-team-list-success-footer", null);
        commandVaroTeamListSuccessDeath = format("command-varo-team-list-success-death", null);
        commandVaroTeamListSuccessAlive = format("command-varo-team-list-success-alive", null);

        commandVaroSpawnListNoSpawn = format("command-varo-spawn-list-no-spawn", null);
        commandVaroSpawnListSuccessFooter = format("command-varo-spawn-list-success-footer", null);

        noSpawnAvailable = format("no-spawn-available", null);
        noTeamAvailable = format("no-team-available", null);
        notInATeam = format("not-in-team", null);
        notEnoughSpawns = format("not-enough-spawns", null);

    }

    public String get(String path) {
        return langConfig.getString(path, "§cERROR 404: String not found / Error in Lang file '" + locale + "'");
    }

    public String format(String path, Map<String, String> variables) {
        String msg = get(path);

        msg = msg.replace("{varo_name}", config.getVaroName());

        if (variables != null) {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                msg = msg.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        return msg;
    }

    public String getNoPermission() {
        return noPermission;
    }

    public String getOnlyPlayer() {
        return onlyPlayer;
    }

    public String getCommandVaroStartAlready() {
        return commandVaroStartAlready;
    }

    public String getCommandVaroStartPrephase() {
        return commandVaroStartPrephase;
    }

    public String getCommandVaroStartRunning() {
        return commandVaroStartRunning;
    }

    public String getCommandVaroStartStopped() {
        return commandVaroStartStopped;
    }

    public String getCommandVaroStartSuccess() {
        return commandVaroStartSuccess;
    }

    public String getCommandVaroEndAlready() {
        return commandVaroEndAlready;
    }

    public String getCommandVaroEndFail() {
        return commandVaroEndFail;
    }

    public String getCommandVaroEndKick() {
        return commandVaroEndKick;
    }

    public String getCommandVaroOpenAlreadyEnd() {
        return commandVaroOpenAlreadyEnd;
    }

    public String getCommandVaroOpenAlready() {
        return commandVaroOpenAlready;
    }

    public String getCommandVaroOpen() {
        return commandVaroOpen;
    }

    public String getCommandVaroCloseEnded() {
        return commandVaroCloseEnded;
    }

    public String getCommandVaroCloseClosed() {
        return commandVaroCloseClosed;
    }

    public String getCommandVaroCloseStarted() {
        return commandVaroCloseStarted;
    }

    public String getCommandVaroClose() {
        return commandVaroClose;
    }

    public String getCommandVaroResetUser() {
        return commandVaroResetUser;
    }

    public String getCommandVaroResetAdminFirst() {
        return commandVaroResetAdminFirst;
    }

    public String getCommandVaroResetAdminSecond() {
        return commandVaroResetAdminSecond;
    }

    public String getCommandVaroReloadSuccess() {
        return commandVaroReloadSuccess;
    }

    public String getVaroStart() {
        return varoStart;
    }

    public String getDeathKickMessage() {
        return deathKickMessage;
    }

    public String getLoginFinish() {
        return loginFinish;
    }

    public String getInfoAdminStateOpen() {
        return infoAdminStateOpen;
    }

    public String getInfoAdminStatePrephase() {
        return infoAdminStatePrephase;
    }

    public String getPlayerJoinFailEnded() {
        return playerJoinFailEnded;
    }

    public String getPlayerJoinFailClosed() {
        return playerJoinFailClosed;
    }
}
