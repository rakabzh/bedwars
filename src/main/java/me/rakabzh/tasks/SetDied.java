package me.rakabzh.tasks;

import me.rakabzh.Main;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class SetDied extends BukkitRunnable {
    private final Main main;
    private final Player player;
    public int timer = 5;

    public SetDied(Main main, Player player){
        this.main = main;
        this.player = player;
    }
    @Override
    public void run() {
        if(timer == 5) {
            player.setGameMode(GameMode.SPECTATOR);
            player.teleport(new Location(player.getWorld(), 0,100,0));
            player.getInventory().clear();
            player.getEquipment().clear();
            player.getEquipment().setLeggings(null);
            player.getEquipment().setBoots(null);
            player.getEquipment().setChestplate(null);
            player.getEquipment().setHelmet(null);
            player.updateInventory();
        }
        if (main.getPlayerWithPlayer(player).getState() == 0) {
            if (timer == 0) {
                player.setFoodLevel(20);
                player.setHealth(20);
                player.setGameMode(GameMode.SURVIVAL);
                main.getPlayerWithPlayer(player).downgradeTools();
                main.getPlayerWithPlayer(player).putAllPermenant();
                main.getPlayerWithPlayer(player).putSword(new ItemStack(Material.WOOD_SWORD));
                player.updateInventory();
                player.teleport(main.getPlayerWithPlayer(player).getLocationSpawn());
                cancel();
            }
            player.sendTitle("§c" + timer + "s", "");
        }
        else {
            player.sendTitle("§4 Vous avez perdu", "§cTu es un loseur");
            main.removePlayerList(main.getPlayerWithPlayer(player));
            main.checkWin();
            cancel();
        }
        timer--;
    }
}
