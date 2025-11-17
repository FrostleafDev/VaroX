package de.jozelot.varoX;

import org.bukkit.plugin.java.JavaPlugin;
import de.jozelot.varox.listeners.JoinLeaveListener;

public final class VaroX extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("Plugin activated!");
        getServer().getPluginManager().getEventManager().register(new JoinLeaveListener());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
