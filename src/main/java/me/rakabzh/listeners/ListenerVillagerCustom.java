package me.rakabzh.listeners;

import me.rakabzh.Main;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ListenerVillagerCustom implements Listener {
    private final Main main;
    public ListenerVillagerCustom(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent    event) {
        Player player = event.getPlayer();
        if (event.getRightClicked() instanceof Villager) {
            Villager v = (Villager) event.getRightClicked();
            if (v.getCustomName().equals("§aSHOP")) {
                event.setCancelled(true);
                main.getPlayerWithPlayer(player).getInventoryShop().menu_du_jeu();
                player.updateInventory();
            }
            if (v.getCustomName().equals("§aUPGRADE")){
                event.setCancelled(true);
                main.getPlayerWithPlayer(player).getInventoryUpgrade().menu_du_jeu();
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack current = event.getCurrentItem();
        Player player = (Player) event.getWhoClicked();

        if (inventory.getName().equalsIgnoreCase("Buy") || main.getPlayerWithPlayer(player).getInventoryShop().getListNameMenuBar().contains(inventory.getName())){
            main.getPlayerWithPlayer(player).getInventoryShop().manageMenuShopInventory(current);
            event.setCancelled(true);
        }
        if (inventory.getName().equalsIgnoreCase("Upgrades and Traps")){
            main.getPlayerWithPlayer(player).getInventoryUpgrade().manageMenuShopInventory(current);
            event.setCancelled(true);
        }
    }
}
