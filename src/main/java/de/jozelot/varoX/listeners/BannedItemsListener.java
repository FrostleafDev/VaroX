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
import org.bukkit.event.entity.EntityPickupItemEvent; // Ersetzt PlayerPickupItemEvent
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.Nullable;

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
    private boolean isPotionBanned(@Nullable ItemStack itemStack) {
        if (itemStack == null || !itemStack.getType().name().contains("POTION")) {
            return false;
        }

        if (itemStack.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            PotionData potionData = potionMeta.getBasePotionData();
            PotionType type = potionData.getType();

            String bannedString = String.format("%s:%s:%s",
                    type.name(),
                    potionData.isUpgraded(),
                    potionData.isExtended());

            List<String> bannedPotions = configManager.getBannedPotions();

            return bannedPotions.contains(bannedString);
        }

        return false;
    }

    private boolean isBanned(@Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            return false;
        }

        if (itemStack.getType().name().contains("POTION")) {
            return isPotionBanned(itemStack);
        }

        List<String> bannedItems = configManager.getBannedItems();

        String typeName = itemStack.getType().name();

        return bannedItems.contains(typeName);
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
        ItemStack item = event.getItemInHand();
        if (isBanned(item)) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(BAN_MESSAGE);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Material blockType = event.getBlock().getType();

        if (isBanned(new ItemStack(blockType))) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(BAN_MESSAGE);
            return;
        }

        if (blockType.name().contains("SKULL")) {
            if (isBanned(new ItemStack(blockType))) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(BAN_MESSAGE);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (item == null || item.getType() == Material.AIR) {
            return;
        }
        if (isBanned(item)) {
            event.setCancelled(true);
            player.sendMessage(BAN_MESSAGE);
        }
    }


    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        // EntityPickupItemEvent für moderne Spigot-Versionen
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getEntity();

        ItemStack item = event.getItem().getItemStack();

        if (item != null && isBanned(item)) {
            event.setCancelled(true);
            player.sendMessage(BAN_MESSAGE);
        }
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        for (int i = 0; i < 3; i++) {
            ItemStack result = event.getContents().getItem(i);

            if (result != null && result.getType().name().contains("POTION")) {
                if (isPotionBanned(result)) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}