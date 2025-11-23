package de.jozelot.varoX.utils;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutPlayerListHeaderFooter;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.List;

public class TabListUtil {

    public static void setHeaderFooter(Player player, List<String> headerLines, List<String> footerLines) {

        String header = String.join("\n", headerLines);
        String footer = String.join("\n", footerLines);

        CraftPlayer craftplayer = (CraftPlayer) player;

        String coloredHeader = ChatColor.translateAlternateColorCodes('&', header);
        String coloredFooter = ChatColor.translateAlternateColorCodes('&', footer);

        String jsonHeader = "{\"text\":\"" + coloredHeader + "\"}";
        String jsonFooter = "{\"text\":\"" + coloredFooter + "\"}";

        IChatBaseComponent headerComponent = IChatBaseComponent.ChatSerializer.a(jsonHeader);
        IChatBaseComponent footerComponent = IChatBaseComponent.ChatSerializer.a(jsonFooter);

        PacketPlayOutPlayerListHeaderFooter packet = new PacketPlayOutPlayerListHeaderFooter();

        try {
            Field headerField = PacketPlayOutPlayerListHeaderFooter.class.getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, headerComponent);

            Field footerField = PacketPlayOutPlayerListHeaderFooter.class.getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, footerComponent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        craftplayer.getHandle().playerConnection.sendPacket(packet);
    }
}
