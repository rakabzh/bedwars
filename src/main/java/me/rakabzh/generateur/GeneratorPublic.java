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

public class GeneratorPublic extends BukkitRunnable {

    Main main;
    Location location;
    int state;

    int timer;

    private Material material;

    private double checkRadius;

    public GeneratorPublic(Location location, Main main, Material material) {
        this.location = location;
        location.setY(location.getBlockY()+2);
        this.main = main;
        this.state = 0;
        this.timer = 0;
        this.checkRadius = 5.0;
        this.material = material;
    }

    @Override
    public void run() {
        World world = location.getWorld();

        int time = 30;
        if (material.equals(Material.EMERALD)) {
            time = 65;
        }
        if (state == 1) {
            if (material.equals(Material.EMERALD)) {
                time = 45;
            } else {
                time = 20;
            }
        }
        if (state == 2) {
            if (material.equals(Material.EMERALD)) {
                time = 30;
            } else {
                time = 10;
            }
        }

        if (timer % time == 0) {
            ItemStack item = new ItemStack(material, 1);
            world.dropItemNaturally(location, item);
        }

        if (!main.isState(State.PLAYING)) {
            cancel();
        }
        timer++;
    }

    // Getters et setters pour la configuration
    public Location getLocation() {
        return location;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Material getMaterial() {
        return material;
    }
}