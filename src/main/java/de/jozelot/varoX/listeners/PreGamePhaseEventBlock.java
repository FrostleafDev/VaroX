package de.jozelot.varoX.listeners;

import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.files.ConfigManager;
import de.jozelot.varoX.manager.StatesManager;
import org.bukkit.GameMode;
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
import org.bukkit.event.weather.WeatherChangeEvent;

public class PreGamePhaseEventBlock implements Listener {

    private final VaroX plugin;
    private final ConfigManager config;
    private final StatesManager statesManager;

    public PreGamePhaseEventBlock(VaroX plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfigManager();
        this.statesManager = plugin.getStatesManager();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (statesManager.getGameState() == 1 && !(event.getPlayer().getGameMode().equals(GameMode.CREATIVE))) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (statesManager.getGameState() == 1 && !(event.getPlayer().getGameMode().equals(GameMode.CREATIVE))) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if ((statesManager.getGameState() == 1 || statesManager.getGameState() == 3) && !(player.getGameMode().equals(GameMode.CREATIVE))) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        if ((statesManager.getGameState() == 1 || statesManager.getGameState() == 3) && !(player.getGameMode().equals(GameMode.CREATIVE))) {
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
        if (statesManager.getGameState() == 1 && !(player.getGameMode().equals(GameMode.CREATIVE))) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {

        if (!(event.getTarget() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getTarget();
        if ((statesManager.getGameState() == 1 || statesManager.getGameState() == 3) && !(player.getGameMode().equals(GameMode.CREATIVE))) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (statesManager.getGameState() == 1) {
            event.setCancelled(true);
        }
    }
}
