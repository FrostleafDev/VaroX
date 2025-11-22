package de.jozelot.varoX.files;

import de.jozelot.varoX.VaroX;

import java.util.List;

public class ConfigManager {

    private final VaroX plugin;

    public ConfigManager(VaroX plugin) {
        this.plugin = plugin;
    }

    private String locale;
    private String varoName;

    private double worldboarderCenterX;
    private double worldboarderCenterZ;
    private int startSize;
    private boolean enableWorldBorder;
    private int startProtectionTime;
    private boolean enableStartProtection;

    private List<String> bannedItems;
    private List<String> bannedPotions;

    private String motd;
    private String playerCountMode;
    private int maxPlayerCount;

    private boolean isFriendlyFireDisabled;

    private boolean isPlayerDeathNonPlayer;
    private boolean isAdvancementsEnbaled;
    private boolean isTeamChestsEnabled;

    private boolean isTabEnabled;
    private int killedByPlayerTime;

    private List<String> tabHeader;
    private List<String> tabFooter;

    public double getWorldboarderCenterX() {
        return worldboarderCenterX;
    }

    public double getWorldboarderCenterZ() {
        return worldboarderCenterZ;
    }

    public int getStartSize() {
        return startSize;
    }

    public boolean isEnableWorldBorder() {
        return enableWorldBorder;
    }

    public int getStartProtectionTime() {
        return startProtectionTime;
    }

    public boolean isEnableStartProtection() {
        return enableStartProtection;
    }

    public List<String> getBannedItems() {
        return bannedItems;
    }

    public List<String> getBannedPotions() {
        return bannedPotions;
    }

    public String getMotd() {
        return motd;
    }

    public String getPlayerCountMode() {
        return playerCountMode;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public boolean isPlayerDeathNonPlayer() {
        return isPlayerDeathNonPlayer;
    }

    public boolean isFriendlyFireDisabled() {
        return isFriendlyFireDisabled;
    }

    public boolean isAdvancementsEnbaled() {
        return isAdvancementsEnbaled;
    }

    public boolean isTeamChestsEnabled() {
        return isTeamChestsEnabled;
    }

    public boolean isTabEnabled() {
        return isTabEnabled;
    }

    public List<String> getTabHeader() {
        return tabHeader;
    }

    public List<String> getTabFooter() {
        return tabFooter;
    }

    public int getKilledByPlayerTime() {
        return killedByPlayerTime;
    }

    public void load() {
        plugin.saveDefaultConfig();

        plugin.reloadConfig();

        locale = plugin.getConfig().getString("locale");
        varoName = plugin.getConfig().getString("varo-name");
        worldboarderCenterX = plugin.getConfig().getDouble("center-x");
        worldboarderCenterZ = plugin.getConfig().getDouble("center-z");
        startSize = plugin.getConfig().getInt("start-worldboarder-size");
        enableWorldBorder = plugin.getConfig().getBoolean("enable-worldboarder");
        startProtectionTime = plugin.getConfig().getInt("start-protection-time");
        enableStartProtection = plugin.getConfig().getBoolean("enable-start-protection");

        bannedItems = plugin.getConfig().getStringList("banned-items");
        bannedPotions = plugin.getConfig().getStringList("banned-potions");

        motd = plugin.getConfig().getString("server-motd");
        playerCountMode = plugin.getConfig().getString("max-player-count-mode");
        maxPlayerCount = plugin.getConfig().getInt("max-player-count-set");
        isPlayerDeathNonPlayer = plugin.getConfig().getBoolean("player-out-by-non-player-death");

        isFriendlyFireDisabled = plugin.getConfig().getBoolean("disable-friendly-fire");
        isAdvancementsEnbaled = plugin.getConfig().getBoolean("enable-advancements");
        isTeamChestsEnabled = plugin.getConfig().getBoolean("enable-team-chests");

        isTabEnabled = plugin.getConfig().getBoolean("server-tablist-enabled");
        tabHeader = plugin.getConfig().getStringList("server-tablist-header");
        tabFooter = plugin.getConfig().getStringList("server-tablist-footer");

        killedByPlayerTime = plugin.getConfig().getInt("killed-by-player-time");
    }

    public String getLocale() {
        return locale;
    }

    public String getVaroName() {
        return varoName;
    }
}
