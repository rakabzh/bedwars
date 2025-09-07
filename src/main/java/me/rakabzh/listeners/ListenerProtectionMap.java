package me.rakabzh.listeners;

import me.rakabzh.Main;
import me.rakabzh.PlayerBedwars;
import me.rakabzh.State;
import me.rakabzh.constants.Messages;
import me.rakabzh.nmsSimple.ProtocolManager;
import me.rakabzh.villagers.ItemShop;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ListenerProtectionMap implements Listener {
    private final Main main;


    public ListenerProtectionMap(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Player player = event.getPlayer();
        if (block.getType() == Material.BED_BLOCK) {
            PlayerBedwars playerNoBed = null;
            for (PlayerBedwars playerBedwars : main.getPlayerList()) {
                if ((playerBedwars.getLocationBedFoot().getX() == block.getLocation().getX() &&
                        playerBedwars.getLocationBedFoot().getZ() == block.getLocation().getZ())||
                        (playerBedwars.getLocationBedHead().getX() == block.getLocation().getX() &&
                                playerBedwars.getLocationBedHead().getZ() == block.getLocation().getZ())) {
                    playerNoBed = playerBedwars;
                    break;
                }
            }
            if (playerNoBed == null){
                System.out.println("probleme no detection bed");
                System.out.println("location block = " + block.getLocation());
                System.out.println("location reel bed { ");
                for (PlayerBedwars playerBedwars : main.getPlayerList()) {
                    System.out.println(playerBedwars.getLocationBedFoot() + " / " + playerBedwars.getLocationBedHead());
               }
                System.out.println("}");
                return;
            }
            if (playerNoBed == main.getPlayerWithPlayer(player)){
                event.setCancelled(true);
                player.sendMessage(Messages.BED_BREAK_HIMSELF.getMessage());
            }
            else {
                main.getPlayerWithPlayer(playerNoBed.getPlayer()).bedBreak();
                Bukkit.broadcastMessage("§fLIT DéTRUIT > " + playerNoBed.getPrefixColor() + "Lit " + playerNoBed.getColor() + "§7 vient d'être cassé par " + main.getPlayerWithPlayer(player).getPrefixColor()+player.getName()+" §7!");
                playerNoBed.getPlayer().sendMessage(Messages.BED_REAL_BREAK.getMessage() + main.getPlayerWithPlayer(player).getPrefixColor()+player.getName()+" §7!");
                event.setCancelled(false);
            }
        }



        else {
            if (Main.getINSTANCE().getBlocksPlaceByPlayer().contains(block) || event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                event.setCancelled(false);
                Main.getINSTANCE().getBlocksPlaceByPlayer().remove(block);
            }
            else {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Messages.BLOCK_MAP_BREAK.getMessage());
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        if (block.getType().equals(Material.TNT)){
            block.setType(Material.AIR);
            TNTPrimed tnt = event.getBlockPlaced().getWorld().spawn(
                    event.getBlockPlaced().getLocation().add(0.5, 0, 0.5),
                    TNTPrimed.class
            );
            tnt.setFuseTicks(80);
            return;
        }
        main.getINSTANCE().getBlocksPlaceByPlayer().add(block);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {

        Entity entity = event.getEntity();
        if (entity instanceof Golem){
            event.setCancelled(false);
            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                entity.remove();
            }, 15*20L);
            return;
        }
        else if (entity instanceof Silverfish){
            event.setCancelled(false);

            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                entity.remove();
            }, 15*20L);
            return;
        }
        else if (entity instanceof Item) {
            Material material = ((Item) entity).getItemStack().getType();
            if (material == Material.BED) {
                event.setCancelled(true);
            }
        }
        else if (!(entity instanceof Player) && !(entity instanceof Villager)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!main.isState(State.PLAYING)) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (ProtocolManager.hasHiddenArmor(player) && event.getDamager() instanceof Player){
                ProtocolManager.showArmorForPlayer(player);
            }
            if (player.getHealth() <= event.getDamage()) {
                player.setHealth(20);
                PlayerBedwars victim = main.getPlayerWithPlayer(player);
                String message = " §7s'est fait tuer par ";
                if (event.getDamager() instanceof Player){
                    PlayerBedwars killer = main.getPlayerWithPlayer((Player) event.getDamager());
                    message += killer.getPrefixColor() + ((Player) event.getDamager()).getName();
                    if (!victim.haveBed()){
                        message += " §bFINAL KILL !";
                    }
                }
                else {
                    message += event.getDamager().getName();
                }
                Bukkit.broadcastMessage(victim.getPrefixColor() + player.getName() + message);

                main.setDied(player);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!main.isState(State.PLAYING)) {
            event.setCancelled(true);
            return;
        }
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.getHealth() <= event.getDamage()) {
                player.setHealth(20);
                PlayerBedwars victim = main.getPlayerWithPlayer(player);
                Bukkit.broadcastMessage(victim.getPrefixColor() + player.getName() + " §7est mort je ne sais pas comment");
                main.setDied(player);
            }
        } else if (event.getEntity() instanceof Villager) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void repairBow(EntityShootBowEvent e){
        if(e.getEntity() instanceof Player){
            e.getBow().setDurability((short) -1);
            ((Player) e.getEntity()).updateInventory();
        }
    }

    @EventHandler
    public void repairWeapons(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player){
            ((Player) e.getDamager()).getInventory().getItemInHand().setDurability((short) -1);
            ((Player) e.getDamager()).updateInventory();
        }else if(e.getEntity() instanceof Player){
            if (e.getDamager() instanceof Fireball){
                e.setCancelled(true);
            }
            ItemStack[] armor = ((Player) e.getEntity()).getInventory().getArmorContents();
            for(ItemStack i : armor){
                i.setDurability((short)-1);
            }
            ((Player) e.getEntity()).updateInventory();
        }
    }

    @EventHandler
    public void onEntityPickupItemEvent(PlayerPickupItemEvent event) {
        Material material = event.getItem().getItemStack().getType();
        Player player = event.getPlayer();
        List<Material> swordList = Arrays.asList(Material.STONE_SWORD, Material.IRON_SWORD, Material.DIAMOND_SWORD);
        if (material.equals(Material.WOOD_SWORD) || material.equals(Material.STONE_SWORD) || material.equals(Material.IRON_SWORD) || material.equals(Material.DIAMOND_SWORD)){
            if (player.getInventory().getItem(0).getType().equals(Material.WOOD_SWORD) || swordList.indexOf(material) > swordList.indexOf(player.getInventory().getItem(0).getType())) {
                main.getPlayerWithPlayer(player).putSword(event.getItem().getItemStack());
                event.setCancelled(true);
                event.getItem().getItemStack().setType(Material.AIR);
            }
        }
        else {
            ItemMeta meta = event.getItem().getItemStack().getItemMeta();
            for (ItemShop itemShop : main.getItemShopList()) {
                if (itemShop.getItem().getType().equals(material)){
                    meta.setDisplayName(null);
                    if (itemShop.getItem().getType().equals(Material.POTION)){
                        List<String> lore = new ArrayList<>();
                        for (String line : itemShop.wrapText(itemShop.getDescription(), 35)) {
                            lore.add("§7" + line);
                        }
                        meta.setLore(lore);
                    }
                    event.getItem().getItemStack().setItemMeta(meta);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Item item = event.getItemDrop();
        Material material = item.getItemStack().getType();
        if (main.isState(State.WAITING) || armorMaterials.contains(material) || material.equals(Material.WOOD_SWORD)) {
            event.setCancelled(true);
        } else {
            event.setCancelled(false);
            event.getItemDrop().getItemStack().removeEnchantment(Enchantment.DAMAGE_ALL);
            if (material.equals(Material.STONE_SWORD) || material.equals(Material.DIAMOND_SWORD) || material.equals(Material.IRON_SWORD)) {
                main.getPlayerWithPlayer(event.getPlayer()).putSword(new ItemStack(Material.WOOD_SWORD));
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    private final Set<Material> armorMaterials = new HashSet<>(Arrays.asList(
            Material.LEATHER_HELMET,
            Material.LEATHER_CHESTPLATE,
            Material.LEATHER_LEGGINGS,
            Material.LEATHER_BOOTS,
            Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_CHESTPLATE,
            Material.CHAINMAIL_LEGGINGS,
            Material.CHAINMAIL_BOOTS,
            Material.IRON_HELMET,
            Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS,
            Material.IRON_BOOTS,
            Material.GOLD_HELMET,
            Material.GOLD_CHESTPLATE,
            Material.GOLD_LEGGINGS,
            Material.GOLD_BOOTS,
            Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE,
            Material.DIAMOND_LEGGINGS,
            Material.DIAMOND_BOOTS
    ));
}