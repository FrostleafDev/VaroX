package de.jozelot.varoX.listeners;

import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.files.ConfigManager;
import de.jozelot.varoX.files.LangManager;
import de.jozelot.varoX.teamchests.TeamChest;
import de.jozelot.varoX.teamchests.TeamChestManager;
import de.jozelot.varoX.teams.TeamsManager;
import de.jozelot.varoX.teams.Team;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Attachable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

public class ProtectionListener implements Listener {

    private final VaroX plugin;
    private final TeamChestManager chestManager;
    private final TeamsManager teamsManager;
    private final ConfigManager config;
    private final LangManager lang;

    public ProtectionListener(VaroX plugin) {
        this.plugin = plugin;
        this.chestManager = plugin.getTeamChestManager();
        this.teamsManager = plugin.getTeamsManager();
        this.config = plugin.getConfigManager();
        this.lang = plugin.getLangManager();
    }

    private boolean isTeamChest(Location loc) {
        Location normalizedLoc = chestManager.normalizeChestLocation(loc);
        return chestManager.getChestAt(normalizedLoc) != null;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!config.isTeamChestsEnabled()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Block block = event.getClickedBlock();
        Player player = event.getPlayer();

        plugin.getLogger().info("--- Interact Test ---");
        plugin.getLogger().info("Clicked Block: " + block.getType());

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST || block.getState() instanceof org.bukkit.block.Sign) {

            Location targetLoc = block.getLocation();

            if (block.getState() instanceof org.bukkit.block.Sign) {
                try {
                    targetLoc = block.getRelative(((Attachable) block.getState().getData()).getAttachedFace()).getLocation();
                } catch (ClassCastException ignored) {
                    return;
                }
            }

            plugin.getLogger().info("1. Target Location (clicked/attached): " + targetLoc.toVector());
            Location normalizedTargetLoc = chestManager.normalizeChestLocation(targetLoc);
            plugin.getLogger().info("2. Normalized Location: " + normalizedTargetLoc.toVector());

            TeamChest chest = chestManager.getChestAt(normalizedTargetLoc);

            if (chest == null) {
                plugin.getLogger().info("3. TeamChest NICHT gefunden (kein Schutz aktiv).");
                plugin.getLogger().info("---------------------------------------------");
                return;
            }

            plugin.getLogger().info("3. TeamChest GEVUNDEN (Team: " + chest.getTeamName() + ")");

            Optional<Team> teamOpt = teamsManager.getTeamByName(chest.getTeamNameKey());

            if (!teamOpt.isPresent()) {
                plugin.getLogger().warning("Team der Truhe (" + chest.getTeamNameKey() + ") existiert nicht mehr. Zugriff erlaubt.");
                plugin.getLogger().info("---------------------------------------------");
                return;
            }

            Team chestTeam = teamOpt.get();

            if (!chestTeam.isAlive()) {
                plugin.getLogger().info("4. Team ist ausgeschieden. Zugriff für alle erlaubt.");
                plugin.getLogger().info("---------------------------------------------");
                return;
            }

            Optional<Team> playerTeamOpt = teamsManager.getTeamOfPlayer(player.getName());


            if (!playerTeamOpt.isPresent() || !playerTeamOpt.get().getName().equalsIgnoreCase(chest.getTeamNameKey())) {
                event.setCancelled(true);

                plugin.getLogger().warning("6. Zugriff VERWEIGERT. Event gecancelled.");
                plugin.getLogger().info("---------------------------------------------");
                Map<String, String> vars = new HashMap<>();
                vars.put("team_name", chest.getTeamName());
                player.sendMessage(lang.format("teamchest-access-denied", vars));
            }else {
                plugin.getLogger().info("6. Zugriff ERLAUBT (Spieler im Team).");
                plugin.getLogger().info("---------------------------------------------");
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!config.isTeamChestsEnabled()) return;

        Block block = event.getBlock();
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        TeamChest chest = null;
        Location chestLoc = null;

        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
            chestLoc = block.getLocation();
        } else if (block.getState() instanceof org.bukkit.block.Sign) {

            try {
                chestLoc = block.getRelative(((Attachable) block.getState().getData()).getAttachedFace()).getLocation();
            } catch (ClassCastException ignored) {
            }
        }

        if (chestLoc == null) return;

        Location normalizedChestLoc = chestManager.normalizeChestLocation(chestLoc);
        chest = chestManager.getChestAt(normalizedChestLoc);

        if (chest == null) return;


        Optional<Team> playerTeamOpt = teamsManager.getTeamOfPlayer(player.getName());

        if (!playerTeamOpt.isPresent() || !playerTeamOpt.get().getName().equalsIgnoreCase(chest.getTeamNameKey())) {
            event.setCancelled(true);
            player.sendMessage(lang.format("teamchest-break-denied", null));
            return;
        }

        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST) {
            chest.getSignBlock().setType(Material.AIR);
            chestManager.removeChest(chest.getChestLocation());
            player.sendMessage(lang.format("teamchest-removed", null));

        } else if (block.getState() instanceof org.bukkit.block.Sign) {
            chestManager.removeChest(chest.getChestLocation());

        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        if (!config.isTeamChestsEnabled()) return;

        Iterator<Block> it = event.blockList().iterator();
        while (it.hasNext()) {
            Block block = it.next();

            if (isTeamChest(block.getLocation())) {
                it.remove();
            }

            else if (block.getState() instanceof org.bukkit.block.Sign) {
                try {
                    Location chestLoc = block.getRelative(((Attachable) block.getState().getData()).getAttachedFace()).getLocation();
                    if (isTeamChest(chestLoc)) {
                        it.remove();
                    }
                } catch (ClassCastException ignored) {
                }
            }
        }
    }
}