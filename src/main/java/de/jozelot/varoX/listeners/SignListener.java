package de.jozelot.varoX.listeners;

import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.files.ConfigManager;
import de.jozelot.varoX.files.LangManager;
import de.jozelot.varoX.teamchests.TeamChest;
import de.jozelot.varoX.teamchests.TeamChestManager;
import de.jozelot.varoX.teams.TeamsManager;
import de.jozelot.varoX.teams.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Directional; // Neue API
import org.bukkit.block.BlockFace; // Hinzugefügt
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
// import org.bukkit.material.Attachable; // Veraltet

import java.util.Optional;

public class SignListener implements Listener {

    private final VaroX plugin;
    private final TeamChestManager chestManager;
    private final TeamsManager teamsManager;
    private final ConfigManager config;
    private final LangManager lang;

    private static final String TEAM_CHEST_TAG = "[TEAMCHEST]";

    public SignListener(VaroX plugin) {
        this.plugin = plugin;
        this.chestManager = plugin.getTeamChestManager();
        this.teamsManager = plugin.getTeamsManager();
        this.config = plugin.getConfigManager();
        this.lang = plugin.getLangManager();
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (!config.isTeamChestsEnabled()) return;

        Player player = event.getPlayer();
        String line0 = event.getLine(0);

        String cleanedLine0 = ChatColor.stripColor(line0).toUpperCase().trim();
        System.out.println("Cleaned Line 0: " + cleanedLine0);
        if (!cleanedLine0.equals("[TEAM]") && !cleanedLine0.equals(TEAM_CHEST_TAG)) {
            return;
        }

        Optional<Team> playerTeamOpt = teamsManager.getTeamOfPlayer(player.getName());
        if (!playerTeamOpt.isPresent()) {
            player.sendMessage(lang.format("teamchest-fail-not-in-team", null));
            event.setCancelled(true);
            return;
        }
        Team team = playerTeamOpt.get();

        Block attachedBlock = null;

        org.bukkit.block.data.BlockData data = event.getBlock().getState().getBlockData();
        if (data instanceof Directional) {
            Directional directional = (Directional) data;
            BlockFace attachedFace = directional.getFacing().getOppositeFace();

            if (attachedFace != BlockFace.DOWN && attachedFace != BlockFace.UP) {
                attachedBlock = event.getBlock().getRelative(attachedFace);
            }
        }
        if (attachedBlock == null) {
            player.sendMessage(lang.format("teamchest-fail-attach-error", null));
            event.setCancelled(true);
            return;
        }

        Material attachedType = attachedBlock.getType();
        if (attachedType != Material.CHEST && attachedType != Material.TRAPPED_CHEST) {
            player.sendMessage(lang.format("teamchest-fail-no-chest", null));
            event.setCancelled(true);
            return;
        }

        Location normalizedChestLoc = chestManager.normalizeChestLocation(attachedBlock.getLocation());

        if (chestManager.getChestAt(normalizedChestLoc) != null) {
            player.sendMessage(lang.format("teamchest-fail-already-registered", null));
            event.setCancelled(true);
            return;
        }

        TeamChest newChest = new TeamChest(team.getName(), normalizedChestLoc, event.getBlock().getLocation());
        chestManager.registerChest(newChest);

        event.setLine(0, ChatColor.DARK_BLUE + TEAM_CHEST_TAG);
        event.setLine(1, ChatColor.BLACK + "von");
        event.setLine(2, ChatColor.BLACK + team.getName());
        event.setLine(3, "");

        player.sendMessage(lang.format("teamchest-success", null));
    }
}