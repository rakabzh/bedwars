package me.rakabzh.listeners;

import me.rakabzh.Main;
import me.rakabzh.PlayerBedwars;
import me.rakabzh.State;
import me.rakabzh.constants.Messages;
import me.rakabzh.tasks.Autostart;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class Listener implements org.bukkit.event.Listener {
    private final Main main;
    private Autostart autostart;

    public Listener(Main main) {
        this.main = main;
        this.autostart = new Autostart(main);
    }

    public ItemStack leavebed(){
        ItemStack leavebed = new ItemStack(Material.BED);
        ItemMeta leavebedMeta = leavebed.getItemMeta();
        leavebedMeta.setDisplayName("§cRetourner au Lobby");
        leavebedMeta.setLore(Arrays.asList("§7Clique pour quitter vers me lobby !"));
        leavebed.setItemMeta(leavebedMeta);
        return leavebed;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        if (main.isState(State.CONFIG) && !event.getPlayer().isOp()){
            event.getPlayer().kickPlayer("§cLe serveur est en cours de configuration");
            event.setJoinMessage("");
            return;
        }
        Player player = event.getPlayer();
        main.getINSTANCE().getScoreboardManager().onLogin(player);
        if(main.isState(State.WAITING) && main.getPlayerList().size() < 8) {
            event.setJoinMessage("§e " + event.getPlayer().getName() + " §3joined (§e" + Bukkit.getServer().getOnlinePlayers().size() + "§3/8)!");
            player.teleport(new Location(Bukkit.getWorld("world"), 0, 99, 0, 0f, 0f));
            player.setGameMode(GameMode.ADVENTURE);
            player.setFoodLevel(20);
            player.setHealth(20);
            player.setLevel(0);
            player.getInventory().clear();
            player.getEquipment().clear();
            player.getEquipment().setLeggings(null);
            player.getEquipment().setBoots(null);
            player.getEquipment().setChestplate(null);
            player.getEquipment().setHelmet(null);
            ItemStack leavebed = leavebed();
            player.getInventory().setItem(8, leavebed);

            PlayerBedwars playerBedwars = new PlayerBedwars(player, main.getColorTeam()[main.getPlayerList().size()], main.getLocationsSpawnerGenerateur().get(main.getVraiColor(main.getColorTeam()[main.getPlayerList().size()])), main);

            main.getPlayerList().add(playerBedwars);
        }

        if (!main.isState(State.WAITING)){
            player.setGameMode(GameMode.SPECTATOR);
            player.sendMessage(Messages.IS_IN_PLAYING.getMessage());
            event.setJoinMessage(null);
            return;
        }

        //if(!main.getPlayerList().contains(player)) main.getPlayerList().add(player);

        if(main.isState(State.WAITING) && Bukkit.getOnlinePlayers().size() >= 2){
            autostart = new Autostart(main);
            autostart.runTaskTimer(main,0,20);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        main.getINSTANCE().getScoreboardManager().onLogout(player);
        main.removePlayerList(main.getPlayerWithPlayer(player));
        if(main.getPlayerList().size() <= 1 && main.isState(State.WAITING) && autostart.timer != 10) {
            autostart.cancel();
            autostart.timer = 10;
            Bukkit.getScheduler().cancelAllTasks();
            for (Player pls : Bukkit.getOnlinePlayers()) {
                pls.sendTitle("§cAttente de nouveau Joueur","");
            }
            System.out.println("autostart probleme");
        }
        System.out.println(main.getPlayerList().size() + " / " + main.getState());
        if(!main.isState(State.WAITING) && !main.isState(State.CONFIG)) {
            main.checkWin();
        }
    }

    @EventHandler
    public void bedLeaveBedwars(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        if (!main.isState(State.PLAYING) && item.getItemMeta().getDisplayName() != null) {
            if (item.getType() == Material.BED && item.getItemMeta().getDisplayName().equalsIgnoreCase("§cRetourner au Lobby")) {
                ByteArrayOutputStream b = new ByteArrayOutputStream();
                DataOutputStream out = new DataOutputStream(b);

                try {
                    out.writeUTF("Connect");
                    out.writeUTF("lobby");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                player.sendPluginMessage(main, "BungeeCord", b.toByteArray());
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(player.getLocation().getY() <= 0){
            PlayerBedwars pb = main.getPlayerWithPlayer(player);
            Bukkit.broadcastMessage(pb.getPrefixColor() + player.getName() + "§7 est tombé dans le vide");
            main.getINSTANCE().setDied(player);
        }
    }
}
