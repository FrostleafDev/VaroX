package de.jozelot.varoX.listeners;

import de.jozelot.varoX.manager.ConfigManager;
import de.jozelot.varoX.manager.LangManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class PingListener implements Listener {

    private final ConfigManager config;

    public PingListener(ConfigManager config) {
        this.config = config;
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