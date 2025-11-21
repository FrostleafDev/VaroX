package de.jozelot.varoX.listeners;

import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.files.ConfigManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener {

    private final VaroX plugin;
    private final ConfigManager config;

    public PingListener(VaroX plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        String motd = config.getMotd();

        event.setMotd(motd);
        int maxPlayerCount = 0;

        if (config.getPlayerCountMode().equalsIgnoreCase("dynamic")) {
            maxPlayerCount = event.getNumPlayers() + 1;
        } else if (config.getPlayerCountMode().equalsIgnoreCase("set")) {
            maxPlayerCount = config.getMaxPlayerCount();
        } else if (config.getPlayerCountMode().equalsIgnoreCase("server")) {
            maxPlayerCount = event.getMaxPlayers();
        }
        event.setMaxPlayers(maxPlayerCount);
    }
}