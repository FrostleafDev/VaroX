package de.jozelot.varoX.listeners;

import de.jozelot.varoX.commands.VaroCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class StartProtection implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)){
            return;
        }
        if (!VaroCommand.varoStartProtection) {
            return;
        }
        event.setCancelled(true);
    }
}
