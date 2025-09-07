package me.rakabzh.tasks;

import me.rakabzh.Main;
import me.rakabzh.PlayerBedwars;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Iterator;

public class FinishCycle extends BukkitRunnable {
    private Main main;

    public final int duree = 5;
    public int timer = duree;

    public FinishCycle(Main main) {
        this.main = main;
    }

    @Override
    public void run() {
        if (timer == duree) {
            if (main.getINSTANCE().getBlocksPlaceByPlayer() == null) return;
            for (Iterator<Block> it = main.getINSTANCE().getBlocksPlaceByPlayer().iterator(); it.hasNext(); ) {
                Block block = it.next();
                block.setType(Material.AIR);
                it.remove();
            }
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                onlinePlayer.setGameMode(GameMode.ADVENTURE);
            }
            Bukkit.broadcastMessage("§4FIN DE LA GAME");
        }
        if (timer == 0) {
            for (PlayerBedwars playerBedwars : main.getPlayerList()){
                playerBedwars.getLocationBedFoot().getBlock().setType(Material.AIR);
                playerBedwars.getLocationBedHead().getBlock().setType(Material.AIR);

                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);

                try {
                    out.writeUTF("Connect");
                    out.writeUTF("lobby");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                playerBedwars.getPlayer().sendPluginMessage(main, "BungeeCord", b.toByteArray());
            }
            for (Block block : main.getBlocksPlaceByPlayer()) {
                main.getPlayerList().get(0).getPlayer().getWorld().getBlockAt(block.getLocation()).setType(Material.AIR);
            }
            Bukkit.getServer().shutdown();
            cancel();
        }
        timer--;
    }
}