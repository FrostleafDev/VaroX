package de.jozelot.varoX;

import org.bukkit.plugin.java.JavaPlugin;
import de.jozelot.varox.listeners.JoinLeaveListener;

public final class VaroX extends JavaPlugin {

    @Override
    public void onEnable() {
        
        getLogger().info("Setting up Listener and Commands!");

        // Listener Initialize
        Bukkit.getPluginManager().registerEvents(new JoinLeaveListener(), this);

        // Commands Initialize
        // getCommand("test").setExecutor(new TestCommand());

        Bukkit.getConsoleSender().sendMessage("§7===== §6[ VaroX ] §7=====");
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§aPlugin version: §c" + getDescription().getVersion());
        Bukkit.getConsoleSender().sendMessage("§aMinecraft version: §c"+ Bukkit.getBukkitVersion());
        Bukkit.getConsoleSender().sendMessage("");
        Bukkit.getConsoleSender().sendMessage("§7===== §6[ VaroX ] §7=====");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
