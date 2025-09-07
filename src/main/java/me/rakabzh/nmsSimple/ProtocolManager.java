package me.rakabzh.nmsSimple;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class ProtocolManager {

    private static ProtocolManager instance;
    private final Set<UUID> playersWithHiddenArmor = new HashSet<>();

    private static final int SLOT_HELMET = 4;      // Casque
    private static final int SLOT_CHESTPLATE = 3;  // Plastron
    private static final int SLOT_LEGGINGS = 2;    // Jambières
    private static final int SLOT_BOOTS = 1;
    public ProtocolManager(JavaPlugin plugin) {
        instance = this;
        // Intercepter les packets d'équipement sortants
        ProtocolLibrary.getProtocolManager().addPacketListener(
                new PacketAdapter(plugin, PacketType.Play.Server.ENTITY_EQUIPMENT) {
                    @Override
                    public void onPacketSending(PacketEvent event) {
                        hideArmorInPacket(event);
                    }
                }
        );
    }

    private void hideArmorInPacket(PacketEvent event) {
        try {
            Object rawPacket = event.getPacket().getHandle();

            // Récupérer l'entity ID pour savoir de quel joueur il s'agit
            Field entityIdField = rawPacket.getClass().getDeclaredField("a");
            entityIdField.setAccessible(true);
            int entityId = (int) entityIdField.get(rawPacket);

            // Trouver le joueur correspondant à cet entity ID
            Player targetPlayer = null;
            for (Player player : event.getPlayer().getWorld().getPlayers()) {
                if (player.getEntityId() == entityId) {
                    targetPlayer = player;
                    break;
                }
            }

            // Si ce joueur n'a pas l'armure cachée, on ne fait rien
            if (targetPlayer == null || !playersWithHiddenArmor.contains(targetPlayer.getUniqueId())) {
                return;
            }

            // Cacher l'armure pour ce joueur
            Field slotField = rawPacket.getClass().getDeclaredField("b");
            Field itemField = rawPacket.getClass().getDeclaredField("c");

            int slot = (int) slotField.get(rawPacket);

            slotField.setAccessible(true);
            itemField.setAccessible(true);
            if (isArmorSlot(slot)) {
                Object airItemStack = craftItemStackToNMS(new ItemStack(Material.AIR));
                itemField.set(rawPacket, airItemStack);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si le slot correspond à un slot d'armure
     * @param slot Le numéro du slot
     * @return true si c'est un slot d'armure, false sinon
     */
    private boolean isArmorSlot(int slot) {
        return slot == SLOT_HELMET ||
                slot == SLOT_CHESTPLATE ||
                slot == SLOT_LEGGINGS ||
                slot == SLOT_BOOTS;
    }

    // Méthodes publiques pour contrôler la visibilité
    public static void hideArmorForPlayer(Player player) {
        if (instance != null) {
            instance.playersWithHiddenArmor.add(player.getUniqueId());
            // Rafraîchir l'équipement pour tous les autres joueurs
            refreshPlayerEquipment(player);
        }
    }

    public static void showArmorForPlayer(Player player) {
        if (instance != null) {
            instance.playersWithHiddenArmor.remove(player.getUniqueId());
            // Rafraîchir l'équipement pour tous les autres joueurs
            refreshPlayerEquipment(player);
        }
    }

    public static boolean hasHiddenArmor(Player player) {
        return instance != null && instance.playersWithHiddenArmor.contains(player.getUniqueId());
    }

    private static void refreshPlayerEquipment(Player player) {
        // Force le rafraîchissement en renvoyant les packets d'équipement
        for (Player online : player.getWorld().getPlayers()) {
            if (!online.equals(player)) {
                online.hidePlayer(player);
                online.showPlayer(player);
            }
        }
    }

    // Méthode utilitaire pour convertir ItemStack Bukkit vers NMS
    private Object craftItemStackToNMS(ItemStack item) {
        try {
            Class<?> craftItemStackClass = Class.forName("org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack");
            return craftItemStackClass.getMethod("asNMSCopy", ItemStack.class).invoke(null, item);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}