package de.jozelot.varoX.listeners;

import de.jozelot.varoX.VaroX;
import de.jozelot.varoX.files.ConfigManager;
import de.jozelot.varoX.files.LangManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import java.util.List;

public class BannedItemsListener implements Listener {

    private final VaroX plugin;
    private final ConfigManager configManager;
    private final LangManager lang;
    private final String BAN_MESSAGE;

    public BannedItemsListener(VaroX plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
        this.lang = plugin.getLangManager();
        BAN_MESSAGE = this.lang.getBannedItemAlert();
    }

    private String getMaterialNameForConfig(Material material) {
        String name = material.name();

        if (name.equals("EYE_OF_ENDER")) {
            return "ENDER_EYE";
        } else if (name.equals("SKULL") || name.equals("SKULL_BLOCK")) {
            return "SKULL";
        }
        return name;
    }

    private boolean isPotionBanned(ItemStack itemStack) {
        if (itemStack.getType() != Material.POTION) {
            return false;
        }

        short potionData = itemStack.getDurability();

        List<String> bannedPotions = configManager.getBannedPotions();

        return bannedPotions.contains(String.valueOf(potionData));
    }

    private boolean isBanned(ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return false;
        }

        if (itemStack.getType() == Material.POTION) {
            return isPotionBanned(itemStack);
        }

        List<String> bannedItems = configManager.getBannedItems();

        String typeName = itemStack.getType().name();

        if (typeName.equals("EYE_OF_ENDER")) {
            typeName = "ENDER_EYE";
        } else if (typeName.equals("SKULL_ITEM") || typeName.equals("SKULL") || typeName.equals("SKULL_BLOCK")) {
            typeName = "SKULL";
        }

        if (bannedItems.contains(typeName)) {
            return true;
        }

        short subId = itemStack.getDurability();

        if (subId != 0) {
            String bannedStringWithData = typeName + ":" + subId;
            return bannedItems.contains(bannedStringWithData);
        }

        return false;
    }

    @EventHandler
    public void onCraft(CraftItemEvent event) {
        if (isBanned(event.getRecipe().getResult())) {
            event.setCancelled(true);
            Player p = (Player) event.getWhoClicked();
            p.sendMessage(BAN_MESSAGE);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (isBanned(event.getItemInHand())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(BAN_MESSAGE);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        ItemStack blockAsItem = new ItemStack(event.getBlock().getType(), 1, event.getBlock().getData());

        if (isBanned(blockAsItem)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(BAN_MESSAGE);
            return;
        }

        Material blockType = event.getBlock().getType();

        if (blockType.name().contains("SKULL")) {
            ItemStack skullItem = new ItemStack(Material.SKULL_ITEM, 1, event.getBlock().getData());
            if (isBanned(skullItem)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(BAN_MESSAGE);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {

        // Prüfe das Item in der Hand (Main Hand in 1.8.8)
        ItemStack item = event.getPlayer().getItemInHand();
        Player player = event.getPlayer();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }

        if (event.getAction().name().contains("RIGHT_CLICK")) {

            // Die isBanned-Methode prüft Tränke und alle anderen Items.
            if (isBanned(item)) {
                event.setCancelled(true);
                player.sendMessage(BAN_MESSAGE);
                return;
            }
        }
    }


    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent event) {
        ItemStack item = event.getItem().getItemStack();

        if (item != null && isBanned(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(BAN_MESSAGE);
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        for (int i = 0; i < 3; i++) {
            ItemStack result = event.getContents().getItem(i);

            if (result != null && result.getType() == Material.POTION) {
                if (isPotionBanned(result)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}