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
import org.bukkit.block.data.Directional; // Neue API
import org.bukkit.block.BlockFace; // Hinzugefügt
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
// import org.bukkit.material.Attachable; // Veraltet

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

        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        if (block.getType() == Material.CHEST || block.getType() == Material.TRAPPED_CHEST || block.getState() instanceof org.bukkit.block.Sign) {

            Location targetLoc = block.getLocation();

            if (block.getState() instanceof org.bukkit.block.Sign) {
                org.bukkit.block.data.BlockData data = block.getState().getBlockData();
                if (data instanceof Directional) {
                    BlockFace attachedFace = ((Directional) data).getFacing().getOppositeFace();
                    targetLoc = block.getRelative(attachedFace).getLocation();
                } else {
                    return;
                }
            }

            Location normalizedTargetLoc = chestManager.normalizeChestLocation(targetLoc);
            TeamChest chest = chestManager.getChestAt(normalizedTargetLoc);

            if (chest == null) {
                return;
            }

            Optional<Team> teamOpt = teamsManager.getTeamByName(chest.getTeamNameKey());

            if (!teamOpt.isPresent()) {
                return;
            }

            Team chestTeam = teamOpt.get();

            if (!chestTeam.isAlive()) {
                return;
            }

            Optional<Team> playerTeamOpt = teamsManager.getTeamOfPlayer(player.getName());


            if (!playerTeamOpt.isPresent() || !playerTeamOpt.get().getName().equalsIgnoreCase(chest.getTeamNameKey())) {
                event.setCancelled(true);
                Map<String, String> vars = new HashMap<>();
                vars.put("team_name", chest.getTeamName());
                player.sendMessage(lang.format("teamchest-access-denied", vars));
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

            org.bukkit.block.data.BlockData data = block.getState().getBlockData();
            if (data instanceof Directional) {
                BlockFace attachedFace = ((Directional) data).getFacing().getOppositeFace();
                chestLoc = block.getRelative(attachedFace).getLocation();
            } else {
                return;
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
            Block signBlock = chest.getSignBlock();
            if (signBlock != null) {
                signBlock.setType(Material.AIR);
            }
            chestManager.removeChest(chest.getChestLocation());
            player.sendMessage(lang.format("teamchest-removed", null));

        } else if (block.getState() instanceof org.bukkit.block.Sign) {
            chestManager.removeChest(chest.getChestLocation());
            player.sendMessage(lang.format("teamchest-removed", null));
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
                org.bukkit.block.data.BlockData data = block.getState().getBlockData();
                if (data instanceof Directional) {
                    BlockFace attachedFace = ((Directional) data).getFacing().getOppositeFace();
                    Location chestLoc = block.getRelative(attachedFace).getLocation();
                    if (isTeamChest(chestLoc)) {
                        it.remove();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignEdit(PlayerInteractEvent event) {
        if (!config.isTeamChestsEnabled()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        Block block = event.getClickedBlock();

        if (!(block.getState() instanceof org.bukkit.block.Sign)) {
            return;
        }

        Location chestLoc = null;
        org.bukkit.block.data.BlockData data = block.getState().getBlockData();

        if (data instanceof Directional) {
            BlockFace attachedFace = ((Directional) data).getFacing().getOppositeFace();
            chestLoc = block.getRelative(attachedFace).getLocation();
        } else {
            return;
        }

        if (chestLoc == null) return;

        Location normalizedChestLoc = chestManager.normalizeChestLocation(chestLoc);
        TeamChest chest = chestManager.getChestAt(normalizedChestLoc);

        if (chest != null) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onSignColor(PlayerInteractEvent event) {
        if (!config.isTeamChestsEnabled()) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        Material heldItemType = item.getType();

        if (!isSignDyeOrInk(heldItemType)) {
            return;
        }

        Block block = event.getClickedBlock();

        if (!(block.getState() instanceof org.bukkit.block.Sign)) {
            return;
        }

        Location chestLoc = null;
        org.bukkit.block.data.BlockData data = block.getState().getBlockData();

        if (data instanceof Directional) {
            BlockFace attachedFace = ((Directional) data).getFacing().getOppositeFace();
            chestLoc = block.getRelative(attachedFace).getLocation();
        } else {
            return;
        }

        if (chestLoc == null) return;

        Location normalizedChestLoc = chestManager.normalizeChestLocation(chestLoc);
        TeamChest chest = chestManager.getChestAt(normalizedChestLoc);

        if (chest != null) {
            event.setCancelled(true);
        }
    }

    private boolean isSignDyeOrInk(Material material) {
        String name = material.name();

        if (name.endsWith("_DYE")) return true;

        // Ink Sacs
        if (name.equals("INK_SAC")) return true;
        if (name.equals("GLOW_INK_SAC")) return true;

        if (name.contains("DYE")) return true;

        return false;
    }
}