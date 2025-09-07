package me.rakabzh.tasks;

import me.rakabzh.PlayerBedwars;
import me.rakabzh.generateur.Generator;
import me.rakabzh.generateur.GeneratorPublic;
import me.rakabzh.Main;
import me.rakabzh.State;
import me.rakabzh.villagers.InventoryShop;
import org.bukkit.*;
import org.bukkit.block.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;

public class Autostart extends BukkitRunnable {
    private final Main main;
    public int timer = 10;
    public ArrayList<GeneratorPublic> generatorList = new ArrayList<>();

    public Autostart(Main main){
        this.main = main;
    }

    @Override
    public void run() {

        World world = main.getPlayerList().get(0).getPlayer().getWorld();


        for (Player pls : Bukkit.getOnlinePlayers()) {
            pls.setLevel(timer);
            pls.setExp(timer);
            if (timer == 10 || timer <= 5) {
                pls.sendTitle("§c" + timer + "s", "");
            }

        }
        if (timer == 10 || timer <= 5) {
            Bukkit.broadcastMessage("Lancement du jeu dans : §e " + timer + "s.");
        }

        if (timer == 0) {
            cancel();
            main.setState(State.PLAYING);

            for (Entity entity : world.getEntities()) {
                if (!(entity instanceof Player)) {
                    entity.remove();
                }
            }

            for (int i = 0; i < main.getLocationsVillagerShop().size(); i++) {
                Color color = main.getVraiColor(main.getColorTeam()[i]);
                Location loc_shop = main.getLocationsVillagerShop().get(color);
                Location loc_upgrade = main.getLocationsVillagerUpgrade().get(color);

                Villager villager_shop = (Villager) world.spawnEntity(loc_shop, EntityType.VILLAGER);
                villager_shop.setCustomName("§aSHOP");
                villager_shop.setCustomNameVisible(true);

                Villager villager_upgrade = (Villager) world.spawnEntity(loc_upgrade, EntityType.VILLAGER);
                villager_upgrade.setCustomName("§aUPGRADE");
                villager_upgrade.setCustomNameVisible(true);
            }

            for (Chunk chunk : world.getLoadedChunks()) {
                for (int x = 0; x < 16; x++) {
                    for (int y = 0; y < world.getMaxHeight(); y++) {
                        for (int z = 0; z < 16; z++) {
                            Block block = chunk.getBlock(x, y, z);
                            Material type = block.getType();

                            if (type == Material.CHEST || type == Material.TRAPPED_CHEST) {
                                BlockState state = block.getState();
                                if (state instanceof Chest) {
                                    Chest chest = (Chest) state;
                                    chest.getInventory().clear();
                                }
                            }
                        }
                    }
                }
            }
            InventoryShop inventoryShop = new InventoryShop(main, main.getPlayerList().get(0).getPlayer());
            inventoryShop.start();

            for (Location location : main.getLocationsGenerateurPublic()) {
                Material material = Material.DIAMOND;
                if (location.getX() != 32 && location.getX() != -32){
                    material = Material.EMERALD;
                }
                GeneratorPublic generatorPublic = new GeneratorPublic(location,main,material);
                generatorPublic.runTaskTimer(main,0L,20L);
                generatorList.add(generatorPublic);
            }

            for (int i = 0; i < main.getPlayerList().size(); i++) {
                Player player = main.getPlayerList().get(i).getPlayer();
                Location spawn = main.getPlayerWithPlayer(player).getLocationSpawn();

                player.getInventory().clear();
                player.setGameMode(GameMode.SURVIVAL);
                main.getPlayerWithPlayer(player).putAllPermenant();
                main.getPlayerWithPlayer(player).putBed();
                player.getInventory().setItem(0, new ItemStack(Material.WOOD_SWORD));
                player.updateInventory();

                player.getEnderChest().clear();

                Generator generator = new Generator(main, main.getPlayerWithPlayer(player).getLocationGenerateur());
                generator.runTaskTimer(main, 0L, 20L);
                main.getPlayerWithPlayer(player).setGenerator(generator);

                player.teleport(spawn);

            }

            Bukkit.getScheduler().runTaskTimer(main,() -> {
                main.addTimer();
                switch (main.getTimer()){
                    case 5*60:
                        for (int i = 0; i < generatorList.size(); i++) {
                            if (generatorList.get(i).getMaterial().equals(Material.DIAMOND)){
                                generatorList.get(i).setState(1);
                            }
                        }
                        break;
                    case 11*60:
                        for (int i = 0; i < generatorList.size(); i++) {
                            if (generatorList.get(i).getMaterial().equals(Material.EMERALD)){
                                generatorList.get(i).setState(1);
                            }
                        }
                        break;
                    case 17*60:
                        for (int i = 0; i < generatorList.size(); i++) {
                            if (generatorList.get(i).getMaterial().equals(Material.DIAMOND)){
                                generatorList.get(i).setState(2);
                            }
                        }
                        break;
                    case 23*60:
                        for (int i = 0; i < generatorList.size(); i++) {
                            if (generatorList.get(i).getMaterial().equals(Material.EMERALD)){
                                generatorList.get(i).setState(2);
                            }
                        }
                        break;
                    case 29*60:
                        for (int i = 0; i < main.getPlayerList().size(); i++) {
                            main.getPlayerList().get(i).bedBreak();
                            world.getBlockAt(main.getPlayerList().get(i).getLocationBedFoot()).setType(Material.AIR);
                            world.getBlockAt(main.getPlayerList().get(i).getLocationBedHead()).setType(Material.AIR);
                        }
                        break;
                    case 49*60:
                        FinishCycle finishCycle = new FinishCycle(main);
                        finishCycle.runTaskTimer(main,0,20);
                        break;
                }
            },0,20);

            main.getINSTANCE().setupdateAllPlayerScoreboard();
            Bukkit.broadcastMessage("§a§l---------------------------------------------\n" +
                    "§f§l                       Bed wars                        \n§l\n" +
                    "§e§l  Protégez votre lit et détruisez les lits ennemis.   \n" +
                    "   Améliorez vous et votre équipe en collectant       \n" +
                    "    du fer, de l'or, de l'émeraude et du diamant      \n" +
                    "  provenant des générateurs pour accéder à de         \n" +
                    "             puissantes améliorations.                \n§l\n" +
                    "§a§l---------------------------------------------");
        }
        timer--;
    }
}
