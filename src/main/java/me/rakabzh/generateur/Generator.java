package me.rakabzh.generateur;

import me.rakabzh.Main;
import me.rakabzh.State;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Generator extends BukkitRunnable {

    Main main;
    Location location;
    int state;

    int timer;

    // Système de limite pour chaque type d'item
    private int maxIronItems;
    private int maxGoldItems;
    private int maxEmeraldItems;

    private int latenceIron;
    private int latenceGold;
    private int latenceEmerald;

    private int currentIronCount;
    private int currentGoldCount;
    private int currentEmeraldCount;

    private double checkRadius; // Rayon de vérification autour du générateur

    public Generator(Main main, Location location) {
        this.main = main;
        this.location = location;
        this.state = 0;
        this.timer = 0;
        this.maxIronItems = 32;
        this.maxGoldItems = 16;
        this.maxEmeraldItems = 0;
        this.latenceIron = 1;
        this.latenceGold = 4;
        this.latenceEmerald = 1;
        this.currentIronCount = 0;
        this.currentGoldCount = 0;
        this.currentEmeraldCount = 0;
        this.checkRadius = 5.0;
    }

    @Override
    public void run() {
        updateItemCounts();

        World world = location.getWorld();

        if (timer % latenceIron == 0 && currentIronCount < maxIronItems) {
            ItemStack ironItem = new ItemStack(Material.IRON_INGOT, 1);
            world.dropItemNaturally(location, ironItem);
        }

        if (timer % latenceGold == 0 && currentGoldCount < maxGoldItems) {
            ItemStack goldItem = new ItemStack(Material.GOLD_INGOT, 1);
            world.dropItemNaturally(location, goldItem);
        }

        if (timer % latenceEmerald == 0 && currentEmeraldCount < maxEmeraldItems) {
            ItemStack emerald = new ItemStack(Material.EMERALD, 1);
            world.dropItemNaturally(location, emerald);
        }

        if (!main.isState(State.PLAYING)) {
            cancel();
        }
        timer++;
    }

    /**
     * Met à jour les compteurs d'items en comptant chaque type dans la zone
     */
    private void updateItemCounts() {
        World world = location.getWorld();
        if (world == null) return;

        currentIronCount = 0;
        currentGoldCount = 0;
        currentEmeraldCount = 0;

        // Compter tous les items dans un rayon défini autour du générateur
        for (Entity entity : world.getNearbyEntities(location, checkRadius, checkRadius, checkRadius)) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                Material itemType = item.getItemStack().getType();
                int amount = item.getItemStack().getAmount();

                switch (itemType) {
                    case IRON_INGOT:
                        currentIronCount += amount;
                        break;
                    case GOLD_INGOT:
                        currentGoldCount += amount;
                        break;
                    case EMERALD:
                        currentEmeraldCount += amount;
                        break;
                }
            }
        }
    }

    // Getters et setters pour la configuration
    public Location getLocation() {
        return location;
    }

    public void addState(int state) {
        this.state++;
        maxEmeraldItems = 8;
    }
}